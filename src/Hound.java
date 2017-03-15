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
      int relevantNeighborCount;
      int houndCount;
      int foxCount;

      AtomicBoolean myLock;
      AtomicBoolean houndLock;
      AtomicBoolean neighborLock;
      AtomicBoolean firstFoxLock;
      AtomicBoolean secondFoxLock;
      AtomicBoolean newHoundLock;

      FieldOccupant chosenNeighbor;
      FieldOccupant chosenHound;
      FieldOccupant firstChosenFox;
      FieldOccupant secondChosenFox;
      FieldOccupant newHound;

      // Create an array to hold FieldOccupants that will need to be accessed later
      FieldOccupant[] relevantNeighbors = new FieldOccupant[NUM_NEIGHBORS];
      FieldOccupant[] foundHounds = new FieldOccupant[NUM_NEIGHBORS];
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
         relevantNeighborCount = 0;

         // Iterate over the neighbors
         for (FieldOccupant currentOccupant : getNeighbors())
         {
            // Check if the neighbor is an Empty Object
            if (currentOccupant instanceof Fox
                  || currentOccupant instanceof Empty)
            {
               // Store the Empty objects in an array to randomly choose one later
               relevantNeighbors[relevantNeighborCount] = currentOccupant;

               // increment the count for either a fox or empty cell around
               relevantNeighborCount++;
            }
         }
         // There was at least 1 fox or empty cell around the hound
         if (relevantNeighborCount > 0)
         {
            chosenNeighbor = randomOccupant(relevantNeighborCount,
                  relevantNeighbors);

            houndCount = 0;
            foxCount = 0;

            // Iterate over the neighbors of the chosen cell looking for foxes and hounds
            for (FieldOccupant currentOccupant : chosenNeighbor.getNeighbors())
            {
               if (currentOccupant instanceof Hound && this != currentOccupant)
               {
                  foundHounds[houndCount] = currentOccupant;
                  houndCount++;
               }
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
                  if (neighborLock != null && chosenNeighbor instanceof Fox)
                  {
                     // Reset the newHound object
                     newHound = null;

                     // Check that there was a hound nearby
                     if (houndCount > 0)
                     {
                        // Choose a random hound
                        chosenHound = randomOccupant(houndCount, foundHounds);

                        // Lock the hound
                        houndLock = chosenHound.lockAndGet();

                        // Make sure the chosenHound is still there
                        if (houndLock != null && chosenHound instanceof Hound)
                        {
                           // Create a lock for the new Hound so that we can unlock it after the thread is started
                           newHoundLock = new AtomicBoolean(true);

                           // Create a new Hound Object with the lock on
                           newHound = new Hound(chosenNeighbor.getX(),
                                 chosenNeighbor.getY(), newHoundLock);

                           // Put a new Hound in the empty cell
                           Simulation._theField
                                 .setOccupantAt(chosenNeighbor.getX(),
                                       chosenNeighbor.getY(), newHound);

                           // Start the Hound thread
                           new Thread((Hound) newHound).start();

                           // Unlock the new Hound so others on the field can use it.
                           newHoundLock.getAndSet(false);
                        }
                     }
                     if (newHound == null)
                     {
                        // Put a new Empty Cell in the Foxes place
                        Simulation._theField
                              .setOccupantAt(chosenNeighbor.getX(),
                                    chosenNeighbor.getY(),
                                    new Empty(chosenNeighbor.getX(),
                                          chosenNeighbor.getY(), false));
                     }
                     // Tell the fox that it has been eaten
                     chosenNeighbor.interruptThread();

                     // Reset this hounds hunger
                     fed();

                     // Set the Boolean to redraw the field
                     Field.setRedrawField();

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
               if (houndCount > 0 && foxCount > 1)
               {
                  // Choose a random hound
                  chosenHound = randomOccupant(houndCount, foundHounds);
                  firstChosenFox = randomOccupant(foxCount, foundFoxes);
                  secondChosenFox = randomOccupant(foxCount, foundFoxes);

                  myLock = this.lockAndGet();

                  if (myLock != null)
                  {
                     // Lock the Empty Cell
                     neighborLock = chosenNeighbor.lockAndGet();

                     // Make sure the the chosen occupant is still Empty
                     if (neighborLock != null
                           && chosenNeighbor instanceof Empty)
                     {
                        houndLock = chosenHound.lockAndGet();

                        // Make sure the the chosen occupant is still a Hound
                        if (houndLock != null && chosenHound instanceof Hound)
                        {
                           firstFoxLock = firstChosenFox.lockAndGet();

                           // Make sure the the chosen occupant is still a fox
                           if (firstFoxLock != null
                                 && firstChosenFox instanceof Fox)
                           {

                              secondFoxLock = secondChosenFox.lockAndGet();

                              // Make sure the the chosen occupant is still a fox
                              if (secondFoxLock != null
                                    && secondChosenFox instanceof Fox)
                              {
                                 // Create a lock for the new Hound so that we can unlock it after the thread is started
                                 newHoundLock = new AtomicBoolean(true);

                                 // Create a new Hound Object with the lock on
                                 newHound = new Hound(chosenNeighbor.getX(),
                                       chosenNeighbor.getY(), newHoundLock);

                                 // Put a new Hound in the empty cell
                                 Simulation._theField
                                       .setOccupantAt(chosenNeighbor.getX(),
                                             chosenNeighbor.getY(), newHound);

                                 // Start the Hound thread
                                 new Thread((Hound) newHound).start();

                                 // Unlock the new Hound so others on the field can use it.
                                 newHoundLock.getAndSet(false);

                                 // Put a new Empty Cell in the Foxes place
                                 Simulation._theField
                                       .setOccupantAt(firstChosenFox.getX(),
                                             firstChosenFox.getY(),
                                             new Empty(firstChosenFox.getX(),
                                                   firstChosenFox.getY(),
                                                   false));

                                 // Tell the fox that it has been eaten
                                 firstChosenFox.interruptThread();

                                 // Reset this hounds hunger
                                 fed();

                                 // Set the Boolean to redraw the field
                                 Field.setRedrawField();

                                 secondFoxLock.getAndSet(false);
                              }
                              firstFoxLock.getAndSet(false);
                           }
                           houndLock.getAndSet(false);
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
      Simulation._theField
            .setOccupantAt(getX(), getY(), new Empty(getX(), getY(), false));

      // Set the Boolean to redraw the field
      Field.setRedrawField();

   }
}

