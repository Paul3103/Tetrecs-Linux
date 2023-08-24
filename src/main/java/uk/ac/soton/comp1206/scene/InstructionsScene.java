package uk.ac.soton.comp1206.scene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class InstructionsScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);

  /**
   * Create a new menu scene
   * @param gameWindow the Game Window this will be displayed in
   */
  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instructions Scene");
  }

  @Override
  public void initialise() {
    this.scene.setOnKeyPressed(e -> this.gameWindow.startMenu());
  }

  /**
   * Build the instructions layout
   */
  @Override
  public void build() {
    Multimedia.stop();
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
    var instructionBox = new VBox();

    instructionBox.setMaxWidth(gameWindow.getWidth());
    instructionBox.setMaxHeight(gameWindow.getHeight());
    instructionBox.getStyleClass().add("menu-background");
    root.getChildren().add(instructionBox);
    var instructionTitle = new Text("Instructions");
    instructionTitle.getStyleClass().add("title");
    instructionBox.getChildren().add(instructionTitle);
    instructionBox.setAlignment(Pos.TOP_CENTER);

    Image image = new Image(Multimedia.class.getResource("/images/Instructions.png").toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(true);
    imageView.setFitWidth(600);
    instructionBox.getChildren().add(imageView);

    var pieceGrid = new GridPane();
    instructionBox.getChildren().add(pieceGrid);
    pieceGrid.setPadding(new Insets(0.0, 30, 0.0, 30));
    pieceGrid.setHgap(20);
    pieceGrid.setVgap(15);
    int j = 0;
    for(int i=0;i< GamePiece.PIECES;i++) {
      GamePiece tempPiece = GamePiece.createPiece(i);
      Grid tempGrid = new Grid(3,3);
      PieceBoard pieceBoard = new PieceBoard(tempGrid,50,50);
      pieceBoard.display(tempPiece);
      pieceGrid.add(pieceBoard,i/3,j);
      j+=1;
      if(j>2) {
        j = 0;
      }
  }
  pieceGrid.setAlignment(Pos.BOTTOM_CENTER);
  /**
   * Initialise the menu
   */

  }
  private void shutdown(ActionEvent event) {
    Platform.exit();
  }
}