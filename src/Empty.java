import java.awt.*;

/**
 * Represents a Empty cell in the field
 */
public class Empty extends FieldOccupant
{
   /**
    * Create a Empty object with an x and y coordinate
    *
    * @param x        the x coordinate
    * @param y        the y coordinate
    * @param initLock the initial lock parameter
    */
   public Empty(int x, int y, boolean initLock)
   {
      super(x, y, initLock);
   }


   /**
    * @return the color to use for a cell occupied by a Fox
    */
   @Override public Color getDisplayColor()
   {
      return Color.black;
   } // getDisplayColor


   /**
    * @return the text representing a Fox
    */
   @Override public String toString()
   {
      return " ";
   }
}
