import java.awt.*;

/**
 *
 */
public class EmptyCell extends FieldOccupant
{
   /**
    * @return the color to use for a cell occupied by an empty cell
    */
   @Override public Color getDisplayColor()
   {
      return Color.white;
   } // getDisplayColor


   /**
    * @return the text representing a Cell
    */
   @Override public String toString()
   {
      return " ";
   }


   public void run()
   {

   }
}
