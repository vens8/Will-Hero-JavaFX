package Game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class settingsController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ButtonBar musicButtonBar;
    @FXML
    private ButtonBar soundButtonBar;
    @FXML
    private ButtonBar saveGameButtonBar;
    @FXML
    private ImageView musicIcon;
    @FXML
    private ImageView soundIcon;
    @FXML
    private Label musicLabel;
    @FXML
    private Label soundLabel;

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

    public void musicClicked() {
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

    public void soundClicked() {
        if(GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();

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

    public void saveGameClicked() throws IOException {
        // Save game
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loadGameMenu.fxml"));
        root = loader.load();
        gameState g = new gameState();
        g.setDate(new Date());
        Controller controller = loader.getController();
        controller.addSavedGame(g);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Game");
        alert.setHeaderText("Game saved");
        alert.setContentText("Your game progress has been saved successfully!");
        alert.showAndWait();
    }

    public void musicMouseEntered() {
        glow.setLevel(0.5f);
        musicButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    public void musicMouseExited() {
        musicButtonBar.setEffect(null);
    }

    public void soundMouseEntered() {
        glow.setLevel(0.5f);
        soundButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    public void soundMouseExited() {
        soundButtonBar.setEffect(null);
    }

    public void saveGameMouseEntered() {
        glow.setLevel(0.5f);
        saveGameButtonBar.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    public void saveGameMouseExited() {
        saveGameButtonBar.setEffect(null);
    }
}
