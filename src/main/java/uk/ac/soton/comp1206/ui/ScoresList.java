package uk.ac.soton.comp1206.ui;

import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

public class ScoresList extends VBox {
  private static final Logger logger = LogManager.getLogger(GameWindow.class);
  public SimpleListProperty<Pair<String,Integer>> scoreListProperty;
  public ArrayList<HBox> scoresContainer;

  public ScoresList() {
    this.scoreListProperty = new SimpleListProperty<Pair<String,Integer>>();
    this.scoresContainer = new ArrayList<>();
    this.setSpacing(10);
    this.setAlignment(Pos.CENTER_LEFT);
    scoreListProperty.addListener((ListChangeListener<? super Pair<String, Integer>>) e -> update());
  }

  public void reveal() {

    Timeline timeline = new Timeline();
    for(int i = 0;i<this.scoresContainer.size();i++) {
      timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i*200+200),
          new KeyValue(this.scoresContainer.get(i).opacityProperty(),1)));

    }
    timeline.play();

  }

  public void update() {
    logger.info("New score to be put into simplelist property");
    for (Pair<String, Integer> newScore : this.scoreListProperty) {
      System.out.println("Updating listproperty with"+newScore);
      //System.out.println("added:"+newScore);
      HBox scoreBox = new HBox();
      scoreBox.setOpacity(0.0);
      //scoreBox.setSpacing(20.0);
      Text userScore = new Text(newScore.getKey() + ":" + newScore.getValue().toString());
      userScore.setFill(GameBlock.COLOURS[this.scoreListProperty.indexOf(newScore)]);
      scoreBox.getChildren().add(userScore);
      userScore.getStyleClass().add("scorelist");
      this.getChildren().add(scoreBox);
      this.scoresContainer.add(scoreBox);

    }
    //this.reveal();
  }

  public ListProperty<Pair<String, Integer>> getScoreProperty() {
    return this.scoreListProperty;
  }


}
