import java.awt.*;
import java.util.*;

/**
 *  The Simulation class is a program that runs and animates a simulation of
 *  Foxes and Hounds.
 */

public class Simulation {

   // The constant CELL_SIZE determines the size of each cell on the screen
   // during animation.  (You may change this if you wish.)
   private static final int CELL_SIZE = 10;
   private static final String USAGE_MESSAGE = "Usage: java Simulation [--graphics] [--width int] [--height int] [--starvetime int] [--fox float] [--hound float]";


   /**
    * Compute the next state of the field from the current state and
    * returns the new state
    *
    * @param currentState is the current state of the Field
    *
    * @return new field state after one timestep
    */
   private static Field performTimestep(Field currentState) {
      // You fill this in
      return null;
   } // performTimestep


   /**
    * Draw the current state of the field
    *
    * @param graphicsContext is an optional GUI window to draw to
    * @param theField is the object to display
    */
   private static void drawField(Graphics graphicsContext, Field theField) {
      // If we have a graphics context then update the GUI, otherwise
      // output text-based display
      if (graphicsContext != null) {
         // Iterate over the cells and draw the thing in that cell
         for (int i = 0; i < theField.getHeight(); i++) {
            for (int j = 0; j < theField.getWidth(); j++) {
               // Get the color of the object in that cell and set the 
               // cell color
               if (theField.getOccupantAt(j,i) != null) {
                  graphicsContext.setColor(theField.getOccupantAt(j,i)
                                           .getDisplayColor());       
               }
               else { // Empty cells are white
                  graphicsContext.setColor(Color.white);
               } 
               graphicsContext.fillRect(j * CELL_SIZE, 
                                        i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            } // for
         } // for
      }
      else { // No graphics, just text
         // Draw a line above the field
         for (int i = 0; i < theField.getWidth() * 2 + 1; i++) {
            System.out.print("-");
         }
         System.out.println();
         // For each cell, display the thing in that cell
         for (int i = 0; i < theField.getHeight(); i++) {
            System.out.print("|"); // separate cells with '|' 
            for (int j = 0; j < theField.getWidth(); j++) {
               if (theField.getOccupantAt(j,i) != null) {
                  System.out.print(theField.getOccupantAt(j,i)+"|");
               }
               else {
                  System.out.print(" |");
               }
            }
            System.out.println();
         } // for

         // Draw a line below the field
         for (int i = 0; i < theField.getWidth() * 2 + 1; i++) {
            System.out.print("-");
         }
         System.out.println();

      } // else
   } // drawField


   /**
    *  Main reads the parameters and performs the simulation and animation.
    */
   public static void main(String[] args) throws InterruptedException {

      /**
       *  Default parameters.  (Change using command-line arguments) 
       */
      int width = 50;                              
      int height  = 25;                           
      int starveTime = Hound.DEFAULT_STARVE_TIME;
      double probabilityFox = 0.5;              
      double probabilityHound = 0.15;         
      boolean graphicsMode = false;

      Random randomGenerator = new Random();      
      Field theField = null;

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
      for (int argNum=0; argNum < args.length; argNum++) {
         // Based on the argument, may need to attempt consuming a numeric value
         // so watch out for missing arguments or incorrect numeric formats
         try {
            switch(args[argNum]) {
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
         catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Illegal or missing argument.");
            System.err.println(USAGE_MESSAGE);
            System.exit(1);
         }
      } // for


      // Create the initial Field.
      theField = new Field(width, height);

      // Visit each cell; randomly placing a Fox, Hound, or nothing in each.
      for (int i = 0; i < theField.getWidth(); i++) {
         for (int j = 0; j < theField.getHeight(); j++) {
            // If a random number is less than or equal to the probability
            // of adding a fox, then place a fox
            if (randomGenerator.nextFloat() <= probabilityFox) {
               theField.setOccupantAt(i, j, new Fox());
            } 
            // If a random number is less than or equal to the probability of
            // adding a hound, then place a hound. 
            else if (randomGenerator.nextFloat() <= probabilityHound) {    
               theField.setOccupantAt(i, j, new Hound());
            }
         } // for
      } // for

      // If we're in graphics mode, then create the frame, canvas, 
      // and window. If not in graphics mode, these will remain null
      if (graphicsMode) {
         windowFrame = new Frame("Foxes and Hounds");
         windowFrame.setSize(theField.getWidth() * CELL_SIZE + 10, 
                             theField.getHeight() * CELL_SIZE + 30);
         windowFrame.setVisible(true);

         // Create a "Canvas" we can draw upon; attach it to the window.
         drawingCanvas = new Canvas();
         drawingCanvas.setBackground(Color.white);
         drawingCanvas.setSize(theField.getWidth() * CELL_SIZE, 
                               theField.getHeight() * CELL_SIZE);
         windowFrame.add(drawingCanvas);
         graphicsContext = drawingCanvas.getGraphics();
      } // if 

      // Loop infinitely, performing timesteps. We could optionally stop
      // when the Field becomes empty or full, though there is no
      // guarantee either of those will ever arise...
      while (true) {                                              
         Thread.sleep(1000);  // Wait one second (1000 milliseconds)
         drawField(graphicsContext, theField);  // Draw the current state 
         theField = performTimestep(theField);  // Simulate a timestep
      }

   } // main

} 
