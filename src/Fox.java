import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Foxes can display themselves
 */
public class Fox extends FieldOccupant
{
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
}
