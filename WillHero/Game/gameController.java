package Game;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class gameController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView redOrc1;
    @FXML
    private ImageView greenOrc1;
    @FXML
    private ImageView chest1;
    @FXML
    private ImageView bossOrc1;
    @FXML
    private ImageView princess;
    @FXML
    private ImageView smallPlatform1;
    @FXML
    private ImageView mediumPlatform1;
    @FXML
    private ImageView bigPlatform1;
    @FXML
    private ImageView settingsButton;

    private ArrayList<GameObject> gameObjects;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make another transition method by polymorphism that takes in other arguments
        //Animations.translateTransition(player, 0, -130, 400, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(redOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(greenOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(chest1, 0, -5, 250, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(princess, 0, -100, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(bossOrc1, 0, -30, 1000, TranslateTransition.INDEFINITE, true).play();
    }

    public void settingsButtonClicked() throws IOException {
        stage = new Stage();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("pauseMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(settingsButton.getScene().getWindow());
        stage.showAndWait();
    }

    public void settingsMouseEntered() {
        glow.setLevel(0.5f);
        settingsButton.setEffect(glow);
    }

    public void settingsMouseExited() {
        settingsButton.setEffect(null);
    }
}
