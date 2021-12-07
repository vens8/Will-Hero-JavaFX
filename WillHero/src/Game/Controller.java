package Game;

import com.sun.scenario.effect.Glow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Controller {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView newGameButton;
    @FXML
    private ImageView loadGameButton;
    @FXML
    private ImageView exitGameButton;
    @FXML
    private ImageView settingsButton;
    @FXML
    private Label coinsLabel;
    @FXML
    private Label highscoreLabel;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
    InnerShadow innerShadow = new InnerShadow();
    //Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/cchest.gif")));

    public void settingsButtonClicked() throws IOException {
        stage = new Stage();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("settings.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(settingsButton.getScene().getWindow());
        stage.showAndWait();
    }

    public void newGameClicked() throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("playGame.fxml")));
        stage = (Stage)(newGameButton.getScene().getWindow());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loadGameClicked() throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("playGame.fxml")));
        stage = (Stage)(loadGameButton.getScene().getWindow());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exitGameClicked() throws IOException {
        stage = (Stage)(exitGameButton.getScene().getWindow());
        stage.close();
    }

    public void newGameMouseEntered() {
        glow.setLevel(0.5f);
        newGameButton.setEffect(glow);
    }

    public void newGameMouseExited() {
        glow.setLevel(0f);
        innerShadow.setWidth(4.62);
        innerShadow.setHeight(8.13);
        newGameButton.setEffect(innerShadow);
    }

    public void loadGameMouseEntered() {
        glow.setLevel(0.5f);
        loadGameButton.setEffect(glow);
    }

    public void loadGameMouseExited() {
        glow.setLevel(0f);
        innerShadow.setWidth(4.62);
        innerShadow.setHeight(8.13);
        loadGameButton.setEffect(innerShadow);
    }

    public void exitGameMouseEntered() {
        glow.setLevel(0.5f);
        exitGameButton.setEffect(glow);
    }

    public void exitGameMouseExited() {
        glow.setLevel(0f);
        innerShadow.setWidth(4.62);
        innerShadow.setHeight(8.13);
        exitGameButton.setEffect(innerShadow);
    }
}
