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
      boolean birthedFox;
      FieldOccupant newFox;

      // Initialize Constants
      final int NUM_NEIGHBORS = 8;

         /*Set<FieldOccupant> neighbors;
         Set<FieldOccupant> emptyCellNeighbors;
         Iterator<FieldOccupant> emptyCellIterator;
         Iterator<FieldOccupant> neighborIterator;
         FieldOccupant nextNeighbor;
         FieldOccupant nextEmptyCellNeighbor;

         */
      while (!dead)
      {
         if (!Simulation.hasSimulationStarted())
         {
            //System.out.println("Running...");
            // Reset Variables
            birthedFox = false;

            // Get the neighboring cells
            neighbors = Simulation._theField
                  .getNeighborCells(super.getX(), super.getY()).toArray(new FieldOccupant[NUM_NEIGHBORS]);

            // Iterate of the neighbors
            for (int i = 0; i < neighbors.length && !birthedFox; i++)
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
                     emptyCellsNeighbors = Simulation._theField
                           .getNeighborCells(neighbors[i].getX(), neighbors[i].getY())
                           .toArray(new FieldOccupant[NUM_NEIGHBORS]);

                     // Reset the numHounds counter
                     numHounds = -1;

                     // Iterate of the neighbors of the empty cell or if 2 hounds are found adjacent
                     for (int j = 0;
                          j < emptyCellsNeighbors.length && numHounds <= 1 && !birthedFox; j++)
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
                           // Check if this is a different fox than the original
                           if (this != emptyCellsNeighbors[j])
                           {
                              // Try to get the lock for this fox
                              secondFoxLock = emptyCellsNeighbors[j].getAndLock();

                              // Check that the lock for the second fox was aquired
                              if (secondFoxLock != null)
                              {
                                 System.out.println("newFox");
                                 // Create a new Fox Object with the lock on
                                 newFox = new Fox(neighbors[i].getX(), neighbors[i].getY(), true);

                                 // Start the fox thread
                                 new Thread((Fox) newFox).start();

                                 // Put a new fox in the empty cell and exit the for loop
                                 Simulation._theField.setOccupantAt(neighbors[i].getX(),
                                       neighbors[i].getY(), newFox);

                                 // Set the birthedFox boolean to exit the loop
                                 birthedFox = true;

                                 // Release the new foxes lock to make it available
                                 newFox._lock.getAndSet(false);

                                 // Release the lock of the second fox
                                 secondFoxLock.getAndSet(false);

                                 Field._redrawField.getAndSet(true);
                              }
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
