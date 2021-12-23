package Game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class GlobalVariables {
    // Music
    public static boolean music = false;
    public static Media gameMusic = new Media(Paths.get("src/Resources/gameMusic.mp3").toUri().toString());
    public static MediaPlayer mediaPlayer = new MediaPlayer(gameMusic);
    public static Media buttonClick = new Media(new File("src/Resources/buttonClick.m4a").toURI().toString());
    public static MediaPlayer buttonClickSound = new MediaPlayer(buttonClick);
    public static Media buttonHover = new Media(new File("src/Resources/buttonHover.m4a").toURI().toString());
    public static MediaPlayer buttonHoverSound = new MediaPlayer(buttonHover);
    public static Media playerJump = new Media(new File("src/Resources/jumpPop.mp3").toURI().toString());
    public static MediaPlayer playerJumpSound = new MediaPlayer(playerJump);
    public static Media playerLeap = new Media(new File("src/Resources/leapSwish.m4a").toURI().toString());
    public static MediaPlayer playerLeapSound = new MediaPlayer(playerLeap);
    public static Media playerFall = new Media(new File("src/Resources/playerFall.m4a").toURI().toString());
    public static MediaPlayer playerFallSound = new MediaPlayer(playerFall);
    public static Media playerDeath = new Media(new File("src/Resources/playerDeath.mp3").toURI().toString());
    public static MediaPlayer playerDeathSound = new MediaPlayer(playerDeath);

    // Sound
    public static boolean sound = true;

    // Game States
    public static ObservableList<gameState> gameStates = FXCollections.observableArrayList();

    // Dimensions
    public static float SCREEN_WIDTH = 676, SCREEN_HEIGHT = 1000;

    // Containers
    public static Stage mainMenuStage;
    public static Parent root;
    public static Scene scene;
    public static Stage stage;
}
