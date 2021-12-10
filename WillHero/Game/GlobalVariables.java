package Game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class GlobalVariables {
    // Music
    public static Media gameMusic = new Media(Paths.get("src/Resources/gameMusic.mp3").toUri().toString());
    public static MediaPlayer mediaPlayer = new MediaPlayer(gameMusic);
    public static boolean music = false;

    // Sound
    public static boolean sound = true;

    // Game States
    public static ObservableList<gameState> gameStates = FXCollections.observableArrayList();

    public static Stage mainMenuStage;
}
