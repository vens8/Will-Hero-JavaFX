package Game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class playController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView settingsButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView playGameButton;
    @FXML
    private AnchorPane playGameAnchorPane;
    @FXML
    private ComboBox<String> chooseDifficulty;

    private String[] difficultyList = {"Easy", "Normal", "Hard"};

    private Main game;
    private Player player;
    private Stage mainGameStage;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException {
        chooseDifficulty.getItems().addAll(difficultyList);
        chooseDifficulty.setValue("Normal");
        Animations.fadeTransition(playGameAnchorPane, 0.5, 1d, 500d, 1, false).play();
    }

    public void getGame(Main game) {
        this.game = game;
        this.player = game.getPlayer();
    }
    
    public void settingsButtonClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        root = loader.load();
        stage.setScene(new Scene(root));
        settingsController settingsController = loader.getController();
        settingsController.getGame(game);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        stage.initOwner(settingsButton.getScene().getWindow());
        stage.showAndWait();
    }

    public void homeButtonClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainMenu.fxml")));
        stage = (Stage)(homeButton.getScene().getWindow());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void playGameClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        if (chooseDifficulty.getValue().equals("Easy")) {
            game.setGameMode(1);
            gameData gameData = new gameData(game.getGameMode());  // All objects created and loaded into GlobalVariables.gameObjects to prevent delay.
            GlobalVariables.difficulty = 200;
        }
        else if (chooseDifficulty.getValue().equals("Normal")) {
            game.setGameMode(1);
            gameData gameData = new gameData(game.getGameMode());  // All objects created and loaded into GlobalVariables.gameObjects to prevent delay.
            GlobalVariables.difficulty = 100;
        }
        else {
            game.setGameMode(2);
            gameData gameData = new gameData(game.getGameMode());  // All objects created and loaded into GlobalVariables.gameObjects to prevent delay.
            GlobalVariables.difficulty = 50;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGame.fxml"));
        GlobalVariables.root = loader.load();
        stage = (Stage) (playGameButton.getScene().getWindow());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        gameController gameController = loader.getController();
        gameController.setupScene(game);
        stage.setScene(GlobalVariables.scene);
        stage.show();
    }

    public void playGameMouseEntered() {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
        glow.setLevel(0.5f);
        playGameButton.setEffect(glow);
    }

    public void playGameMouseExited() {
        playGameButton.setEffect(null);
    }

    public void settingsMouseEntered() {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
        glow.setLevel(0.5f);
        settingsButton.setEffect(glow);
    }

    public void settingsMouseExited() {
        settingsButton.setEffect(null);
    }

    public void homeMouseEntered() {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
        glow.setLevel(0.5f);
        homeButton.setEffect(glow);
    }

    public void homeMouseExited() {
        homeButton.setEffect(null);
    }
}
