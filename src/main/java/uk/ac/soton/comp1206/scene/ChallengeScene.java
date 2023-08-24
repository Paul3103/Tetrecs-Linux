package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.CurrentPieceListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    public PieceBoard pieceBoard;
    public PieceBoard pieceBoard2;
    public int gridX = 0;
    public int gridY = 0;
    public GameBoard board;
    public Rectangle timer;
    private VBox bottomPane;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        this.board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2,
            gameWindow.getWidth() / 2);
        mainPane.setCenter(board);
        final var playerStats = new GridPane();
        //Pieceboard for displaying piece
        VBox pieceBoardVbox = new VBox();
        mainPane.setRight(pieceBoardVbox);
        pieceBoardVbox.setAlignment(Pos.BOTTOM_RIGHT);
        Text highScoreToBeat = new Text("High Score:"+getHighScore());
        pieceBoardVbox.getChildren().add(highScoreToBeat);
        highScoreToBeat.getStyleClass().add("hiscore");
        pieceBoardVbox.setSpacing(10);
        pieceBoardVbox.setPadding(new Insets(20, 20, 80, 20));
        final var currentPieceElement = new Text("Current:");
        pieceBoardVbox.getChildren().add(currentPieceElement);
        currentPieceElement.getStyleClass().add("title");
        this.pieceBoard2 = new PieceBoard(new Grid(3, 3), 150, 150);
        pieceBoardVbox.getChildren().add(pieceBoard2);
        final var nextPieceElement = new Text("Next:");
        pieceBoardVbox.getChildren().add(nextPieceElement);
        nextPieceElement.getStyleClass().add("title");
        this.pieceBoard2.display(game.currentPiece);
        this.pieceBoard = new PieceBoard(new Grid(3, 3), 100, 100);
        pieceBoardVbox.getChildren().add(pieceBoard);
        this.pieceBoard.display(game.nextGamePiece);

        //Score, lives and multiplier elements which are bound to SimpleIntegerProperties
        final var scoreElement = new Text("0");
        scoreElement.textProperty().bind(this.game.score.asString());
        final var fullScoreElement = new Text("Score:");
        playerStats.setColumnIndex(fullScoreElement, 0);
        playerStats.setMargin(fullScoreElement, new Insets(20, 10, 5, 20));
        playerStats.setColumnIndex(scoreElement, 1);
        playerStats.setMargin(scoreElement, new Insets(20, 50, 5, 5));
        fullScoreElement.getStyleClass().add("hiscore");
        scoreElement.getStyleClass().add("hiscore");

        final var livesElement = new Text("3");
        livesElement.textProperty().bind(this.game.lives.asString());
        final var fullLivesElement = new Text("Lives:");
        playerStats.setColumnIndex(fullLivesElement, 2);
        playerStats.setMargin(fullLivesElement, new Insets(20, 10, 5, 20));
        playerStats.setColumnIndex(livesElement, 3);
        playerStats.setMargin(livesElement, new Insets(20, 75, 5, 5));
        fullLivesElement.getStyleClass().add("lives");
        livesElement.getStyleClass().add("lives");

        final var multiplierElement = new Text("1");
        multiplierElement.textProperty().bind(this.game.multiplier.asString());
        final var fullMultiplierElement = new Text("x");
        playerStats.setColumnIndex(fullMultiplierElement, 4);
        playerStats.setMargin(fullMultiplierElement, new Insets(20, 5, 5, 20));
        playerStats.setColumnIndex(multiplierElement, 5);
        playerStats.setMargin(multiplierElement, new Insets(20, 55, 5, 5));
        fullMultiplierElement.getStyleClass().add("title");
        multiplierElement.getStyleClass().add("title");

        final var fullLevelElement = new Text("level:");
        playerStats.setColumnIndex(fullLevelElement, 6);
        playerStats.setMargin(fullLevelElement, new Insets(20, 5, 5, 10));
        final var levelElement = new Text("1");
        levelElement.textProperty().bind(this.game.level.asString());
        playerStats.setColumnIndex(levelElement, 7);
        playerStats.setMargin(levelElement, new Insets(20, 20, 5, 5));
        fullLevelElement.getStyleClass().add("level");
        levelElement.getStyleClass().add("level");

        playerStats.getChildren()
            .addAll(fullScoreElement, scoreElement, fullLivesElement, livesElement,
                fullMultiplierElement, multiplierElement, fullLevelElement, levelElement);
        mainPane.getChildren().add(playerStats);
        this.bottomPane = new VBox();
        mainPane.setBottom(bottomPane);
        //displays the timebar
        this.timer = new Rectangle();
        timer.setFill(Color.LIME);
        timer.setWidth(gameWindow.getWidth());
        timer.setHeight(30);
        bottomPane.getChildren().add(timer);
        bottomPane.setAlignment(Pos.BOTTOM_CENTER);
        //progBar.getProgress();

        //Handle block on gameboard grid being clicked
        board.setOnRightClick(this::rotateCurrentPiece);
        pieceBoard2.setOnBlockClick(this::rotateCurrentPiece);
        pieceBoard.setOnBlockClick(this::swapPiece);
        board.setOnBlockClick(this::blockClicked);
        game.setNextPieceListener(this::nextPiece);
        game.setCurrentPieceListener(this::currentPiece);
        game.setLineClearedListener(this::lineCleared);
        game.setOnGameLoop(this::gameLoop);
        game.setOnGameOver(() -> {
            gameOver(this.game);
        });
        /**
         * Multimedia to play the background music
         */
        //Multimedia.playMusic("game_start.wav");
        Multimedia.playMusic("Monkeys.wav");
    }

    private void gameLoop(final int i) {
        logger.info("Reduce Bar");
        logger.info(i);
        Timeline timeline = new Timeline(

            new KeyFrame(Duration.millis(i * 0.33),
                new KeyValue((this.timer.widthProperty()),
                    this.timer.widthProperty().get() * 0.66)),
            new KeyFrame(Duration.millis(i * 0.66),
                new KeyValue(this.timer.widthProperty(), this.timer.widthProperty().get() * 0.33)),
            new KeyFrame(Duration.millis(i * 0.66),
                new KeyValue(this.timer.fillProperty(), Color.ORANGE)),
            new KeyFrame(Duration.millis(i * 0.99),
                new KeyValue(this.timer.widthProperty(), 0)),
            new KeyFrame(Duration.millis(i * 0.99),
                new KeyValue(this.timer.fillProperty(), Color.RED)));
        timeline.play();
        //timeline.stop();
        this.timer.setWidth(gameWindow.getWidth());
        this.timer.setFill(Color.LIME);

    }

    private void lineCleared(Set<GameBlockCoordinate> gameBlockCoordinates) {
        logger.info("Line cleared");
        this.board.fadeOut(gameBlockCoordinates);
        gameLoop(game.getTimeDelay());
    }

    private void handleKey(KeyEvent keyEvent) {
        logger.info("Key Pressed: " + keyEvent.getCode());
        if (keyEvent.getCode().equals(KeyCode.SPACE) || keyEvent.getCode().equals(KeyCode.R)) {
            this.swapPiece();
        } else if (keyEvent.getCode().equals((KeyCode.ESCAPE))) {
            Multimedia.stop();
            game.gameTimer.cancel();
            this.gameWindow.startMenu();
        } else if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode()
            .equals(KeyCode.X)) {
            this.blockClicked(this.board.getBlock(this.gridX, this.gridY));
        } else if (keyEvent.getCode().equals(KeyCode.W) || keyEvent.getCode().equals(KeyCode.UP)) {
            if (gridY > 0) {
                this.gridY -= 1;
            }
        } else if (keyEvent.getCode().equals(KeyCode.A) || keyEvent.getCode()
            .equals(KeyCode.LEFT)) {
            if (gridX > 0) {
                this.gridX -= 1;
            }
        } else if (keyEvent.getCode().equals(KeyCode.D) || keyEvent.getCode()
            .equals(KeyCode.RIGHT)) {
            if (gridX < game.getCols() - 1) {
                this.gridX += 1;
            }
        } else if (keyEvent.getCode().equals(KeyCode.S) || keyEvent.getCode()
            .equals(KeyCode.DOWN)) {
            if (gridY < game.getRows() - 1) {
                this.gridY += 1;
            }
        } else if (keyEvent.getCode().equals(KeyCode.Q) || keyEvent.getCode().equals(KeyCode.Z)
            || keyEvent.getCode().equals(KeyCode.OPEN_BRACKET)) {
            rotateCurrentPiece(3);
        } else if (keyEvent.getCode().equals(KeyCode.E) || keyEvent.getCode().equals(KeyCode.C)
            || keyEvent.getCode().equals(KeyCode.CLOSE_BRACKET)) {
            rotateCurrentPiece();
        }
        this.board.hover(this.board.getBlock(this.gridX, this.gridY));

    }

    private void swapPiece(GameBlock gameBlock) {
        logger.info("switching piece");
        game.swapCurrentPiece();
        nextPiece(game.nextGamePiece);
        currentPiece(game.currentPiece);
    }

    private void swapPiece() {
        logger.info("switching piece");
        game.swapCurrentPiece();
        nextPiece(game.nextGamePiece);
        currentPiece(game.currentPiece);
    }

    private void rotateCurrentPiece(GameBlock gameBlock) {
        logger.info("Rotating currentPiece");
        game.rotateCurrentPiece();
        currentPiece(game.currentPiece);
    }

    private void rotateCurrentPiece() {
        logger.info("Rotating currentPiece");
        game.rotateCurrentPiece();
        currentPiece(game.currentPiece);
    }

    private void rotateCurrentPiece(int rot) {
        logger.info("Rotating currentPiece");
        game.rotateCurrentPiece(rot);
        currentPiece(game.currentPiece);
    }

    private void nextPiece(GamePiece gamePiece) {
        this.pieceBoard.clear(gamePiece);
        this.pieceBoard.display(gamePiece);
    }

    private void currentPiece(GamePiece gamePiece) {
        this.pieceBoard2.clear(gamePiece);
        this.pieceBoard2.display(gamePiece);
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
        //gameLoop(game.getTimeDelay());
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        gameWindow.getScene().setOnKeyPressed(e -> handleKey(e));

        game.start();
    }

    public void gameOver(Game game) {
        logger.info("Uh oh, game over");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Multimedia.stop();
                gameWindow.startScores(game);
            }
        });
    }

    public String getHighScore() {
        try {

            File file = new File("highscores.txt");
            if (file.length() == 0) {
                throw new IOException("");
            }
            FileReader reader = new FileReader("highscores.txt");
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            String[] parts = line.split(":");
            String score = parts[1];
            return score;
        } catch (IOException e) {
            e.printStackTrace();
        } return "None";
    }
}

