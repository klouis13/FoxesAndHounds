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

      // New hounds are not hungry
      fed();
   }


   public Hound(int x, int y, AtomicBoolean theLock)
   {
      super(x, y, theLock);

      // New hounds are not hungry
      fed();
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


   private void fed()
   {
      _hungerLevel = _houndStarveTime;
   }


   /**
    * Sets the starve time for this class
    *
    * @param starveTime
    */
   public static void setStarveTime(int starveTime)
   {
      // Convert to ms
      _houndStarveTime = starveTime * 1000;
   }


   public void run()
   {
      // Declare Variables
      int specificNeighborCount;
      int foxCount;

      AtomicBoolean myLock;
      AtomicBoolean houndLock;
      AtomicBoolean neighborLock;
      AtomicBoolean firstFoxLock;
      AtomicBoolean secondFoxLock;

      FieldOccupant chosenNeighbor;
      FieldOccupant chosenHound;
      FieldOccupant firstChosenFox;
      FieldOccupant secondChosenFox;
      FieldOccupant newHound;

      // Create an array to hold FieldOccupants that will need to be accessed later
      FieldOccupant[] specificNeighbors = new FieldOccupant[NUM_NEIGHBORS];
      FieldOccupant[] foundFoxes = new FieldOccupant[NUM_NEIGHBORS];

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
         // Reset the counter for the FieldOccupants that need to be stored
         specificNeighborCount = 0;
         foxCount = 0;
         chosenNeighbor = null;

         // Iterate over the neighbors
         for (FieldOccupant currentOccupant : getNeighbors())
         {
            // Check if the neighbor is an Empty Object
            if (currentOccupant instanceof Fox)
            {
               // Store the Empty objects in an array to randomly choose one later
               foundFoxes[foxCount] = currentOccupant;

               // increment the count for either a fox or empty cell around
               foxCount++;
            }
            if (currentOccupant instanceof Empty)
            {
               // Store the Empty objects in an array to randomly choose one later
               specificNeighbors[specificNeighborCount] = currentOccupant;

               // increment the count for either a fox or empty cell around
               specificNeighborCount++;
            }
         }
         // If there was a fox prioritize eating it
         if (foxCount > 0)
         {
            chosenNeighbor = randomOccupant(foxCount,
                  foundFoxes);
         }
         // There was not a fox so check if there is an empty cell
         else if (specificNeighborCount > 0)
         {
            chosenNeighbor = randomOccupant(specificNeighborCount,
                  specificNeighbors);
         }
         // Check that there was a fox or a hound
         if (specificNeighborCount > 0 || foxCount > 0)
         {
            // Rset the counters for the hounds and foxes
            specificNeighborCount = 0;
            foxCount = 0;

            // Iterate over the neighbors of the chosen cell looking for foxes and hounds
            for (FieldOccupant currentOccupant : chosenNeighbor.getNeighbors())
            {
               // Count and store the hounds that are around the neighbor excluding this hound
               if (currentOccupant instanceof Hound && this != currentOccupant)
               {
                  specificNeighbors[specificNeighborCount] = currentOccupant;
                  specificNeighborCount++;
               }
               // Count and store the foxes around the hound
               else if (currentOccupant instanceof Fox)
               {
                  foundFoxes[foxCount] = currentOccupant;
                  foxCount++;
               }
            }

            // Check if the random Occupant chosen was a fox
            if (chosenNeighbor instanceof Fox)
            {
               // Try to lock yourself
               myLock = this.lockAndGet();

               // Check that you acquired the lock
               if (myLock != null)
               {
                  // Lock the fox
                  neighborLock = chosenNeighbor.lockAndGet();

                  // Make sure the the chosen occupant is still a fox
                  if (neighborLock != null)
                  {
                     if (chosenNeighbor instanceof Fox)
                     {
                        // Reset the newHound object
                        newHound = null;

                        // Check that there was a hound nearby
                        if (specificNeighborCount > 0)
                        {
                           // Choose a random hound
                           chosenHound = randomOccupant(specificNeighborCount,
                                 specificNeighbors);

                           // Lock the hound
                           houndLock = chosenHound.lockAndGet();

                           // Make sure the chosenHound is still there
                           if (houndLock != null)
                           {
                              if (chosenHound instanceof Hound)
                              {
                                 // Create a new Hound
                                 createNewFieldOccupant(chosenNeighbor.getX(),
                                       chosenNeighbor.getY(), HOUND);
                              }
                              houndLock.getAndSet(false);
                           }
                        }
                        if (newHound == null)
                        {
                           // Create a new empty cell where the fox was
                           createNewFieldOccupant(chosenNeighbor.getX(),
                                 chosenNeighbor.getY(), EMPTY);
                        }

                        // Tell the fox that it has been eaten
                        chosenNeighbor.interruptThread();

                        // Reset this hounds hunger
                        fed();
                     }
                     // Unlock the fox (might not need)
                     neighborLock.getAndSet(false);
                  }
                  // Unlock this hound
                  myLock.getAndSet(false);
               }
            }
            // Check if the chosen neighbor was an empty cell
            else if (chosenNeighbor instanceof Empty)
            {
               if (specificNeighborCount > 0 && foxCount > 1)
               {
                  // Choose a random hound
                  chosenHound = randomOccupant(specificNeighborCount, specificNeighbors);
                  firstChosenFox = randomOccupant(foxCount, foundFoxes);
                  secondChosenFox = randomOccupant(foxCount, foundFoxes);

                  myLock = this.lockAndGet();

                  if (myLock != null)
                  {
                     // Lock the Empty Cell
                     neighborLock = chosenNeighbor.lockAndGet();

                     // Make sure the the chosen occupant is still Empty
                     if (neighborLock != null)
                     {
                        if (chosenNeighbor instanceof Empty)
                        {
                           houndLock = chosenHound.lockAndGet();

                           // Make sure the the chosen occupant is still a Hound
                           if (houndLock != null)
                           {
                              if (chosenHound instanceof Hound)
                              {
                                 firstFoxLock = firstChosenFox.lockAndGet();

                                 // Make sure the the chosen occupant is still a fox
                                 if (firstFoxLock != null)
                                 {
                                    if (firstChosenFox instanceof Fox)
                                    {
                                       secondFoxLock = secondChosenFox
                                             .lockAndGet();

                                       // Make sure the the chosen occupant is still a fox
                                       if (secondFoxLock != null)
                                       {
                                          if (secondChosenFox instanceof Fox)
                                          {
                                             // Create a new Hound in the Empty Cell
                                             createNewFieldOccupant(
                                                   chosenNeighbor.getX(),
                                                   chosenNeighbor.getY(),
                                                   HOUND);

                                             // Put a new Empty Cell in the Foxes place
                                             createNewFieldOccupant(
                                                   firstChosenFox.getX(),
                                                   firstChosenFox.getY(),
                                                   EMPTY);

                                             // Tell the fox that it has been eaten
                                             firstChosenFox.interruptThread();

                                             // Reset this hounds hunger
                                             fed();

                                             // Set the Boolean to redraw the field
                                             Field.setRedrawField();

                                          }
                                          secondFoxLock.getAndSet(false);
                                       }
                                    }
                                    firstFoxLock.getAndSet(false);
                                 }
                              }
                              houndLock.getAndSet(false);
                           }
                        }
                        neighborLock.getAndSet(false);
                     }
                     myLock.getAndSet(false);
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

      // The hound died put a new Empty Cell in the Hounds place
      createNewFieldOccupant(getX(), getY(), EMPTY);
   }
}

