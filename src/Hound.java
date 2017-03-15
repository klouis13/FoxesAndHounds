import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hounds can display themsevles. They also get hungry
 */
public class Hound extends FieldOccupant implements Runnable
{
   // Declare instance variables
   private static int _houndStarveTime;
   private        int _hungerLevel;

   // Default starve time for Hounds
   public static final int DEFAULT_STARVE_TIME = 3;


   /**
    * Create a hound
    */
   public Hound(int x, int y, boolean initLock)
   {
      super(x, y, initLock);

      // Convert _houndStarveTime to ms
      _houndStarveTime = _houndStarveTime * 1000;

      _hungerLevel = _houndStarveTime;
   }


   /**
    * @return the color to use for a cell occupied by a Hound
    */
   @Override public Color getDisplayColor()
   {
      return Color.red;
   } // getDisplayColor


   /**
    * @return the text representing a Hound
    */
   @Override public String toString()
   {
      return "H";
   }


   /**
    * Sets the starve time for this class
    *
    * @param starveTime
    */
   public static void setStarveTime(int starveTime)
   {
      _houndStarveTime = starveTime;
   }


   public void run()
   {
      FieldOccupant[] neighbors;
      FieldOccupant[] otherNeighbors;
      boolean completedAction;
      AtomicBoolean neighborLock;
      AtomicBoolean otherNeighborLock;
      FieldOccupant newOccupant;
      int[] numNeighborTypes;

      try
      {
         Simulation._simulationStarted.await();
      }
      catch (InterruptedException e)
      {

      }

      // Run while the Hound has not starved
      while (_hungerLevel > 0)
      {
         completedAction = false;

         while (!completedAction)
         {
            // Get the neighbors of the Hound
            neighbors = getNeighborsArray();

            for (int i = 0; i < neighbors.length && !completedAction; i++)
            {
               if (neighbors[i] instanceof Fox)
               {
                  // Try to acquire the lock for the neighbor
                  neighborLock = neighbors[i].lockAndGet();

                  // Check if the lock was acquired
                  if (neighborLock != null)
                  {

                     otherNeighbors = neighbors[i].getNeighborsArray();

                     for (int j = 0;
                          j < otherNeighbors.length && !completedAction; j++)
                     {
                        // Check if the cell is a hound
                        if (otherNeighbors[j] instanceof Hound)
                        {

                           // Try to get the lock
                           otherNeighborLock = otherNeighbors[j].lockAndGet();

                           // Check if the lock was acquired
                           if (otherNeighborLock != null)
                           {

                              //neighbors[i].interruptThread();

                              // Create a new Hound Object with the lock on
                              newOccupant = new Hound(neighbors[i].getX(),
                                    neighbors[i].getY(), true);

                              // Start the fox thread
                              new Thread((Hound) newOccupant).start();

                              // Put a new Hound in the Foxes cell and exit the for loop
                              Simulation._theField
                                    .setOccupantAt(neighbors[i].getX(),
                                          neighbors[i].getY(), newOccupant);

                              // Set the completedAction boolean to exit the loop
                              completedAction = true;

                              // Release the new Hound lock to make it available
                              newOccupant._lock.getAndSet(false);

                              otherNeighborLock.getAndSet(false);
                           }
                        }
                     }
                     if (!completedAction)
                     {
                        // Put a new Empty Cell in the Foxes cell and exit the for loop
                        Simulation._theField.setOccupantAt(neighbors[i].getX(),
                              neighbors[i].getY(),
                              new Empty(neighbors[i].getX(),
                                    neighbors[i].getY(), false));

                        // Set the boolean to exit the while loop
                        completedAction = true;
                     }

                     // The hound eats the fox and its hunger is gone
                     _hungerLevel = _houndStarveTime;

                     neighborLock.getAndSet(false);

                     // Set the Boolean to redraw the field
                     Field._redrawField.getAndSet(true);
                  }
               }
               else if (neighbors[i] instanceof Empty)
               {
                  otherNeighbors = neighbors[i].getNeighborsArray();

                  for (int j = 0;
                       j < otherNeighbors.length && !completedAction; j++)
                  {
                     // Try to get the lock
                     otherNeighborLock = otherNeighbors[j].lockAndGet();

                     // Check if the lock was acquired
                     if (otherNeighborLock != null)
                     {
                        // Check if the cell is a hound
                        if (otherNeighbors[j] instanceof Hound)
                        {
                           //neighbors[i].interruptThread();

                           // Create a new Hound Object with the lock on
                           newOccupant = new Hound(neighbors[i].getX(),
                                 neighbors[i].getY(), true);

                           // Start the fox thread
                           new Thread((Hound) newOccupant).start();

                           // Put a new Hound in the Foxes cell and exit the for loop
                           Simulation._theField
                                 .setOccupantAt(neighbors[i].getX(),
                                       neighbors[i].getY(), newOccupant);

                           // Set the completedAction boolean to exit the loop
                           completedAction = true;

                           // Release the new Hound lock to make it available
                           newOccupant._lock.getAndSet(false);
                        }
                        otherNeighborLock.getAndSet(false);
                     }
                  }
               }
            }
         }
         try
         {
            _hungerLevel -= threadSleep();
         }
         catch (InterruptedException e)
         {

         }

      }
   }

