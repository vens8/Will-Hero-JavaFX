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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        GlobalVariables.game.getPlayer().updateCoins();
        if (!GlobalVariables.game.getPlayer().getHero().isKilled()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Main Menu");
            alert.setHeaderText("You're about to go to the main menu!");
            alert.setContentText("Do you want to save your progress before exiting?");

            if (alert.showAndWait().get() == ButtonType.OK) {
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

                // Close current Pause Menu stage
                stage = (Stage) (exitButtonBar.getScene().getWindow());
                stage.close();

                // Open Main Menu stage
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainMenu.fxml")));
                stage = GlobalVariables.mainMenuStage;
                stage.setScene(new Scene(root));
                stage.show();
            }
        }
        else {
            // Close current Pause Menu stage
            stage = (Stage) (exitButtonBar.getScene().getWindow());
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
        mainGameStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        gameController gameController = loader.getController();

        // Reset game/player properties
        game.getPlayer().setScore(0);
        game.getPlayer().setHero(new mainHero(50, 290));
        game.getPlayer().setRevived(false);
        gameController.resetFlags();
        GlobalVariables.gameData = new gameData(game.getGameMode());
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
    void saveGameClicked() throws IOException {
        // Save game
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loadGameMenu.fxml"));
        root = loader.load();
        gameState g = new gameState();
        String pattern = "HH_mm_ss__MM_dd_yyyy";

        DateFormat dateFormat = new SimpleDateFormat(pattern);

        Date today = Calendar.getInstance().getTime();
        String todayAsString = dateFormat.format(today);
        g.setDate(todayAsString);
        g.setGame(game);
        g.setCurrentLocationX(game.getPlayer().getHero().getHero().getLayoutX());
        g.setCurrentLocationY(game.getPlayer().getHero().getHero().getLayoutY());

        saveGameData(g);
        game.getPlayer().updateCoins();
        loadGameController controller = loader.getController();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Game");
        alert.setHeaderText("Game saved");
        alert.setContentText("Your game progress has been saved successfully!");
        alert.showAndWait();
    }

    public void saveGameData(gameState gameState) throws IOException {  // Change to serialize and the loading part to deserialize
        try {
            System.out.println(gameState.getDate());
            FileOutputStream fileStream = new FileOutputStream("src/Resources/SavedGames/" + gameState.getDate() + ".txt");
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

            objectStream.writeObject(gameState);

            objectStream.close();
            fileStream.close();
        } catch(FileNotFoundException e){
            System.out.println("File not found!");
        }
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
