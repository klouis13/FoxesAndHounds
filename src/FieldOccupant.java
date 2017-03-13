import java.awt.Color;
import java.util.Random;

/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */
public abstract class FieldOccupant extends Thread
{
   protected Field.FieldCell _theCurrentCell;

   // Declare Constants
   private final int MAX_SLEEP_TIME = 1250;
   private final int MIN_SLEEP_TIME = 750;

   public FieldOccupant(Field.FieldCell theCell)
   {
      _theCurrentCell = theCell;
   }

   /**
    * @return the color to use for a cell containing a particular kind
    * of occupant
    */
   abstract public Color getDisplayColor();


   public void sleep() throws InterruptedException
   {
      Thread.sleep((long) Math.random() * (MAX_SLEEP_TIME - MIN_SLEEP_TIME)
            + MIN_SLEEP_TIME);
   }
}
