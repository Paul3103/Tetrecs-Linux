package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Go through and check to see if a block can be placed
     * @param gamePiece
     * @param x_Coord
     * @param y_Coord
     * @return if piece is valid to be placed
     */
    public boolean canPlayPiece(GamePiece gamePiece, int x_Coord, int y_Coord) {
        logger.info("canPlayPiece- is the piece valid");
        int piece_x = -1;
        for(int i = x_Coord - 1; i < x_Coord + 2; i++) {
            piece_x+=1;
            int piece_y = -1;
            for(int j = y_Coord - 1; j < y_Coord + 2; j++) {
                piece_y += 1;
                //System.out.println(i+","+j);
                int value = get(i,j);
                //System.out.println(value);
                if (gamePiece.getBlocks()[piece_x][piece_y]>0 && value != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    public void playPiece(GamePiece gamePiece, int x_Coord, int y_Coord,int newValue) {
        int piece_x = -1;
        for(int i=x_Coord-1; i<x_Coord+2;i++) {
            piece_x += 1;
            int piece_y = -1;
            for(int j=y_Coord-1;j<y_Coord+2;j++) {
                piece_y += 1;
                if(gamePiece.getBlocks()[piece_x][piece_y]>0){
                    set(i,j,newValue);
                }
            }
        }
    }
}
