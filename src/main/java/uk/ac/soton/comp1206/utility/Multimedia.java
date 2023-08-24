package uk.ac.soton.comp1206.utility;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

public class Multimedia {
    private static final Logger logger= LogManager.getLogger(Multimedia.class);

    private static MediaPlayer mediaPlayer;
    private static MediaPlayer musicPlayer;

    public static void playSound(String file) {
        String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        logger.info("Playing sound: " +toPlay);
        try{
            Media play = new Media(toPlay);
            mediaPlayer = new MediaPlayer(play);
            mediaPlayer.setVolume(0.6);
            mediaPlayer.play();
        } catch (Exception e) {
            logger.error("sound can't be played");
        }
    }
    public static void playMusic (String file) {
        String musicToPlay = Multimedia.class.getResource("/music/"+file).toExternalForm();
        logger.info("Playing music: " +musicToPlay);
        try{
            Media play = new Media(musicToPlay);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.play();
            musicPlayer.setVolume(1.2);
            musicPlayer.setCycleCount(-1);
        } catch (Exception e) {
            logger.error("music can't be played");
        }
    }

  public static void stop() {
        Multimedia.musicPlayer.stop();
  }
}
