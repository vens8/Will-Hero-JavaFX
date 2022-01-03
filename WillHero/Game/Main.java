package Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable, Serializable {
    public static Stage myStage;
    private Player player;
    private int highScore;
    private int gameMode;

    @Override
    public void start(Stage primaryStage) {
            try {
                highScore = 0;
                playMusic();
                System.out.println("Created!");
                primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
                GlobalVariables.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainMenu.fxml")));
                GlobalVariables.mainMenuStage = primaryStage;
                primaryStage.setTitle("Will Hero");
                GlobalVariables.scene = new Scene(GlobalVariables.root);
                primaryStage.setScene(GlobalVariables.scene);
                primaryStage.setResizable(false);  //Uncomment at the end!!
                primaryStage.show();
                primaryStage.setOnCloseRequest(event -> {
                    try {
                        event.consume();
                        exitGameClicked(primaryStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void exitGameClicked(Stage stage) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("You're about to exit!");
        alert.setContentText("Do you want to save your progress before exiting?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            // Insert code to save game state
            stage.close();
        }

    }
    public void playMusic() {
        GlobalVariables.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        GlobalVariables.mediaPlayer.play();
        GlobalVariables.mediaPlayer.setVolume(0.75);
        GlobalVariables.music = true;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getMyStage() {
        return myStage;
    }

    public static void setMyStage(Stage myStage) {
        Main.myStage = myStage;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
