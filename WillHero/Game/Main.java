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

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable, Serializable {
    private Player player;
    private int highScore;
    private int gameMode;

    @Override
    public void start(Stage primaryStage) {
        try {
            playMusic();
            System.out.println("Created!");
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
            GlobalVariables.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainMenu.fxml")));
            GlobalVariables.mainMenuStage = primaryStage;
            primaryStage.setTitle("Will Hero");
            GlobalVariables.scene = new Scene(GlobalVariables.root);
            primaryStage.setScene(GlobalVariables.scene);
            primaryStage.setResizable(true);  //Uncomment at the end!!
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
            gameState g = new gameState();
            String pattern = "HH_mm_ss__MM_dd_yyyy";

            DateFormat dateFormat = new SimpleDateFormat(pattern);

            Date today = Calendar.getInstance().getTime();
            String todayAsString = dateFormat.format(today);
            g.setDate(todayAsString);
            g.setGame(GlobalVariables.game);
            g.setCurrentLocationX(GlobalVariables.game.getPlayer().getHero().getHero().getLayoutX());
            g.setCurrentLocationY(GlobalVariables.game.getPlayer().getHero().getHero().getLayoutY());

            saveGameData(g);
            GlobalVariables.game.getPlayer().updateCoins();
            stage.close();
        }

    }

    private void saveGameData(gameState gameState) {
        try {
            FileOutputStream fileStream = new FileOutputStream("src/Resources/SavedGames/" + gameState.getDate() + ".txt");
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

            objectStream.writeObject(gameState);

            objectStream.close();
            fileStream.close();
        } catch(FileNotFoundException e){
            System.out.println("File not found!");
        } catch (IOException e) {
            e.printStackTrace();
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


    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        try {
            FileOutputStream fileStream = new FileOutputStream("src/Resources/GameData/highscore.txt");
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(highScore);
            objectStream.close();
            fileStream.close();
        } catch(FileNotFoundException e){
            System.out.println("File not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
