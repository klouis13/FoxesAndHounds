import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * The Simulation class is a program that runs and animates a simulation of
 * Foxes and Hounds.
 */
public class Simulation
{
   // Create the count down latch for the foxes and hounds to wait on
   public static final CountDownLatch SIMULATION_START = new CountDownLatch(1);

   // The constant CELL_SIZE determines the size of each cell on the 
   // screen during animation.  (You may change this if you wish.)
   private static final int    CELL_SIZE     = 20;
   private static final String USAGE_MESSAGE = "Usage: java Simulation "
         + "[--graphics] [--width int] [--height int] [--starvetime int] "
         + "[--fox float] [--hound float]";

   /*
    * Draws the current state of the field
    *
    * @param graphicsContext is an optional GUI window to draw to
    * @param theField        is the object to display
    */
   private static void drawField(Graphics graphicsContext, Field theField)
   {
      // If we have a graphics context then update the GUI, otherwise
      // output text-based display
      if (graphicsContext != null)
      {
         // Iterate over the cells and draw the thing in that cell
         for (int j = 0; j < theField.getHeight(); j++)
         {
            for (int i = 0; i < theField.getWidth(); i++)
            {
               // Get the color of the object in that cell and set the cell
               // color
               graphicsContext
                     .setColor(theField.getOccupantAt(i, j).getDisplayColor());

               graphicsContext.fillRect(i * CELL_SIZE, j * CELL_SIZE,
                     CELL_SIZE, CELL_SIZE);
            } // for
         } // for
      }
      else // No graphics, just text
      {
         // Draw a line above the field
         for (int i = 0; i < theField.getWidth() * 2 + 1; i++)
         {
            System.out.print("-");
         }
         System.out.println();
         // For each cell, display the thing in that cell
         for (int j = 0; j < theField.getHeight(); j++)
         {
            System.out.print("|"); // separate cells with '|'
            for (int i = 0; i < theField.getWidth(); i++)
            {
               System.out.print(theField.getOccupantAt(i, j) + "|");
            }
            System.out.println();
         } // for

         // Draw a line below the field
         for (int i = 0; i < theField.getWidth() * 2 + 1; i++)
         {
            System.out.print("-");
         }
         System.out.println();

      } // else
   } // drawField


   /*
    * Add elements to the field to start. Each element is a new thread and is
    * started. The thread will wait for the field to be drawn to start computing
    *
    * @param theField         The field to add elements to
    * @param probabilityFox   The probability that a fox will be created
    * @param probabilityHound The probability that a hound will be created
    */
   private static Field setStartingField(Field theField, double probabilityFox,
         double probabilityHound)
   {
      // Initialize variables
      Random randomGenerator = new Random();
      FieldOccupant newOccupant;

      // Visit each cell; randomly placing a Fox, Hound, or nothing in each.
      for (int i = 0; i < theField.getWidth(); i++)
      {
         for (int j = 0; j < theField.getHeight(); j++)
         {
            // If a random number is less than or equal to the probability of
            // adding a hound, then place a hound.
            if (randomGenerator.nextFloat() <= probabilityHound)
            {
               newOccupant = new Hound(i, j, false);
               new Thread((Hound) newOccupant).start();
            }
            // If a random number is less than or equal to the probability
            // of adding a fox, then place a fox
            else if (randomGenerator.nextGaussian() <= probabilityFox)
            {
               newOccupant = new Fox(i, j, false);
               new Thread((Fox) newOccupant).start();
            }
            else
            // The spot has neither a fox or a hound, so put an empty spot there
            {
               newOccupant = new Empty(i, j, false);
            }

            theField.setOccupantAt(i, j, newOccupant);

         } // for
      } // for

      return theField;
   }


   /*
    * Loop continuously redrawing the field each time the AtomicBoolean is set
    *
    * @param theField        the field to redraw
    * @param graphicsContext the context for the graphics content if in graphic
    * mode
    */
   private static void redrawField(Field theField, Graphics graphicsContext)
         throws InterruptedException
   {
      // Loop continuously checking the flag and redrawing the field
      while (true)
      {
         // Check if the redraw boolean is set and set it to false
         if (Field.getRedrawField().getAndSet(false))
         {
            // Wait 30 milliseconds for the field to draw
            Thread.sleep(15);

            // Draw the field
            drawField(graphicsContext, theField);
         }
      }
   }


   /**
    * Main reads the parameters and performs the simulation and animation.
    */
   public static void main(String[] args) throws InterruptedException
   {
      /*
       *  Default parameters.  (You may change these if you wish.)
       */
      int width = 25;                              // Default width
      int height = 25;                             // Default height
      int starveTime = Hound.DEFAULT_STARVE_TIME;  // Default starvation time
      double probabilityFox = .5;                 // Default probability of fox
      double probabilityHound = .1;              // Default probability of hound
      boolean graphicsMode = false;
      Field theField;

      // If we attach a GUI to this program, these objects will hold
      // references to the GUI elements
      Frame windowFrame = null;
      Graphics graphicsContext = null;
      Canvas drawingCanvas = null;

      /*
       *  Process the input parameters. Switches we understand include:
       *  --graphics for "graphics" mode
       *  --width 999 to set the "width" 
       *  --height 999 to set the height
       *  --starvetime 999 to set the "starve time"
       *  --fox 0.999 to set the "fox probability"
       *  --hound 0.999 to set the "hound probability"
       */
      for (int argNum = 0; argNum < args.length; argNum++)
      {
         try
         {
            switch (args[argNum])
            {
               case "--graphics":  // Graphics mode
                  graphicsMode = true;
                  break;

               case "--width": // Set width
                  width = Integer.parseInt(args[++argNum]);
                  break;

               case "--height": // set height
                  height = Integer.parseInt(args[++argNum]);
                  break;

               case "--starvetime": // set 'starve time'
                  starveTime = Integer.parseInt(args[++argNum]);
                  break;

               case "--fox": // set the probability for adding a fox
                  probabilityFox = Double.parseDouble(args[++argNum]);
                  break;

               case "--hound": // set the probability for adding a hound
                  probabilityHound = Double.parseDouble(args[++argNum]);
                  break;

               default: // Anything else is an error and we'll quit
                  System.err.println("Unrecognized switch.");
                  System.err.println(USAGE_MESSAGE);
                  System.exit(1);
            } // switch
         }
         catch (NumberFormatException | ArrayIndexOutOfBoundsException e)
         {
            System.err.println("Illegal or missing argument.");
            System.err.println(USAGE_MESSAGE);
            System.exit(1);
         }
      } // for

      // Create the initial Field.
      theField = new Field(width, height);

      // Set the Field for the fieldOccupants
      FieldOccupant.setOccupantField(theField);

      // Set the starve time for hounds
      Hound.setStarveTime(starveTime);

      // Initialize the starting field with elements
      theField = setStartingField(theField, probabilityFox, probabilityHound);

      // Check if graphics mode was set and if so create the frame, canvas,
      // and window. If not in graphics mode, these will remain null
      if (graphicsMode)
      {
         windowFrame = new JFrame("Foxes and Hounds");
         windowFrame.setSize(theField.getWidth() * CELL_SIZE + 10,
               theField.getHeight() * CELL_SIZE + 30);
         windowFrame.setVisible(true);
         ((JFrame) windowFrame).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         // Create a "Canvas" we can draw upon; attach it to the window.
         drawingCanvas = new Canvas();
         drawingCanvas.setBackground(Color.white);
         drawingCanvas.setSize(theField.getWidth() * CELL_SIZE,
               theField.getHeight() * CELL_SIZE);
         windowFrame.add(drawingCanvas);
         graphicsContext = drawingCanvas.getGraphics();
      } // if

      drawField(graphicsContext, theField);
      drawField(graphicsContext, theField);

      // Start the simulation
      SIMULATION_START.countDown();

      redrawField(theField, graphicsContext);
   } // main

}
