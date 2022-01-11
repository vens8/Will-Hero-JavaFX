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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {
    private Stage stage;
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
    @FXML
    private AnchorPane mainMenuAnchorPane;
    private Player player;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
    InnerShadow innerShadow = new InnerShadow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException {
        if (GlobalVariables.game == null) {
            GlobalVariables.game = new Main();
            player = new Player(new mainHero(50, 290)); // starting position (50, 290)
            GlobalVariables.game.setPlayer(player);
            setCoins();
        }
        displayCoins();
        displayHighscore();
        Animations.fadeTransition(mainMenuAnchorPane, 0d, 1d, 1500d, 1, false).play();
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
        settingsController.getGame(GlobalVariables.game);
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
        player = GlobalVariables.game.getPlayer();
        GlobalVariables.game.getPlayer().setHero(new mainHero(50, 290)); // starting position (50, 290)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("playGame.fxml"));
        GlobalVariables.root = loader.load();
        stage = (Stage)(newGameButton.getScene().getWindow());
        playController playController = loader.getController();
        playController.getGame(GlobalVariables.game);  // Passing the Main object to the playController
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

        Stage loadStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loadGameMenu.fxml"));
        root = loader.load();
        loadStage.setScene(new Scene(root));
        loadGameController loadGameController = loader.getController();
        loadGameController.setup(loadStage, stage);
        loadStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        loadStage.initModality(Modality.APPLICATION_MODAL);
        loadStage.initOwner(loadGameButton.getScene().getWindow());
        loadStage.showAndWait();
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

            stage = (Stage)(exitGameButton.getScene().getWindow());
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

    public void setCoins() {
        try {
            FileInputStream fileStream = new FileInputStream("src/Resources/GameData/coins.txt");
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            GlobalVariables.game.getPlayer().setCoins((long) objectStream.readObject());
        }
        catch (FileNotFoundException f) {
            System.out.println("coins.txt not found!");
        }
        catch (ClassNotFoundException | IOException c) {
            System.out.println("Class not found!");
        }
    }

    public void displayCoins() {
        coinsLabel.setText(String.format("%d", GlobalVariables.game.getPlayer().getCoins()));
    }

    public void displayHighscore() {
        try {
            FileInputStream fileStream = new FileInputStream("src/Resources/GameData/highscore.txt");
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            GlobalVariables.highscore = (int) objectStream.readObject();
            highscoreLabel.setText(String.format("%d", GlobalVariables.highscore));
        }
        catch (FileNotFoundException f) {
            System.out.println("highscore.txt not found!");
        }
        catch (ClassNotFoundException | IOException c) {
            System.out.println("Class not found!");
        }
    }
}
