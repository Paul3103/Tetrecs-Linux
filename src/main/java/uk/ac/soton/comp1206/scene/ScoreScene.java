package uk.ac.soton.comp1206.scene;

import static java.lang.Integer.parseInt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import javafx.collections.FXCollections;
import uk.ac.soton.comp1206.ui.ScoresList;
import uk.ac.soton.comp1206.utility.Multimedia;

public class ScoreScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  SimpleListProperty scoreListElement;
  SimpleListProperty scoreListElement2;
  public ArrayList<Pair<String, Integer>> arrayListScore;
  ObservableList<Pair<String, Integer>> observableListScore;
  ObservableList<Pair<String, Integer>> observableListScore2;
  public Game game;
  public ScoresList scoresList;
  public ScoresList scoresList2;

  private GridPane scorePane;
  public ArrayList<Pair<String,Integer>> arrayListRemoteScores;
  public ObservableList<Pair<String,Integer>> observableRemoteScores;
  private Communicator comms;
  public ScoresList remoteScoresList;
  private SimpleBooleanProperty showScores;
  private Timer timer;
  public ArrayList<Pair<String,Integer>> arrayListRemoteScore;
  public SimpleListProperty<Pair<String, Integer>> remoteScoresElement;
  public ArrayList<Pair<String,Integer>> tempScoreArrayList;
  private TextField username;
  private Button butt;
  private VBox topPane;
  public int globalIndex;
  public String sendUsername;
  private Text usernameElement;

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public ScoreScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    logger.info("Creating Scores Scene");
    Multimedia.stop();
    Multimedia.playSound("Explode.wav");
    Multimedia.playMusic("Happy.mp3");
    this.game = game;

  }

  /**
   * Initialise this scene. Called after creation
   */
  public void initialise() {

  }

  /**
   * Build the layout of the scene
   */
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var menuPane = new BorderPane();
    this.topPane = new VBox();
    Text scoreTitle = new Text("High Scores");
    this.scorePane = new GridPane();
    menuPane.setCenter(this.scorePane);
    menuPane.setMargin(this.scorePane, new Insets(50, 50, 0, 150));
    menuPane.setTop(topPane);
    this.topPane.getChildren().add(scoreTitle);
    this.topPane.setAlignment(Pos.TOP_CENTER);
    menuPane.setMargin(scoreTitle, new Insets(50, 150, 10, 150));
    scoreTitle.getStyleClass().add("bigtitle");

    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    this.username = new TextField("Paul2");
    this.username.setMaxWidth(150);
    this.usernameElement = new Text("");
    this.usernameElement.textProperty().bindBidirectional( this.username.textProperty());
    this.topPane.getChildren().add(this.username);
    this.butt = new Button("Enter Your Username:");
    this.butt.setOnAction(this::revealScoreList);
    this.topPane.getChildren().add(this.butt);

    Text finalScore = new Text("Your Score:"+Integer.toString(game.getScore()));
    this.topPane.getChildren().add(finalScore);
    finalScore.getStyleClass().add("heading");
    this.arrayListScore = getHighScores();
    int index = checkHighScore();
    if(index!=-1) {

      this.arrayListScore.set(index,new Pair<>(this.username.getText(),game.getScore()));
    }
    this.observableListScore = (ObservableList<Pair<String, Integer>>) FXCollections.observableArrayList(
        getList(this.arrayListScore));
    this.scoreListElement = new SimpleListProperty<>(this.observableListScore);
    this.scoresList = new ScoresList();
    this.scoresList.getScoreProperty().bind(scoreListElement);
    writeScores(this.arrayListScore);
    this.tempScoreArrayList = new ArrayList<>();
    this.arrayListRemoteScores = getRemoteHighScores();

    this.remoteScoresList = new ScoresList();



    //commicator for new scores
    this.comms = gameWindow.getCommunicator();
    //this.comms.addListener(score -> Platform.runLater(() -> this.receiveScore(score)));
    //this.comms.send("HISCORES UNIQUE");

    this.showScores = new SimpleBooleanProperty(false);


    //this.scorePane.getChildren().add(remoteScoresList);
    //revealScoreList();
    //this.remoteScoresList.display();
    //this.scoresList.reveal();

  }
  private void startGame(ActionEvent event) {
    Multimedia.stop();
    gameWindow.startChallenge();
  }

  private ArrayList<Pair<String,Integer>> getRemoteHighScores() {
    System.out.println(this.tempScoreArrayList.size());
    for(Pair<String,Integer> pair : tempScoreArrayList) {System.out.println(pair);}
    return this.tempScoreArrayList;
  }

  private void revealScoreList(ActionEvent event) {
    this.sendUsername = this.username.getText();
    updateUsername();
    //System.out.println(this.sendUsername);
    this.topPane.getChildren().remove(this.username);
    this.topPane.getChildren().remove(this.butt);
    Button butt2 = new Button("Play Again");
    butt2.setOnAction(this::startGame);
    this.topPane.getChildren().add(butt2);
    Text localScores = new Text("Local Scores");
    Text globalScores = new Text("Global Scores");

    this.observableRemoteScores = FXCollections.observableArrayList(
        getList(this.arrayListRemoteScores));
    this.remoteScoresElement = new SimpleListProperty<>(this.observableRemoteScores);
    this.remoteScoresList.getScoreProperty().bind(remoteScoresElement);
    this.scorePane.add(localScores,0,0);
    localScores.getStyleClass().add("heading");
    ColumnConstraints column = new ColumnConstraints(300);
    this.scorePane.getColumnConstraints().add(column);
    globalScores.getStyleClass().add("heading");
    this.scorePane.add(globalScores,1,0);
    this.scorePane.add(scoresList,0,1);
    this.scorePane.add(remoteScoresList,1,1);

    this.scoresList.reveal();
    this.remoteScoresList.reveal();
    this.comms.send("HISCORE "+this.username.getText()+":"+game.getScore());
  }

  private void updateUsername() {
    System.out.println(this.scoresList.getScoreProperty().size());
    for(Pair<String,Integer>pair : this.scoresList.getScoreProperty()) {
      System.out.println(pair);
    }
  }

  private int checkRemoteHighScore() {
    int index = -1;
    for(int i=0;i<this.arrayListRemoteScores.size();i++) {
      if(game.getScore()> this.arrayListRemoteScores.get(i).getValue()) {
        return  i;
      }
    } return index;
  }

  /**
   * loads score from server
   */
  public void receiveScore(String scores) {
    logger.info("New scores");
    if (scores.substring(0, 8).equals("HISCORES")) {
      System.out.println("uh oh");
      scores = scores.substring(9);
      String[] onlineScores = scores.split("\n");
      ArrayList<Pair<String, Integer>> arr = new ArrayList<>();
      boolean newHighScore = false;
      Pair<String, Integer> temp = null;
      for (int i = 0; i < 10; i++) {
        //System.out.println(i);
        System.out.println(newHighScore);
        if (!newHighScore) {
          var userScore = onlineScores[i].split(":");
          if (!userScore[1].equals("NaN")) {
            String user = userScore[0];
            int score = parseInt(userScore[1]);
            if (game.getScore() > score) {
              System.out.println("New High score!");
              newHighScore = true;
              temp = new Pair<>(user, score);
              this.tempScoreArrayList.add(new Pair<>(this.username.getText(), game.getScore()));
            } else {
              System.out.println("New Pair added");
              this.tempScoreArrayList.add(new Pair<>(user, score));
            }
          }
        } else {
          var userScore = onlineScores[i].split(":");
          if (!userScore[1].equals("NaN")) {
            String user = userScore[0];
            int score = parseInt(userScore[1]);
            this.tempScoreArrayList.add(temp);
            temp = new Pair<>(user, score);
          }
          for (Pair<String, Integer> p : tempScoreArrayList) {
            System.out.println(p);
          }
          //this.globalIndex = checkRemoteHighScore();
          //if (this.globalIndex != -1) {

        }
        //for(Pair<String,Integer> pair: arrayListRemoteScores) { System.out.println(pair);}
      } //this.arrayListRemoteScores = (ArrayList<Pair<String, Integer>>) this.arrayListRemoteScores.subList(0,9);
      //makes the scorelists visible
      //this.scorePane.getChildren().add(remoteScoresList);

    }
  }
  /**
   * This method goes through the text file and gets the scores
   * @return array of highscores
   */
  public ArrayList<Pair<String, Integer>> getHighScores() {
    logger.info("Getting High scores");
    try {
      File file = new File("highscores.txt");
      if(file.length() == 0){
        throw new IOException("");
      }
      ArrayList<Pair<String, Integer>> newScoreList = new ArrayList<>();
      FileReader reader = new FileReader("highscores.txt");
      BufferedReader br = new BufferedReader(reader);
      String line = br.readLine();
      while (line != null) {
        //System.out.println(line);
        String[] parts = line.split(":");
        String part1 = parts[0]; // Username
        int part2 = parseInt(parts[1]); // Score
        newScoreList.add(new Pair<String, Integer>(part1, part2));
        //this.scoresList.update();
        line = br.readLine();
      } br.close();
        return newScoreList;



    } catch (IOException e) {
      logger.info("No previous scores");
      ArrayList<Pair<String, Integer>> newScoreList = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        newScoreList.add(new Pair<String, Integer>("Paul", 10000-i*1000));
        //System.out.println(newScoreList.get(i));
      }
      return  newScoreList;

    }
    //return new ArrayList<Pair<String, Integer>>();
  }
  public List<Pair<String,Integer>> getList(ArrayList arrayList){
    return arrayList;
  }


  public void writeScores(ArrayList<Pair<String, Integer>> arrayList) {
    logger.info("Saving scores");
    try {
      FileWriter writer = new FileWriter("highscores.txt");
      for(int i = 0; i< arrayList.size();i++) {
        String partScore = arrayList.get(i).toString();
        String[] parts = partScore.split("=");
        String part1 = parts[0]; // Username
        String part2 = parts[1].toString(); // Score
        writer.write(part1+":"+part2);
        writer.write("\n");
        //System.out.println("Added:"+part1+","+part2);
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public int checkHighScore() {
    int index = -1;
    for(int i=0;i<this.arrayListScore.size();i++) {
      if(game.getScore()> this.arrayListScore.get(i).getValue()) {
        return  i;
      }
    } return index;
  }
}
