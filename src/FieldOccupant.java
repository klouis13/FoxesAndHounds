import sun.util.resources.cldr.xog.CalendarData_xog_UG;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */
public abstract class FieldOccupant extends Thread
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
    * @param x the x coordiante
    * @param y the y coordinate
    */
   public FieldOccupant(int x, int y)
   {
      _lock = new AtomicBoolean(false);
      _xCoordiante = x;
      _yCoordinate = y;
   }

   /**
    * @return the color to use for a cell containing a particular kind
    * of occupant
    */
   abstract public Color getDisplayColor();


   /**
    * Sleep for a random time between 750 and 1250 ms
    *
    * @throws InterruptedException
    */
   public void sleep() throws InterruptedException
   {
      Thread.sleep((long) Math.random() * (MAX_SLEEP_TIME - MIN_SLEEP_TIME)
            + MIN_SLEEP_TIME);
   }

   /**
    * Checks if the lock if false and sets to true if it is false
    *
    * @return The lock or null if the lock is already taken.
    */
   public synchronized AtomicBoolean getAndLock()
   {
      AtomicBoolean lock = null;

      if (_lock.compareAndSet(false, true))
      {
         lock = _lock;
      }

      return lock;
   }

}
