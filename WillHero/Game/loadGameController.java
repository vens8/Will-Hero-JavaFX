package Game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class loadGameController implements Initializable {
    @FXML
    private ListView<String> loadGameListView;

    @FXML
    private ImageView loadStateButton;
    private Main game;
    private Player player;
    private Stage stage;
    private Stage mainStage;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
    InnerShadow innerShadow = new InnerShadow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException {
        loadGames();
    }

    public void loadGames() {
        System.out.println("Is null list: " + loadGameListView);

        File savedGames = new File("src/Resources/SavedGames");
        //List of all files and directories
        String savedGamesList[] = savedGames.list();
        //Arrays.sort(savedGamesList, Collections.reverseOrder());
        try {
            loadGameListView.getItems().addAll(savedGamesList);  // Display all the game states in the listView
        }
        catch (NullPointerException e) {
            System.out.println("Game States is empty!");
            System.out.println(e);
        }
    }

    @FXML
    void loadStateButtonClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        String fileName = loadGameListView.getSelectionModel().getSelectedItem();
        try {
            FileInputStream fileStream = new FileInputStream("src/Resources/SavedGames/" + fileName);
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            gameState savedState = (gameState) objectStream.readObject();
            GlobalVariables.game = savedState.getGame();
            game = GlobalVariables.game;
            this.player = GlobalVariables.game.getPlayer();
            player.setHero(new mainHero(savedState.getCurrentLocationX(), savedState.getCurrentLocationY()));
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("playGame.fxml"));
            GlobalVariables.root = loader.load();
            playController playController = loader.getController();
            playController.getGame(GlobalVariables.game);  // Passing the game to the playController (redundant, will fix later)
            GlobalVariables.mainMenuStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
            GlobalVariables.scene = new Scene(GlobalVariables.root);
            GlobalVariables.mainMenuStage.setScene(GlobalVariables.scene);

            GlobalVariables.mainMenuStage.show();

        }
        catch (FileNotFoundException f) {
            System.out.println("saved game state not found!");
        }
        catch (ClassNotFoundException c) {
            System.out.println("Class not found!");
        }
    }

    @FXML
    void loadStateButtonEntered() {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
        glow.setLevel(0.5f);
        loadStateButton.setEffect(glow);
    }

    @FXML
    void loadStateButtonExited() {
        glow.setLevel(0f);
        innerShadow.setWidth(4.62);
        innerShadow.setHeight(8.13);
        loadStateButton.setEffect(innerShadow);
    }

    public void setup(Stage loadStage, Stage stage) {
        this.stage = loadStage;
        this.mainStage = stage;
        System.out.println("Is null mainstage setup: " + mainStage);
    }
}
