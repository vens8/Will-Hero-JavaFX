package Game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private ListView<gameState> loadGameListView;
    @FXML
    private Label coinsLabel;
    @FXML
    private Label highscoreLabel;
    @FXML
    private AnchorPane mainMenuAnchorPane;

    private Main game;
    private Player player;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
    InnerShadow innerShadow = new InnerShadow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException {
        game = new Main();
        player = new Player(new mainHero(50, 290)); // starting position (50, 290)
        game.setPlayer(player);
        highscoreLabel.setText(String.format("%d", game.getHighScore()));
        coinsLabel.setText(String.format("%d", game.getPlayer().getCoins()));
        try {
            Animations.fadeTransition(mainMenuAnchorPane, 0d, 1d, 1500d, 1, false).play();
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

    public void newGameClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("playGame.fxml"));
        GlobalVariables.root = loader.load();
        stage = (Stage)(newGameButton.getScene().getWindow());
        playController playController = loader.getController();
        playController.getGame(game);  // Passing the Main object to the playController
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        stage.setScene(GlobalVariables.scene);

        stage.show();
    }

    public void loadGameClicked() throws IOException, ClassNotFoundException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }

        try {
            FileInputStream fileStream = new FileInputStream("/Resources/savedGames.txt");
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            gameState savedState = (gameState) objectStream.readObject();
        }
        catch (FileNotFoundException f) {
            System.out.println("not found!");
        }

        //this is where the magic happens :)

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

    public void displayCoins() {

    }

    public void displayHighscore() {

    }
}
