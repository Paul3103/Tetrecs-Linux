package uk.ac.soton.comp1206.component;

import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * PieceBoard is used to display the current piece and next piece on the screen
 * It is an extension of gameboard, with display being an added method
 */
public class PieceBoard extends GameBoard {

    private BlockClickedListener blockClickedListener;

    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);
    }

    /**
     * Method for displaying adding blocks to grid
     *
     * @param gamePiece
     */
    public void display(GamePiece gamePiece) {
        //System.out.println("displaying gamepiece");
        int[][] blocks = gamePiece.getBlocks(); //Nested for loop to attach each block correctly
        for (int i = 0; i < this.grid.getRows(); i++) {
            for (int j = 0; j < this.grid.getCols(); j++) {
                if (blocks[i][j] > 0) {
                    grid.set(i, j, gamePiece.getValue());

                } if(i==this.grid.getCols()/2 && j==this.grid.getRows()/2){
                    //System.out.println("setting centre");
                    int midX = this.grid.getRows() / 2 ;
                    int midY = this.grid.getCols() / 2 ;
                    this.blocks[midX][midY].addCentre();
                }
            }
        }
    }

    public void clear(GamePiece gamePiece) {
        int[][] blocks = gamePiece.getBlocks(); //Nested for loop to attach each block correctly
        for (int i = 0; i < this.grid.getRows(); i++) {
            for (int j = 0; j < this.grid.getCols(); j++) {
                grid.set(i, j, 0);
            }
        }
    }
    @Override
    public void hover(GameBlock block) {
        return;
    }
}

