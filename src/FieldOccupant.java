import java.awt.Color;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */
public abstract class FieldOccupant
{
   // Declare Instance variables
   private AtomicBoolean _lock;
   private int _xCoordiante;
   private int _yCoordinate;

   // Initialize Constants
   protected final int NUM_NEIGHBORS = 8;
   protected final int FOX           = 0;
   protected final int HOUND         = 1;
   protected final int EMPTY         = 2;

   /**
    * Create a FieldOccupant with a given x and y coordiante
    *
    * @param x the x coordiante
    * @param y the y coordinate
    */
   public FieldOccupant(int x, int y, boolean initLock)
   {
      _lock = new AtomicBoolean(initLock);
      _xCoordiante = x;
      _yCoordinate = y;
   }


   protected FieldOccupant(int x, int y, AtomicBoolean theLock)
   {
      _lock = theLock;
      _xCoordiante = x;
      _yCoordinate = y;
   }


   public int getX()
   {
      return _xCoordiante;
   }


   public int getY()
   {
      return _yCoordinate;
   }


   /**
    * Checks if the lock if false and sets to true if it is false
    *
    * @return The lock or null if the lock is already taken.
    */
   protected AtomicBoolean lockAndGet()
   {
      AtomicBoolean lock = null;

      if (_lock.compareAndSet(false, true))
      {
         lock = _lock;
      }

      return lock;
   }


   protected AtomicBoolean unlockAndGet()
   {
      AtomicBoolean lock = null;

      if (_lock.compareAndSet(true, false))
      {
         lock = _lock;
   }

      return lock;
   }

   protected void interruptThread()
   {
      Thread.currentThread().interrupt();
   }


   protected boolean isThreadInterrupted()
   {
      return Thread.currentThread().isInterrupted();
   }

   /**
    * Sleep for a random time between 750 and 1250 ms
    *
    * @throws InterruptedException
    */
   protected int threadSleep() throws InterruptedException
   {
      // Declare Constants
      final int MAX_SLEEP_TIME = 1250;
      final int MIN_SLEEP_TIME = 750;

      int sleepTime = (int) (Math.random() * (MAX_SLEEP_TIME - MIN_SLEEP_TIME))
            + MIN_SLEEP_TIME;

      Thread.sleep(sleepTime);

      return sleepTime;
   }


   /**
    * @return an array of the neighbors around the object
    */
   protected FieldOccupant[] getNeighborsArray()
   {
      // Get the neighbors
      return Simulation._theField.getNeighborCells(getX(), getY())
            .toArray(new FieldOccupant[NUM_NEIGHBORS]);
   }


   protected Set<FieldOccupant> getNeighbors()
   {
      return Simulation._theField.getNeighborCells(getX(), getY());
   }

   /**
    * @return
    */
   protected int[] dirtyReadNeighbors()
   {
      int[] theOccupantCount = new int[3];

      for (FieldOccupant theOccupant : getNeighbors())
      {
         if (theOccupant instanceof Fox)
         {
            theOccupantCount[FOX]++;
         }
         else if (theOccupant instanceof Hound)
         {
            theOccupantCount[HOUND]++;
         }
         else
         {
            theOccupantCount[EMPTY]++;
         }
      }

      return theOccupantCount;
   }

   /**
    * Count the number of hounds around a given cell
    *
    * @return the number of hounds
    */
   protected int numNeighboringHounds()
   {
      int houndCount = 0;

      for (FieldOccupant occupant : getNeighbors())
      {
         if (occupant instanceof Hound)
         {
            houndCount++;
         }
      }
      return houndCount;
   }


   /**
    * Create a new FieldOccupant and set the update field flas
    * @param x
    * @param y
    * @param newOccupantType
    */
   protected void createNewFieldOccupant(int x, int y, int newOccupantType)
   {
      // Create a lock for the new fox so that we can unlock it after the thread is started
      AtomicBoolean newOccupantLock = new AtomicBoolean(true);
      FieldOccupant newOccupant = null;

      // Create a new FieldOccupant
      switch (newOccupantType)
      {
         case FOX:
            newOccupant = new Fox(x,
                  y, newOccupantLock);
            break;
         case HOUND:
            newOccupant = new Hound(x,
                  y, newOccupantLock);
            break;
         case EMPTY:
            newOccupant = new Empty(x,
                  y, newOccupantLock);
            break;
      }

      // Put a new Occupant in the field
      Simulation._theField
            .setOccupantAt(x, y, newOccupant);

      // Create and Start the thread if it was a Fox or Hound
      switch (newOccupantType)
      {
         case FOX:
            new Thread((Fox) newOccupant).start();
            break;
         case HOUND:
            new Thread((Hound) newOccupant).start();
            break;
      }

      // Unlock the new Occupant so others on the field can use it.
      newOccupantLock.getAndSet(false);

      // Set the Boolean to redraw the field
      Field.setRedrawField();
   }

   /**
    * @param max the max range exclusive
    * @return a random number between 0 and max exclusive
    */
   protected FieldOccupant randomOccupant(int max, FieldOccupant[] theOccupants)
   {
      return theOccupants[(int)(Math.random() * (max))];
   }


   /**
    * @return the color to use for a cell containing a particular kind
    * of occupant
    */
   abstract public Color getDisplayColor();

}
