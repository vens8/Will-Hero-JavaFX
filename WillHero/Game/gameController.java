package Game;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class gameController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView player;
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

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TranslateTransition translatePlayer = new TranslateTransition();
        translatePlayer.setNode(player);
        translatePlayer.setDuration(Duration.millis(400));
        translatePlayer.setCycleCount(TranslateTransition.INDEFINITE);
        translatePlayer.setByY(-130);
        translatePlayer.setAutoReverse(true);
        translatePlayer.play();

        TranslateTransition translateRedOrc1 = new TranslateTransition();
        translateRedOrc1.setNode(redOrc1);
        translateRedOrc1.setDuration(Duration.millis(500));
        translateRedOrc1.setCycleCount(TranslateTransition.INDEFINITE);
        translateRedOrc1.setByY(-160);
        translateRedOrc1.setAutoReverse(true);
        translateRedOrc1.play();

        TranslateTransition translateGreenOrc1 = new TranslateTransition();
        translateGreenOrc1.setNode(greenOrc1);
        translateGreenOrc1.setDuration(Duration.millis(500));
        translateGreenOrc1.setCycleCount(TranslateTransition.INDEFINITE);
        translateGreenOrc1.setByY(-160);
        translateGreenOrc1.setAutoReverse(true);
        translateGreenOrc1.play();

        TranslateTransition translateChest1 = new TranslateTransition();
        translateChest1.setNode(chest1);
        translateChest1.setDuration(Duration.millis(250));
        translateChest1.setCycleCount(TranslateTransition.INDEFINITE);
        translateChest1.setByY(-5);
        translateChest1.setAutoReverse(true);
        translateChest1.play();

        TranslateTransition translatePrincess = new TranslateTransition();
        translatePrincess.setNode(princess);
        translatePrincess.setDuration(Duration.millis(500));
        translatePrincess.setCycleCount(TranslateTransition.INDEFINITE);
        translatePrincess.setByY(-100);
        translatePrincess.setAutoReverse(true);
        translatePrincess.play();

        TranslateTransition translateBossOrc = new TranslateTransition();
        translateBossOrc.setNode(bossOrc1);
        translateBossOrc.setDuration(Duration.millis(1000));
        translateBossOrc.setCycleCount(TranslateTransition.INDEFINITE);
        translateBossOrc.setByY(-30);
        translateBossOrc.setAutoReverse(true);
        translateBossOrc.play();
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
