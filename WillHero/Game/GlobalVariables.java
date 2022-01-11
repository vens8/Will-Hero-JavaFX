package Game;

import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GlobalVariables {
    // Music
    public static boolean music = true;  // false by default can be changed to true at the end (changed)
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
    public static Media playerDeath = new Media(new File("src/Resources/playerDeath.m4a").toURI().toString());
    public static MediaPlayer playerDeathSound = new MediaPlayer(playerDeath);  // Use when orc kills player
    public static Media weaponChestOpen = new Media(new File("src/Resources/weaponChestOpen.m4a").toURI().toString());
    public static MediaPlayer weaponChestOpenSound = new MediaPlayer(weaponChestOpen);
    public static Media coinChestOpen = new Media(new File("src/Resources/coinChestOpen.m4a").toURI().toString());
    public static MediaPlayer coinChestOpenSound = new MediaPlayer(coinChestOpen);
    public static Media swordKill = new Media(new File("src/Resources/swordDeath.mp3").toURI().toString());
    public static MediaPlayer swordKillSound = new MediaPlayer(swordKill);
    public static Media shurikenThrow = new Media(new File("src/Resources/shurikenThrow.m4a").toURI().toString());
    public static MediaPlayer shurikenThrowSound = new MediaPlayer(shurikenThrow);
    public static Media tntExplosion = new Media(new File("src/Resources/tntExplosion.m4a").toURI().toString());
    public static MediaPlayer tntExplosionSound = new MediaPlayer(tntExplosion);
    public static Media playerRevive = new Media(new File("src/Resources/playerRevive.m4a").toURI().toString());
    public static MediaPlayer playerReviveSound = new MediaPlayer(playerRevive);
    public static Media playerReviveThud = new Media(new File("src/Resources/playerReviveThud.m4a").toURI().toString());
    public static MediaPlayer playerReviveThudMusic = new MediaPlayer(playerReviveThud);
    public static Media coinCollect = new Media(new File("src/Resources/coinCollect.m4a").toURI().toString());
    public static MediaPlayer coinCollectSound = new MediaPlayer(coinCollect);
    public static Media orcDeath = new Media(new File("src/Resources/orcDeath.m4a").toURI().toString());
    public static MediaPlayer orcDeathSound = new MediaPlayer(orcDeath);
    public static Media bossOrcDeath = new Media(new File("src/Resources/bossDeath.m4a").toURI().toString());
    public static MediaPlayer bossOrcDeathSound = new MediaPlayer(bossOrcDeath);
    public static Media eerie = new Media(new File("src/Resources/eerieMusic.mp3").toURI().toString());
    public static MediaPlayer eerieMusic = new MediaPlayer(eerie);
    public static Media victory = new Media(new File("src/Resources/victory.m4a").toURI().toString());
    public static MediaPlayer victorySound = new MediaPlayer(victory);
    public static Media victoryMusic = new Media(new File("src/Resources/victoryMusic.m4a").toURI().toString());
    public static MediaPlayer victoryMusicPlay = new MediaPlayer(victoryMusic);


    // Visuals
    public static Media background = new Media(new File("src/Resources/hardMode.mp4").toURI().toString());
    public static MediaPlayer backgroundVideo = new MediaPlayer(background);

    // Main timeline
    public static Timeline timeline = new Timeline();

    // Sound
    public static boolean sound = true;

    // Game Data
    public static gameData gameData;
    public static Main game;
    public static int highscore;

    // Dimensions
    public static float SCREEN_WIDTH = 676, SCREEN_HEIGHT = 1000;

    // Level data
    public static ArrayList<GameObject> gameObjects = new ArrayList<>();
    public static double difficulty = 100;

    // Anchor Panes
    public static AnchorPane gameAnchorPane;
    public static AnchorPane staticAnchorPane;

    // Containers
    public static Stage mainMenuStage;
    public static Parent root;
    public static Scene scene;
    public static Stage stage;

    // Revive
    public static double lastJumpLocationX;  // Used to revive
    public static double lastJumpLocationY;
}
