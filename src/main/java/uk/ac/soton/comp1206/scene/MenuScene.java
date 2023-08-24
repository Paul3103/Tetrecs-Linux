package uk.ac.soton.comp1206.scene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);
        FadeTransition fade = new FadeTransition(new Duration(3000.0), menuPane);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //better title
        Image image = new Image(Multimedia.class.getResource("/images/TetrECS.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);
        mainPane.setCenter(imageView);
        RotateTransition rotate = new RotateTransition(new Duration(1000.0), imageView);
        rotate.setCycleCount(-1);
        rotate.setFromAngle(-10.0);
        rotate.setToAngle(10.0);
        rotate.setAutoReverse(true);
        rotate.play();
        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        VBox buttonBox = new VBox();
        var singlePlayer = new Button("Single Player");
        singlePlayer.getStyleClass().add("menuItem");
        var multiPlayer = new Button("Multiplayer");
        multiPlayer.getStyleClass().add("menuItem");
        var instructions = new Button("How to Play");
        instructions.getStyleClass().add("menuItem");
        var quit = new Button("Quit");
        quit.getStyleClass().add("menuItem");
        singlePlayer.setPrefWidth(450);
        multiPlayer.setPrefWidth(450);
        instructions.setPrefWidth(450);
        quit.setPrefWidth(450);

        buttonBox.getChildren().addAll(singlePlayer,multiPlayer,instructions,quit);
        mainPane.setBottom(buttonBox);
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);

        //Bind the button action to the startGame method in the menu
        singlePlayer.setOnAction(this::startGame);
        instructions.setOnAction(this::startInstructions);
        quit.setOnAction(this::shutdown);
        //Adds menu music
        Multimedia.playMusic("Weasel.mp3");
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }
    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        Multimedia.stop();
        gameWindow.startChallenge();
    }

    /**
     * Handles when the instructions should begin
     * @param event
     */
    private void startInstructions(ActionEvent event) {
        gameWindow.startInstructions();
    }
    /**
     * Handles the program being shut down
     * @param event
     */
    private void shutdown(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
