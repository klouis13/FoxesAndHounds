import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a Empty (empty) cell in the field
 */
public class Empty extends FieldOccupant
{
   /**
    * Create a Empty object with an x and y coordinate
    * @param x the x coordinate
    * @param y the y coordinate
    */
   public Empty(int x, int y, boolean initLock)
   {
      super(x,y, initLock);
   }

   public Empty(int x, int y, AtomicBoolean lock)
   {
      super(x,y, lock);
   }


   /**
    * @return the color to use for a cell occupied by a Fox
    */
   @Override public Color getDisplayColor()
   {
      return Color.white;
   } // getDisplayColor


   /**
    * @return the text representing a Fox
    */
   @Override public String toString()
   {
      return " ";
   }
}
