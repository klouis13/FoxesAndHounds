/**
 *  The Field class defines an object that models a field full of foxes and
 *  hounds. Descriptions of the methods you must implement appear below. 
 */
public class Field {

   /**
    *  Create an empty field of given width and height
    *
    *  @param width of the field.
    *  @param height of the field.
    */
   public Field (int width, int height) {
     // Your solution here.
   } // Field

   
   /**
    *  Get the width of the field
    *
    *  @return the width of the field.
    */
   public int getWidth() {
       // Replace the following line with your solution.
       return 1;
   } // getWidth


   /**
    *  Get the height of the field
    *
    *  @return the height of the field.
    */
   public int getHeight() {
      // Replace the following line with your solution.
      return 1;
   } // getHeight


   /**
    *  Place an occupant in cell (x, y).
    *
    *  @param x is the x-coordinate of the cell to place a mammal in.
    *  @param y is the y-coordinate of the cell to place a mammal in.
    *  @param toAdd is the occupant to place.
    */
   public void setOccupantAt(int x, int y, FieldOccupant toAdd) {
       // Your solution here.
       
   } // setOccupantAt


   /**
    *  @param x is the x-coordinate of the cell whose contents are queried.
    *  @param y is the y-coordinate of the cell whose contents are queried.
    *
    *  @return occupant of the cell (or null if unoccupied)
    */
   public FieldOccupant getOccupantAt(int x, int y) {
      // Replace the following line with your solution.
      return null;
   } // getOccupantAt


   /**
    *  Define any variables associated with a Field object here.  These
    *  variables MUST be private.
    */
}
