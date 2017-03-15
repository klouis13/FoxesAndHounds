import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */
public abstract class FieldOccupant
{
   // Declare Instance variables
   protected AtomicBoolean _lock;
   private int _xCoordiante;
   private int _yCoordinate;

   // Initialize Constants
   protected final int NUM_NEIGHBORS = 8;
   protected final int FOX = 0;
   protected final int HOUND = 1;
   protected final int EMPTY_CELL = 2;

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
   protected AtomicBoolean getAndLock()
   {
      AtomicBoolean lock = null;

      if (_lock.compareAndSet(false, true))
      {
         lock = _lock;
      }

      return lock;
   }


   protected void interruptThread()
   {
      Thread.currentThread().interrupt();
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

      int sleepTime = (int) Math.random() * (MAX_SLEEP_TIME - MIN_SLEEP_TIME)
            + MIN_SLEEP_TIME;

      Thread.sleep(sleepTime);

      return sleepTime;
   }


   /**
    * @param x the x coordinate
    * @param y the y coordinate
    * @return an array of the neighbors around a specific cell
    */
   protected FieldOccupant[] getNeighborsArray()
   {
      // Get the neighbors
      return Simulation._theField.getNeighborCells(getX(), getY())
            .toArray(new FieldOccupant[NUM_NEIGHBORS]);
   }


   /**
    *
    * @param x
    * @param y
    * @return
    */
   protected int[] dirtyReadNeighbors()
   {
      int[] theOccupantCount = new int[3];

      for (FieldOccupant theOccupant : getNeighborsArray())
      {
         if (theOccupant instanceof Fox)
         {
            theOccupantCount[0]++;
         }
         else if (theOccupant instanceof Hound)
         {
            theOccupantCount[1]++;
         }
         else
         {
            theOccupantCount[2]++;
         }
      }

      return theOccupantCount;
   }

   /**
    * Count the number of hounds around a given cell
    * @param x the x coordiantes
    * @param y the y coordinates
    * @return the number of hounds
    */
   protected int numNeighboringHounds(int x, int y)
   {
      int houndCount = 0;

      for (FieldOccupant occupant : Simulation._theField.getNeighborCells(x, y))
      {
         if (occupant instanceof Hound)
         {
            houndCount++;
         }
      }
      return houndCount;
   }


   protected int randomToMax(int max)
   {
      return (int)(Math.random() * (max + 1));
   }


   /**
    * @return the color to use for a cell containing a particular kind
    * of occupant
    */
   abstract public Color getDisplayColor();

}
