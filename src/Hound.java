import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hounds can display themsevles. They also get hungry
 */
public class Hound extends FieldOccupant
{
   // Declare instance variables
   private static int _houndStarveTime;
   private int _hungerLevel;

   // Default starve time for Hounds
   public static final int DEFAULT_STARVE_TIME = 3;


   /**
    * Create a hound
    */
   public Hound()
   {
      _hungerLevel = _houndStarveTime;
   }


   /**
    * @return the color to use for a cell occupied by a Hound
    */
   @Override public Color getDisplayColor()
   {
      return Color.red;
   } // getDisplayColor


   /**
    * @return the text representing a Hound
    */
   @Override public String toString()
   {
      return "H";
   }


   /**
    * Sets the starve time for this class
    *
    * @param starveTime
    */
   public static void setStarveTime(int starveTime)
   {
      _houndStarveTime = starveTime;
   }


   public void run()
   {
      while (!Simulation.hasSimulationStarted())
      {
         // Wait for the simulation to start
      }
      while (_hungerLevel != 0)
      {

         try
         {
            sleep();
         }
         catch (InterruptedException e)
         {

         }
      }
   }

}
