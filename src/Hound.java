import java.awt.Color;

/**
 * Hounds can display themsevles
 */
public class Hound extends FieldOccupant { 

   /**
    * @return the color to use for a cell occupied by a Hound
    */
   @Override
   public Color getDisplayColor() {
      return Color.red;
   } // getDisplayColor


   // The default starve time for Hounds
   public static final int DEFAULT_STARVE_TIME = 3;

}
