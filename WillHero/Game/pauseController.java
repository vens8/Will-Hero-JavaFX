package Game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class pauseController implements Initializable {
    private Scene scene;
    private Stage stage, mainGameStage;
    private Parent root;

    @FXML
    private ButtonBar musicButtonBar;

    @FXML
    private ImageView musicIcon;

    @FXML
    private Label musicLabel;

    @FXML
    private ButtonBar restartGameButtonBar;

    @FXML
    private ButtonBar resumeGameButtonBar;

    @FXML
    private ButtonBar saveGameButtonBar;

    @FXML
    private ButtonBar soundButtonBar;

    @FXML
    private ButtonBar exitButtonBar;

    @FXML
    private ImageView soundIcon;

    @FXML
    private Label soundLabel;

    private Main game;
    private Player player;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
    Image musicOn = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/musicon.png")));
    Image musicOff = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/musicoff.png")));
    Image soundOn = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/soundon.png")));
    Image soundOff = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/soundoff.png")));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(GlobalVariables.music) {
            musicIcon.setImage(musicOn);
            musicLabel.setText("Music On");
            GlobalVariables.music = true;
        }
        else {
            musicIcon.setImage(musicOff);
            musicLabel.setText("Music Off");
            GlobalVariables.music = false;
        }

        if(GlobalVariables.sound) {
            soundIcon.setImage(soundOn);
            soundLabel.setText("Sound On");
            GlobalVariables.sound = true;
        }
        else {
            soundIcon.setImage(soundOff);
            soundLabel.setText("Sound Off");
            GlobalVariables.sound = false;
        }
    }

    @FXML
    void exitClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Main Menu");
        alert.setHeaderText("You're about to go to the main menu!");
        alert.setContentText("Do you want to save your progress before exiting?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            // Insert code to save game state

            // Close current Pause Menu stage
            stage = (Stage)(exitButtonBar.getScene().getWindow());
            stage.close();

            // Open Main Menu stage
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainMenu.fxml")));
            stage = GlobalVariables.mainMenuStage;
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    @FXML
    void exitMouseEntered() {
        glow.setLevel(0.5f);
        exitButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void exitMouseExited() {
        exitButtonBar.setEffect(null);
    }

    @FXML
    void musicClicked() {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        if(GlobalVariables.music) {
            GlobalVariables.mediaPlayer.pause();
            musicIcon.setImage(musicOff);
            musicLabel.setText("Music Off");
            GlobalVariables.music = false;
        }
        else {
            GlobalVariables.mediaPlayer.play();
            musicIcon.setImage(musicOn);
            musicLabel.setText("Music On");
            GlobalVariables.music = true;
        }
    }

    @FXML
    void musicMouseEntered() {
        glow.setLevel(0.5f);
        musicButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void musicMouseExited() {
        musicButtonBar.setEffect(null);
    }

    @FXML
    void restartClicked() throws IOException {
        stage.close();

        // Reopen game stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGame.fxml"));
        GlobalVariables.root = loader.load();
        //stage = (Stage) (restartGameButtonBar.getScene().getWindow());
        mainGameStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        gameController gameController = loader.getController();

        // Reset player properties
        player.setScore(0);
        player.setHero(new mainHero(27, 290));
        player.setRevived(false);
        gameController.setupScene(game);
        mainGameStage.setScene(GlobalVariables.scene);
        mainGameStage.show();
    }

    @FXML
    void restartMouseEntered() {
        glow.setLevel(0.5f);
        restartGameButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void restartMouseExited() {
        restartGameButtonBar.setEffect(null);
    }

    @FXML
    void resumeClicked() {
        GlobalVariables.timeline.play();
        try {
            stage.close();
        }
        catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    @FXML
    void resumeMouseEntered() {
        glow.setLevel(0.5f);
        resumeGameButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void resumeMouseExited() {
        resumeGameButtonBar.setEffect(null);
    }

    @FXML
    void saveGameClicked() {

    }

    @FXML
    void saveGameMouseEntered() {
        glow.setLevel(0.5f);
        saveGameButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void saveGameMouseExited() {
        saveGameButtonBar.setEffect(null);
    }

    @FXML
    void soundClicked() {
        if(GlobalVariables.sound) {
            soundIcon.setImage(soundOff);
            soundLabel.setText("Sound Off");
            GlobalVariables.sound = false;
        }
        else {
            soundIcon.setImage(soundOn);
            soundLabel.setText("Sound On");
            GlobalVariables.sound = true;
        }
    }

    @FXML
    void soundMouseEntered() {
        glow.setLevel(0.5f);
        soundButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void soundMouseExited() {
        soundButtonBar.setEffect(null);
    }

    public void setup(Stage stage, Stage mainGameStage, Main game) {
        this.game = game;
        this.player = this.game.getPlayer();
        this.stage = stage;
        this.mainGameStage = mainGameStage;
    }

    public Stage getStage() {
        return stage;
    }
}
