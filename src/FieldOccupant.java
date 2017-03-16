import java.awt.Color;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */
public abstract class FieldOccupant
{
   // Declare Instance variables
   private          AtomicBoolean _lock;
   private          int           _xCoordiante;
   private          int           _yCoordinate;
   protected static Field         _occupantField;

   // Initialize Constants
   protected final int NUM_NEIGHBORS = 8;
   protected final int FOX           = 0;
   protected final int HOUND         = 1;
   protected final int EMPTY         = 2;


   /**
    * Creata a FieldOccupant with the given x and y coordinate and the value of
    * lock
    *
    * @param x        the x coordinate
    * @param y        the y coordiante
    * @param initLock the boolean lock
    */
   public FieldOccupant(int x, int y, boolean initLock)
   {
      _lock = new AtomicBoolean(initLock);
      _xCoordiante = x;
      _yCoordinate = y;
   }


   /**
    * Set the field variable to be used by the FieldObjects
    *
    * @param theField the field for the FieldOccupants to live
    */
   public static void setOccupantField(Field theField)
   {
      _occupantField = theField;
   }


   /*
    * @return The x coordinate of the occupant
    */
   protected int getX()
   {
      return _xCoordiante;
   }


   /*
    * @return the y coordinate of the occupant
    */
   protected int getY()
   {
      return _yCoordinate;
   }


   /*
    * Checks if the lock if false and sets to true if it is false
    *
    * @return The lock or null if the lock is already taken.
    */
   protected AtomicBoolean lockAndGet()
   {
      AtomicBoolean lock = null;

      // Compare the current state of the lock and if false return the lock
      // if true then return null
      if (_lock.compareAndSet(false, true))
      {
         lock = _lock;
      }

      return lock;
   }


   /*
    * Interrupt the thread
    */
   protected void interruptThread()
   {
      Thread.currentThread().interrupt();
   }


   /*
    * @return true if the thread was interrupted or false if not
    */
   protected boolean isThreadInterrupted()
   {
      return Thread.currentThread().isInterrupted();
   }


   /*
    * Sleep for a random time between 750 and 1250 ms
    *
    * @throws InterruptedException
    */
   protected int threadSleep() throws InterruptedException
   {
      // Declare Constants
      final int MAX_SLEEP_TIME = 1250;
      final int MIN_SLEEP_TIME = 750;

      // Calculate a random sleep time between max and min
      int sleepTime = (int) (Math.random() * (MAX_SLEEP_TIME - MIN_SLEEP_TIME))
            + MIN_SLEEP_TIME;

      // Put the thread to sleep
      Thread.sleep(sleepTime);

      return sleepTime;
   }


   /**
    * @return the neighbor cells
    */
   protected Set<FieldOccupant> getNeighbors()
   {
      return _occupantField.getNeighborCells(getX(), getY());
   }


   /**
    * Create a new FieldOccupant and set the update field flag
    *
    * @param x               the x coordinates
    * @param y               the y coordinates
    * @param newOccupantType the type of FieldOccupant to create
    */
   protected void createNewFieldOccupant(int x, int y, int newOccupantType)
   {
      // Create a lock for the new fox so that we can unlock it after the thread
      // is started
      AtomicBoolean newOccupantLock;
      FieldOccupant newOccupant = null;

      // Create a new FieldOccupant
      switch (newOccupantType)
      {
         case FOX:
            newOccupant = new Fox(x, y, false);
            break;
         case HOUND:
            newOccupant = new Hound(x, y, false);
            break;
         case EMPTY:
            newOccupant = new Empty(x, y, false);
            break;
      }
      // Get and set the lock for the new occupant to true
      newOccupantLock = newOccupant.lockAndGet();

      // Put a new Occupant in the field
      _occupantField.setOccupantAt(x, y, newOccupant);

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
      return theOccupants[(int) (Math.random() * (max))];
   }


   /**
    * @return the color to use for a cell containing a particular kind
    * of occupant
    */
   abstract public Color getDisplayColor();

}
