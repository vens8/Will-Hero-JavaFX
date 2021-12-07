package Game;

import com.sun.scenario.effect.Glow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class settingsController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ButtonBar musicButtonBar;
    @FXML
    private ButtonBar soundButtonBar;
    @FXML
    private ButtonBar settingButtonBar;
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


    public void musicClicked() {
        if(musicLabel.getText().equalsIgnoreCase("Music On")) {
            musicIcon.setImage(musicOff);
            musicLabel.setText("Music Off");
        }
        else {
            musicIcon.setImage(musicOn);
            musicLabel.setText("Music On");
        }
    }

    public void soundClicked() {
        if(soundLabel.getText().equalsIgnoreCase("Sound On")) {
            soundIcon.setImage(soundOff);
            soundLabel.setText("Sound Off");
        }
        else {
            soundIcon.setImage(soundOn);
            soundLabel.setText("Sound On");
        }
    }

    public void settingClicked() {

    }

    public void musicMouseEntered() {
        glow.setLevel(0.5f);
        musicButtonBar.setEffect(glow);
    }

    public void musicMouseExited() {
        musicButtonBar.setEffect(null);
    }

    public void soundMouseEntered() {
        glow.setLevel(0.5f);
        soundButtonBar.setEffect(glow);
    }

    public void soundMouseExited() {
        soundButtonBar.setEffect(null);
    }

    public void settingMouseEntered() {
        glow.setLevel(0.5f);
        settingButtonBar.setEffect(glow);
    }

    public void settingMouseExited() {
        settingButtonBar.setEffect(null);
    }
}
