import java.awt.Color;

/**
 * Creates an empty cell
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
    * @return the text representing an empty cell
    */
   @Override public String toString()
   {
      return " ";
   }

   
   @Override public void run()
   {

   }
}

