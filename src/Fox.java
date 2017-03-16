import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Foxes can display themselves
 */
public class Fox extends FieldOccupant implements Runnable
{

   /**
    * Create a fox at x and y with the lock initialized to the boolean
    * @param x the x coordinate
    * @param y the y coordinate
    * @param initLock the initial boolean condition
    */
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
      int houndCount;
      int specificNeighborCount;
      AtomicBoolean myLock;
      AtomicBoolean neighborLock;
      AtomicBoolean secondFoxLock;
      FieldOccupant chosenNeighbor;
      FieldOccupant chosenFox;

      // Create an array to hold FieldOccupants that will need to be accessed
      // later
      FieldOccupant[] specificNeighbors = new FieldOccupant[NUM_NEIGHBORS];

      // Initialize Constants
      final int MAX_NEIGHBORING_HOUNDS = 2;

      // Wait for the simulation to start
      try
      {
         Simulation.SIMULATION_START.await();
      }
      catch (InterruptedException e)
      {

      }

      // Run while the fox has not been eaten
      while (!isThreadInterrupted())
      {
         // The fox is done doing things so it sleeps
         try
         {
            threadSleep();
         }
         catch (InterruptedException e)
         {

         }

         // Reset the counter for the FieldOccupants that need to be stored
         specificNeighborCount = 0;

         // Iterate over the neighbors
         for (FieldOccupant currentOccupant : getNeighbors())
         {
            // Check if the neighbor is an Empty Object
            if (currentOccupant instanceof Empty)
            {
               // Store the Empty objects in an array to randomly choose one
               // later
               specificNeighbors[specificNeighborCount] = currentOccupant;

               // increment the number of empty cells that you have
               specificNeighborCount++;
            }
         }
         // Check that at least 1 empty cell was found
         if (specificNeighborCount > 0)
         {
            // Chose a random empty cell from those around
            chosenNeighbor = randomOccupant(specificNeighborCount,
                  specificNeighbors);

            // Check that the thread is not interrupted
            if (!isThreadInterrupted())
            {
               // Reset the count for the neighbors
               specificNeighborCount = 0;
               houndCount = 0;

               // Iterate over the neighbors of the empty cell looking for foxes
               // and hounds
               for (FieldOccupant currentOccupant : chosenNeighbor
                     .getNeighbors())
               {
                  // Check for another fox
                  if (currentOccupant instanceof Fox && currentOccupant != this)
                  {
                     // Store a reference to the fox
                     specificNeighbors[specificNeighborCount] = currentOccupant;

                     // Increment the fox count
                     specificNeighborCount++;
                  }
                  // Check for a hound and count the number of hounds around
                  else if (currentOccupant instanceof Hound)
                  {
                     // Increment the hound count
                     houndCount++;
                  }
               }
               // If the number of hounds is less than 2 and there was at least
               // 1 other fox
               if (houndCount < MAX_NEIGHBORING_HOUNDS
                     && specificNeighborCount > 0)
               {
                  // Choose a random Fox
                  chosenFox = randomOccupant(specificNeighborCount,
                        specificNeighbors);

                  // Try to Lock yourself then check
                  myLock = lockAndGet();

                  // Check that you have acquired the lock and not dead
                  if (myLock != null && !isThreadInterrupted())
                  {
                     // Try to Lock the empty cell and then check
                     neighborLock = chosenNeighbor.lockAndGet();
                     if (neighborLock != null)
                     {
                        // Make sure that the neighbor is still empty
                        if (chosenNeighbor instanceof Empty)
                        {
                           // Try to lock the other fox then check
                           secondFoxLock = chosenFox.lockAndGet();
                           if (secondFoxLock != null)
                           {
                              if (chosenFox instanceof Fox)
                              {
                                 // Create a new Fox
                                 createNewFieldOccupant(chosenNeighbor.getX(),
                                       chosenNeighbor.getY(), FOX);
                              }
                              // Reset the second foxes lock to false
                              secondFoxLock.getAndSet(false);
                           }
                        }
                        // Reset the Neibors lock to false
                        neighborLock.getAndSet(false);
                     }
                     // Reset your lock to false
                     myLock.getAndSet(false);
                  }
               }
            }
         }
      }
   }
}







