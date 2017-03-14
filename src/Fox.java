import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Foxes can display themselves
 */
public class Fox extends FieldOccupant implements Runnable
{

   public Fox(int x, int y, boolean initLock)
   {
      super(x, y, initLock);
   }


   /**
    * @return the color to use for a cell occupied by a Fox
    */
   @Override public Color getDisplayColor()
   {
      return Color.green;
   } // getDisplayColor


   /**
    * @return the text representing a Fox
    */
   @Override public String toString()
   {
      return "F";
   }


   public void run()
   {
      // Declare Variables
      boolean dead = false;
      FieldOccupant[] neighbors;
      FieldOccupant[] emptyCellsNeighbors;
      AtomicBoolean neighborLock;
      AtomicBoolean secondFoxLock;
      int numHounds;
      boolean completedAction;
      FieldOccupant newFox;

      // Run while the fox has not been eaten
      while (!dead)
      {
         if (!Simulation.hasSimulationStarted())
         {
            completedAction = false;

            // Get the neighboring cells
            neighbors = getNeighborsArray();

            // Iterate of the neighbors
            for (int i = 0; i < neighbors.length && !completedAction; i++)
            {
               // Check if the neighbor is an Empty Object
               if (neighbors[i] instanceof Empty)
               {

                  // Try to get and lock the empty cell object
                  neighborLock = neighbors[i].getAndLock();

                  // Check if the lock was aquired
                  if (neighborLock != null)
                  {

                     // Get the neighbors of the Empty Cell
                     emptyCellsNeighbors = neighbors[i].getNeighborsArray();

                     // Reset the numHounds counter
                     numHounds = -1;

                     // Iterate of the neighbors of the empty cell or if 2 hounds are found adjacent
                     for (int j = 0;
                          j < emptyCellsNeighbors.length && numHounds <= 1
                                && !completedAction; j++)
                     {
                        // Check the number of neighboring hounds the first time through
                        if (numHounds == -1)
                        {
                           // Set the numHounds after checking
                           numHounds = numNeighboringHounds(neighbors[i].getX(),
                                 neighbors[i].getY());
                        }

                        // Check for another fox
                        if (emptyCellsNeighbors[j] instanceof Fox)
                        {
                           // Try to get the lock for this nextCell
                           secondFoxLock = emptyCellsNeighbors[j].getAndLock();

                           // Check that the lock for the second fox was aquired
                           if (secondFoxLock != null)
                           {
                              // Check if this is a different fox than the original
                              if (this != emptyCellsNeighbors[j])
                              {
                                 // Create a new Fox Object with the lock on
                                 newFox = new Fox(neighbors[i].getX(),
                                       neighbors[i].getY(), true);

                                 // Start the fox thread
                                 new Thread((Fox) newFox).start();

                                 // Put a new fox in the empty cell and exit the for loop
                                 Simulation._theField
                                       .setOccupantAt(neighbors[i].getX(),
                                             neighbors[i].getY(), newFox);

                                 // Set the completedAction boolean to exit the loop
                                 completedAction = true;

                                 // Release the new foxes lock to make it available
                                 newFox._lock.getAndSet(false);

                                 // Set the Boolean to redraw the field
                                 Field._redrawField.getAndSet(true);
                              }
                              // Release the lock of the second fox
                              secondFoxLock.getAndSet(false);
                           }
                           else
                           // Failed to acquire a lock on the fox
                           {
                              // Set the completedAction to get out of the loops
                              completedAction = true;
                           }
                        }
                     }
                     // Set the lock of the empty cell back to false
                     neighborLock.getAndSet(false);
                  }
               }
            }
         }

         // The fox is done doing things so it sleeps
         try
         {
            threadSleep();
         }
         catch (InterruptedException e)
         {
            dead = true;
         }
      }

   }
}
