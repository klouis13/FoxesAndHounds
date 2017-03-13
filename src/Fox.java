import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * Foxes can display themselves
 */
public class Fox extends FieldOccupant
{

   public Fox(int x, int y)
   {
      super(x, y);
   }

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


   public void run()
   {
/*
      boolean dead = false;
      Set<FieldOccupant> neighbors = new HashSet<FieldOccupant>();

      while (!Simulation.hasSimulationStarted())
      {
         // Wait for the simulation to start
      }
      while (!dead)
      {

         try
         {
            sleep();
         }
         catch(InterruptedException e)
         {
            dead = true;
         }
      }

      */
   }
}
