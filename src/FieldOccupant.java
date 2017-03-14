import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */
public abstract class FieldOccupant
{
   // Declare Instance variables
   protected AtomicBoolean _lock;
   int _xCoordiante;
   int _yCoordinate;

   // Declare Constants
   private final int MAX_SLEEP_TIME = 1250;
   private final int MIN_SLEEP_TIME = 750;


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
   protected synchronized AtomicBoolean getAndLock()
   {
      AtomicBoolean lock = null;

      if (_lock.compareAndSet(false, true))
      {
         lock = _lock;
      }

      return lock;
   }


   /**
    * Sleep for a random time between 750 and 1250 ms
    *
    * @throws InterruptedException
    */
   protected void threadSleep() throws InterruptedException
   {
      Thread.sleep((int)(Math.random() * (MAX_SLEEP_TIME - MIN_SLEEP_TIME)
            + MIN_SLEEP_TIME));
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

   /**
    * @return the color to use for a cell containing a particular kind
    * of occupant
    */
   abstract public Color getDisplayColor();

}
