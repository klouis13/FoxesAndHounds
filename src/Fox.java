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


   public Fox(int x, int y, AtomicBoolean theLock)
   {
      super(x, y, theLock);
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

   // Ask if I'm interrupted right after looking at neighbors
   // Then lock yourself
   // Use a countdown latch


   public void run()
   {
      // Declare Variables
      int houndCount;
      int specificNeighborCount;
      AtomicBoolean myLock;
      AtomicBoolean neighborLock;
      AtomicBoolean secondFoxLock;
      AtomicBoolean newFoxLock;
      FieldOccupant chosenNeighbor;
      FieldOccupant chosenFox;
      FieldOccupant newFox = null;

      // Create an array to hold FieldOccupants that will need to be accessed later
      FieldOccupant[] specificNeighbors = new FieldOccupant[NUM_NEIGHBORS];

      // Initialize Constants
      final int MAX_NEIGHBORING_HOUNDS = 2;

      // Wait for the simulation to start
      try
      {
         Simulation._simulationStarted.await();
      }
      catch (InterruptedException e)
      {

      }

      // Run while the fox has not been eaten
      while (!isThreadInterrupted())
      {
         // Reset the counter for the FieldOccupants that need to be stored
         specificNeighborCount = 0;

         // Iterate over the neighbors
         for (FieldOccupant currentOccupant : getNeighbors())
         {
            // Check if the neighbor is an Empty Object
            if (currentOccupant instanceof Empty)
            {
               // Store the Empty objects in an array to randomly choose one later
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

               // Iterate over the neighbors of the empty cell looking for foxes and hounds
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
               // If the number of hounds is less than 2 and there was at least 1 other fox
               if (houndCount < MAX_NEIGHBORING_HOUNDS
                     && specificNeighborCount > 0)
               {
                  // Choose a random Fox
                  chosenFox = randomOccupant(specificNeighborCount,
                        specificNeighbors);

                  // Try to Lock yourself then check
                  myLock = lockAndGet();
                  if (myLock != null)
                  {
                     // Try to Lock the empty cell and then check
                     neighborLock = chosenNeighbor.lockAndGet();
                     if (neighborLock != null
                           && chosenNeighbor instanceof Empty)
                     {
                        // Try to lock the other fox then check
                        secondFoxLock = chosenFox.lockAndGet();
                        if (secondFoxLock != null && chosenFox instanceof Fox)
                        {
                           // Make sure you haven't been interrupted
                           if (!isThreadInterrupted())
                           {
                              // Create a new Fox
                              createNewFieldOccupant(chosenNeighbor.getX(), chosenNeighbor.getY(), FOX);
                           }
                           // Reset the second foxes lock to false
                           secondFoxLock.getAndSet(false);
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
         // The fox is done doing things so it sleeps
         try
         {
            //  System.out.println("sleep");
            threadSleep();
         }
         catch (InterruptedException e)
         {

         }
      }
   }
}







