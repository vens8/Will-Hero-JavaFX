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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {
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
    private ImageView homeButton;
    @FXML
    private ImageView playGameButton;
    @FXML
    private ListView<gameState> loadGameListView;
    @FXML
    public Label dummyLabel;
    @FXML
    private Label coinsLabel;
    @FXML
    private Label highscoreLabel;
    @FXML
    private AnchorPane mainMenuAnchorPane;
    @FXML
    private AnchorPane playGameAnchorPane;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
    InnerShadow innerShadow = new InnerShadow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException{
        try {
            Animations.fadeTransition(mainMenuAnchorPane, 0d, 1d, 1500d, 1, false).play();
            Animations.fadeTransition(playGameAnchorPane, 0.5, 1d, 500d, 1, false).play();
            loadGameListView.getItems().addAll(GlobalVariables.gameStates);  // Display all the game states in the listView
        }
        catch (NullPointerException e) {
            System.out.println("Game States is empty!");
            System.out.println(e);
        }
    }

    public void settingsButtonClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        stage = new Stage();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("settings.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        stage.initOwner(settingsButton.getScene().getWindow());
        stage.showAndWait();
    }

    public void newGameClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        GlobalVariables.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("playGame.fxml")));
        stage = (Stage)(newGameButton.getScene().getWindow());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        stage.setScene(GlobalVariables.scene);

        stage.show();
    }

    public void loadGameClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        stage = new Stage();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("loadGameMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(loadGameButton.getScene().getWindow());
        stage.showAndWait();
    }

    public void addSavedGame(gameState g) {
        GlobalVariables.gameStates.add(g);
        for (int i = 0; i < GlobalVariables.gameStates.size(); i++) {
            System.out.println(GlobalVariables.gameStates.get(i).getDate());
        }
    }

    public void exitGameClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("You're about to exit!");
        alert.setContentText("Do you want to save your progress before exiting?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            // Insert code to save game state

            stage = (Stage)(exitGameButton.getScene().getWindow());
            stage.close();
        }

    }

    public void newGameMouseEntered() {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
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
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
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
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
        glow.setLevel(0.5f);
        exitGameButton.setEffect(glow);
    }

    public void exitGameMouseExited() {
        glow.setLevel(0f);
        innerShadow.setWidth(4.62);
        innerShadow.setHeight(8.13);
        exitGameButton.setEffect(innerShadow);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGame.fxml"));
        GlobalVariables.root = loader.load();
        stage = (Stage) (playGameButton.getScene().getWindow());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        gameController gameController = loader.getController();
        gameController.activateKeyListener();
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
