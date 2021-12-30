package Game;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class gameController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ImageView bossOrc1;
    @FXML
    private ImageView princess;
    @FXML
    private ImageView settingsButton;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private AnchorPane gameAnchorPane;
    @FXML
    private AnchorPane bgAnchorPane;
    @FXML
    private AnchorPane staticAnchorPane;
    @FXML
    private AnchorPane gameOverAnchorPane;
    @FXML
    private Label fpsLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label coinLabel;
    @FXML
    private Group reviveButton;
    @FXML
    private Group restartButton;
    @FXML
    private Group exitButton;
    @FXML
    private Label shurikenLevel;
    @FXML
    private Label swordLevel;
    @FXML
    private ImageView shurikenImage;
    @FXML
    private ImageView swordImage;

    private Camera camera;
    private Main game;
    private Player player;
    private coinChest chest;
    private weaponChest chest2, chest3;
    private TNT tnt;
    private Coin coin1, coin2, coin3;
    private redOrc redOrc;
    private greenOrc greenOrc;
    private bossOrc bossOrc;
    private boolean gameStarted;
    private ArrayList<GameObject> gameObjects;
    private ArrayList<Platform> platforms;
    private ArrayList<Chest> chests;
    private ArrayList<Orc> orcs;
    private ArrayList<TNT> TNTs;
    private ArrayList<Coin> coins;
    private smallPlatform smallPlatform;
    private mediumPlatform mediumPlatform;
    private bigPlatform bigPlatform;

    // FPS computation
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false;
    long now;  // Current time

    GaussianBlur gaussianBlur = new GaussianBlur();
    Glow glow = new Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make another transition method by polymorphism that takes in other arguments if needed
        GlobalVariables.gameAnchorPane = gameAnchorPane;
        // Use reference of Main game here.
        camera = new Camera(0, 0);
        gameObjects = new ArrayList<>();
        mediumPlatform = new mediumPlatform(-18, 208);
        //mediumPlatform.addToScreen(gameAnchorPane);
        smallPlatform = new smallPlatform(287, 252);
        //smallPlatform.addToScreen(gameAnchorPane);
        bigPlatform = new bigPlatform(559, 226);
        //bigPlatform.addToScreen(gameAnchorPane);
        chest = new coinChest(347, 249, 10);  // test
        //chest.addToScreen(gameAnchorPane);

        chest2 = new weaponChest(500, 211, 0);  // test
        //chest2.addToScreen(gameAnchorPane);
        chest3 = new weaponChest(575, 211, 0);
        //chest3.addToScreen(gameAnchorPane);
        tnt = new TNT(2000, 265);
        //tnt.addToScreen(gameAnchorPane);
        coin1 = new Coin(930, 236);
        coin2 = new Coin(960, 236);
        coin3 = new Coin(990, 236);
        //coin1.addToScreen(gameAnchorPane);
        //coin2.addToScreen(gameAnchorPane);
        //coin3.addToScreen(gameAnchorPane);
        redOrc = new redOrc(125, 288);
        //redOrc.addToScreen(gameAnchorPane);
        greenOrc = new greenOrc(1500, 288);  // 650
        //greenOrc.addToScreen(gameAnchorPane);
        bossOrc = new bossOrc(850, 173);
        //bossOrc.addToScreen(gameAnchorPane);
        System.out.println("total size: " + GlobalVariables.gameObjects.size());

        gameStarted = true;  // local to the game

        GlobalVariables.timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> run()));  // Run a frame every 10 milliseconds (100 fps)
        GlobalVariables.timeline.setCycleCount(Timeline.INDEFINITE);

        // Add this for the final stage after beating boss (cinematic effect)
        // Animations.translateTransition(princess, 0, -100, 500, TranslateTransition.INDEFINITE, true).play();
        GlobalVariables.timeline.play();
    }

    public void settingsButtonClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        GlobalVariables.timeline.pause();  // Pause the game on clicking the settings button
//        stage = new Stage();
//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("pauseMenu.fxml")));
//        stage.setScene(new Scene(root));
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.initOwner(settingsButton.getScene().getWindow());
//        pauseController.setStage(stage, game);  // Sending stage so it can be closed from pauseController after resuming
//        stage.setOnCloseRequest(event -> GlobalVariables.timeline.play());  // Resume game when settings is closed
//        stage.showAndWait();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("pauseMenu.fxml"));
        root = loader.load();
        Stage mainGameStage = (Stage) (fpsLabel.getScene().getWindow());  // mainGame stage in which new game is opened
        stage = new Stage();  // Pause menu stage to close
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(settingsButton.getScene().getWindow());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        scene = new Scene(root);
        pauseController pauseController = loader.getController();
        pauseController.setup(stage, mainGameStage, game);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> GlobalVariables.timeline.play());
        stage.showAndWait();
    }

    public void settingsMouseEntered() {
        glow.setLevel(0.5f);
        settingsButton.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }
    public void settingsMouseExited() {
        settingsButton.setEffect(null);
    }

    public void reviveButtonClicked() {
        if (player.getCoins() >= 10) {  // 50 coins to revive
            player.setRevived(true);

            gaussianBlur.setRadius(0);
            Animations.translateTransition(gameOverAnchorPane, 0, 550, 500, 1, false).play();
            Animations.translateTransition(scoreLabel, 0, -125, 500, 1, false).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(gameAnchorPane.effectProperty(), gaussianBlur)),
                    new KeyFrame(Duration.millis(250), new KeyValue(bgAnchorPane.effectProperty(), gaussianBlur)),
                    new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleXProperty(), 1)),
                    new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleYProperty(), 1)),
                    new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.textFillProperty(), Color.rgb(97, 90, 90))));
            timeline.play();
            System.out.println("Game over!");  // Make game over scene and fade transition (bring down opacity of the game scene)
            //GlobalVariables.timeline.pause();
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            // Move anchor pane a little forward and place player on a platform
            System.out.println("here");
            player.setHero(new mainHero(player.getHero().getHero().getLayoutX() - 200, player.getHero().getHero().getLayoutY() + 800));
            GlobalVariables.timeline.setRate(1);  // reset rate
            gameStarted = true;
            //GlobalVariables.timeline.play();
            GlobalVariables.playerReviveSound.stop();
            GlobalVariables.playerReviveSound.play();
        }
    }

    public void reviveMouseEntered() {
        glow.setLevel(0.5f);
        reviveButton.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    public void reviveMouseExited() {
        reviveButton.setEffect(null);
    }

    @FXML
    void restartButtonClicked() throws IOException {
        gaussianBlur.setRadius(0);
        Animations.translateTransition(gameOverAnchorPane, 0, 550, 500, 1, false).play();
        Animations.translateTransition(scoreLabel, 0, -125, 500, 1, false).play();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(gameAnchorPane.effectProperty(), gaussianBlur)),
                new KeyFrame(Duration.millis(250)   , new KeyValue(bgAnchorPane.effectProperty(), gaussianBlur)),
                new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleXProperty(), 1)),
                new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleYProperty(), 1)),
                new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.textFillProperty(), Color.rgb(97, 90, 90))));
        timeline.play();
        System.out.println("Game restarted!");  // Make game over scene and fade transition (bring down opacity of the game scene)

        // Reopen game stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGame.fxml"));
        GlobalVariables.root = loader.load();
        stage = (Stage) (restartButton.getScene().getWindow());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        gameController gameController = loader.getController();

        // Reset player properties
        resetFlags();  // Reset the flags of gameObjects
        game.getPlayer().setScore(0);
        game.getPlayer().setHero(new mainHero(50, 290));
        game.getPlayer().setRevived(false);
        gameController.setupScene(game);
        stage.setScene(GlobalVariables.scene);
        stage.show();
    }

    @FXML
    void restartMouseEntered() {
        glow.setLevel(0.5f);
        restartButton.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void restartMouseExited() {
        restartButton.setEffect(null);
    }

    @FXML
    void exitButtonClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Main Menu");
        alert.setHeaderText("You're about to go to the main menu!");
        alert.setContentText("Do you want to save your progress before exiting?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            // Insert code to save game state

            // Close current Pause Menu stage
            stage = (Stage)(exitButton.getScene().getWindow());
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
        exitButton.setEffect(glow);
        if (GlobalVariables.sound) {
            GlobalVariables.buttonHoverSound.stop();
            GlobalVariables.buttonHoverSound.play();
        }
    }

    @FXML
    void exitMouseExited() {
        exitButton.setEffect(null);
    }

    public void setupScene(Main game) {
        this.game = game;
        this.player = game.getPlayer();
        player.getHero().addToScreen(gameAnchorPane);
        GlobalVariables.scene.setOnMousePressed(mouseEvent -> {
            if (!mouseEvent.isControlDown()) {
                if(mouseEvent.getPickResult().getIntersectedNode().getId() == null || !mouseEvent.getPickResult().getIntersectedNode().getId().equals("settingsButton")) {
                    if (GlobalVariables.sound) {
                        GlobalVariables.playerLeapSound.stop();
                        GlobalVariables.playerLeapSound.play();
                    }
                    System.out.println("Mouse clicked");
                    player.getHero().setSpeedX(Game.mainHero.getJumpSlice());
                    player.getHero().setLeaped(true);
                    if (player.getHero().getCurrentWeapon() instanceof Shuriken) {
                        Shuriken shuriken = new Shuriken(player.getHero().getHero().getLayoutX(), player.getHero().getHero().getLayoutY());
                        shuriken.setSpeedX(Shuriken.getThrowSlice());
                        shuriken.setThrown(true);
                        System.out.println(shuriken);
                        shuriken.addToScreen(gameAnchorPane);
                        player.getHero().addShuriken(shuriken);
                    }
                    else if (player.getHero().getCurrentWeapon() instanceof Sword){
                        try {
                            Sword sword = (Sword) ((Sword) player.getHero().getCurrentWeapon()).clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Reset all flags here
                }
            }
        });
    }

    public void displayFPS() {
        now = System.nanoTime();
        long oldFrameTime = frameTimes[frameTimeIndex] ;
        frameTimes[frameTimeIndex] = now;
        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
        if (frameTimeIndex == 0) {
            arrayFilled = true ;
        }
        if (arrayFilled) {
            long elapsedNanos = now - oldFrameTime ;
            long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
            double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
            fpsLabel.setText(String.format("%.1f", frameRate));
        }
    }

    public void displayScore() {
        scoreLabel.setText(String.format("%d", player.getScore()));
    }

    public void displayCoins() {
        coinLabel.setText(String.format("%d", player.getCoins()));
    }

    private void displayPlayerWeapons() {
        if (player.getHero().getUnlockedWeapons().size() == 2) {
            for (int i = 0; i < player.getHero().getUnlockedWeapons().size(); i++) {
                if (player.getHero().getUnlockedWeapons().get(i) instanceof Shuriken) {
                    shurikenImage.setOpacity(1);
                    shurikenLevel.setText(String.format("%d", ((Shuriken) player.getHero().getUnlockedWeapons().get(i)).getLevel()));
                }
                else {
                    swordImage.setOpacity(1);
                    swordLevel.setText(String.format("%d", ((Sword) player.getHero().getUnlockedWeapons().get(i)).getLevel()));
                }
            }
        }
        else if (player.getHero().getUnlockedWeapons().size() == 1) {
            if (player.getHero().getUnlockedWeapons().get(0) instanceof Shuriken) {
                shurikenImage.setOpacity(1);
                shurikenLevel.setText(String.format("%d", ((Shuriken) player.getHero().getUnlockedWeapons().get(0)).getLevel()));
            }
            else {
                swordImage.setOpacity(1);
                swordLevel.setText(String.format("%d", ((Sword) player.getHero().getUnlockedWeapons().get(0)).getLevel()));
            }
        }
        else {
            shurikenImage.setOpacity(0.4);
            shurikenLevel.setText("0");
            swordImage.setOpacity(0.4);
            swordLevel.setText("0");
        }

    }

    // Reset all gameObjects flags to false after player dies - preparing for restart/revive
    public void resetFlags() {
        for (int i = 0; i < GlobalVariables.gameObjects.size(); i++) {
            GlobalVariables.gameObjects.get(i).setAdded(false);
        }
    }

    // Add objects to the screen as the player moves
    public void generateObjects() {
        for (int i = 0; i < GlobalVariables.gameObjects.size(); i++) {
            if (GlobalVariables.gameObjects.get(i).getP().getX() - player.getHero().getHero().getLayoutX() <= 1000 && !GlobalVariables.gameObjects.get(i).isAdded()) {
                System.out.println("inside index: " + i);
                gameObjects.add(GlobalVariables.gameObjects.get(i));
                GlobalVariables.gameObjects.get(i).setAdded(true);
                if (GlobalVariables.gameObjects.get(i) instanceof smallPlatform) {
                    ((smallPlatform) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof mediumPlatform) {
                    ((mediumPlatform) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof bigPlatform) {
                    ((bigPlatform) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof greenOrc) {
                    ((greenOrc) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof redOrc) {
                    ((redOrc) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof bossOrc) {
                    ((bossOrc) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof coinChest) {
                    ((coinChest) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof weaponChest) {
                    ((weaponChest) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
                if (GlobalVariables.gameObjects.get(i) instanceof TNT) {
                    ((TNT) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
            }
        }
    }

    // Remove objects from the screen and local arraylist for optimized rendering
    public void destroyObjects() {
        for (int i = 0; i < gameObjects.size(); i++) {
            if (gameObjects.get(i) instanceof smallPlatform) {
                if (((smallPlatform) gameObjects.get(i)).getsPlatform().getLayoutX() >= 1000) {
                    ((smallPlatform) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof mediumPlatform) {
                if (player.getHero().getHero().getLayoutX() - ((mediumPlatform) gameObjects.get(i)).getmPlatform().getLayoutX() >= 1000) {
                    ((mediumPlatform) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof bigPlatform) {
                if (player.getHero().getHero().getLayoutX() - ((bigPlatform) gameObjects.get(i)).getbPlatform().getLayoutX() >= 1000) {
                    ((bigPlatform) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof greenOrc) {
                if (player.getHero().getHero().getLayoutX() - ((greenOrc) gameObjects.get(i)).getGreenOrc().getLayoutX() >= 1000) {
                    ((greenOrc) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof redOrc) {
                if (player.getHero().getHero().getLayoutX() - ((redOrc) gameObjects.get(i)).getRedOrc().getLayoutX() >= 1000) {
                    ((redOrc) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof bossOrc) {
                if (player.getHero().getHero().getLayoutX() - ((bossOrc) gameObjects.get(i)).getBossOrc().getLayoutX() >= 1000) {
                    ((bossOrc) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof coinChest) {
                if (player.getHero().getHero().getLayoutX() - ((coinChest) gameObjects.get(i)).getCoinChestImageView().getLayoutX() >= 1000) {
                    ((coinChest) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof weaponChest) {
                if (player.getHero().getHero().getLayoutX() - ((weaponChest) gameObjects.get(i)).getWeaponChestImageView().getLayoutX() >= 1000) {
                    ((weaponChest) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
            else if (gameObjects.get(i) instanceof TNT) {
                if (player.getHero().getHero().getLayoutX() - ((TNT) gameObjects.get(i)).getTntImage().getLayoutX() >= 1000) {
                    ((TNT) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
        }
    }

    public void playerDeath(int deathType) {  // 0 for fall death, 1 for normal death
        GlobalVariables.timeline.setRate(0.1);  // Slow down time on death
        if (deathType == 0) {
            GlobalVariables.playerFallSound.stop();
            GlobalVariables.playerFallSound.play();
        }
        else if (deathType == 1) {
            GlobalVariables.playerDeathSound.stop();
            GlobalVariables.playerDeathSound.play();
        }
        gaussianBlur.setRadius(15);
        if (player.isRevived()) {
            reviveButton.setDisable(true);
            reviveButton.setOpacity(0.2);
        }
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {}));
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            Animations.translateTransition(gameOverAnchorPane, 0, -550, 500, 1, false).play();
            Animations.translateTransition(scoreLabel, 0, 125, 500, 1, false).play();
        }));
        Timeline timeline3 = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(gameAnchorPane.effectProperty(), gaussianBlur)),
                new KeyFrame(Duration.millis(250), new KeyValue(bgAnchorPane.effectProperty(), gaussianBlur)),
                new KeyFrame(Duration.millis(500), new KeyValue(scoreLabel.scaleXProperty(), 2)),
                new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleYProperty(), 2)),
                new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.textFillProperty(), Color.BLACK)));
        SequentialTransition sequentialTransition = new SequentialTransition(timeline1, timeline2, timeline3);
        sequentialTransition.play();
        System.out.println("Game over!");  // Make game over scene and fade transition (bring down opacity of the game scene)
        //GlobalVariables.timeline.pause();
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
        // Play player falling translate transition animation and then GlobalVariables.timeline.stop()
        GlobalVariables.timeline.pause();

        //GlobalVariables.timeline.stop();
        gameStarted = false;
    }

    public void run() {
        displayFPS();
        displayScore();
        displayCoins();
        displayPlayerWeapons();
        generateObjects();  // Checks each time whether the next item in queue is within a specified distance of the player (camera)
        destroyObjects();
        camera.update(player.getHero(), gameAnchorPane, bgAnchorPane);  // Follow the player
        if (gameStarted) {
            for (int i = 0; i < gameObjects.size(); i++) {
                for (int j = 0; j < gameObjects.size(); j++) {

                    if (gameObjects.get(i) instanceof smallPlatform) {

                    }
                    else if (gameObjects.get(i) instanceof mediumPlatform) {

                    }
                    else if (gameObjects.get(i) instanceof bigPlatform) {

                    }
                    else if (gameObjects.get(i) instanceof greenOrc) {
                        if (player.getHero().getHero().getLayoutX() - ((greenOrc) gameObjects.get(i)).getGreenOrc().getLayoutX() >= 1000) {
                            ((greenOrc) gameObjects.get(i)).removeFromScreen();
                            System.out.println("Removed: " + gameObjects.get(i));
                            gameObjects.remove(gameObjects.get(i));
                        }
                    }
                    else if (gameObjects.get(i) instanceof redOrc) {
                        if (player.getHero().getHero().getLayoutX() - ((redOrc) gameObjects.get(i)).getRedOrc().getLayoutX() >= 1000) {
                            ((redOrc) gameObjects.get(i)).removeFromScreen();
                            System.out.println("Removed: " + gameObjects.get(i));
                            gameObjects.remove(gameObjects.get(i));
                        }
                    }
                    else if (gameObjects.get(i) instanceof bossOrc) {
                        if (player.getHero().getHero().getLayoutX() - ((bossOrc) gameObjects.get(i)).getBossOrc().getLayoutX() >= 1000) {
                            ((bossOrc) gameObjects.get(i)).removeFromScreen();
                            System.out.println("Removed: " + gameObjects.get(i));
                            gameObjects.remove(gameObjects.get(i));
                        }
                    }
                    else if (gameObjects.get(i) instanceof coinChest) {
                        if (player.getHero().getHero().getLayoutX() - ((coinChest) gameObjects.get(i)).getCoinChestImageView().getLayoutX() >= 1000) {
                            ((coinChest) gameObjects.get(i)).removeFromScreen();
                            System.out.println("Removed: " + gameObjects.get(i));
                            gameObjects.remove(gameObjects.get(i));
                        }
                    }
                    else if (gameObjects.get(i) instanceof weaponChest) {
                        if (player.getHero().getHero().getLayoutX() - ((weaponChest) gameObjects.get(i)).getWeaponChestImageView().getLayoutX() >= 1000) {
                            ((weaponChest) gameObjects.get(i)).removeFromScreen();
                            System.out.println("Removed: " + gameObjects.get(i));
                            gameObjects.remove(gameObjects.get(i));
                        }
                    }
                    else if (gameObjects.get(i) instanceof TNT) {
                        if (player.getHero().getHero().getLayoutX() - ((TNT) gameObjects.get(i)).getTntImage().getLayoutX() >= 1000) {
                            ((TNT) gameObjects.get(i)).removeFromScreen();
                            System.out.println("Removed: " + gameObjects.get(i));
                            gameObjects.remove(gameObjects.get(i));
                        }
                    }
                }
            }






            // Red Orc movements
            if (redOrc.getCurrentJumpHeight() > redOrc.getJumpHeight()) {
                    redOrc.setSetY(redOrc.getSpeedY() - Game.redOrc.getAccelerationY());
                    redOrc.setSpeedY(redOrc.getSetY());
                    redOrc.jump();
                    redOrc.setCurrentJumpHeight(redOrc.getCurrentJumpHeight() + redOrc.getSetY());

            } else {
                //System.out.println("Main Else");
                redOrc.setSetY(redOrc.getSpeedY() + Game.redOrc.getAccelerationY());
                redOrc.setSpeedY(redOrc.getSetY());
                redOrc.jump();
                //jumpHeight += setY;
            }
            if (redOrc.isPushed()) {
                System.out.println("Orc speed: " + redOrc.getSpeedX());
                if (redOrc.getSpeedX() <= 0 || redOrc.getSpeedX() + Game.redOrc.getAccelerationX() <= 0) {
                    redOrc.setPushed(false);
                }
                else {
                    redOrc.setSpeedX(redOrc.getSpeedX() + Game.redOrc.getAccelerationX());
                    redOrc.push();
                }
            }
            if (redOrc.getBottomRectangle().getBoundsInParent().intersects(mediumPlatform.getmPlatformPolygon().getBoundsInParent()) || redOrc.getBottomRectangle().getBoundsInParent().intersects(smallPlatform.getsPlatformPolygon().getBoundsInParent()) || redOrc.getBottomRectangle().getBoundsInParent().intersects(bigPlatform.getbPlatformPolygon().getBoundsInParent())) {
                if (!redOrc.isKilled()) {
                    redOrc.setCurrentJumpHeight(0);
                    redOrc.setSpeedY(-Game.redOrc.getJumpSlice());
                }
            }
            // Green Orc movements
            if (greenOrc.getCurrentJumpHeight() > greenOrc.getJumpHeight()) {
                greenOrc.setSetY(greenOrc.getSpeedY() - Game.greenOrc.getAccelerationY());
                greenOrc.setSpeedY(greenOrc.getSetY());
                greenOrc.jump();
                greenOrc.setCurrentJumpHeight(greenOrc.getCurrentJumpHeight() + greenOrc.getSetY());

            } else {
                //System.out.println("Main Else");
                greenOrc.setSetY(greenOrc.getSpeedY() + Game.greenOrc.getAccelerationY());
                greenOrc.setSpeedY(greenOrc.getSetY());
                greenOrc.jump();
                //jumpHeight += setY;
            }
            if (greenOrc.isPushed()) {
                System.out.println("Green Orc speed: " + greenOrc.getSpeedX());
                if (greenOrc.getSpeedX() <= 0 || greenOrc.getSpeedX() + Game.greenOrc.getAccelerationX() <= 0) {
                    greenOrc.setPushed(false);
                }
                else {
                    greenOrc.setSpeedX(greenOrc.getSpeedX() + Game.greenOrc.getAccelerationX());
                    greenOrc.push();
                }
            }
            if (greenOrc.getBottomRectangle().getBoundsInParent().intersects(mediumPlatform.getmPlatformPolygon().getBoundsInParent()) || greenOrc.getBottomRectangle().getBoundsInParent().intersects(smallPlatform.getsPlatformPolygon().getBoundsInParent()) || greenOrc.getBottomRectangle().getBoundsInParent().intersects(bigPlatform.getbPlatformPolygon().getBoundsInParent())) {
                if (!greenOrc.isKilled()) {
                    greenOrc.setCurrentJumpHeight(0);
                    greenOrc.setSpeedY(-Game.greenOrc.getJumpSlice());
                }
            }
            // Boss Orc movements
            if (bossOrc.getCurrentJumpHeight() > bossOrc.getJumpHeight()) {
                bossOrc.setSetY(bossOrc.getSpeedY() - Game.bossOrc.getAccelerationY());
                bossOrc.setSpeedY(bossOrc.getSetY());
                bossOrc.jump();
                bossOrc.setCurrentJumpHeight(bossOrc.getCurrentJumpHeight() + bossOrc.getSetY());

            } else {
                //System.out.println("Main Else");
                bossOrc.setSetY(bossOrc.getSpeedY() + Game.bossOrc.getAccelerationY());
                bossOrc.setSpeedY(bossOrc.getSetY());
                bossOrc.jump();
                //jumpHeight += setY;
            }
            if (bossOrc.isPushed()) {
                System.out.println("Boss Orc speed: " + bossOrc.getSpeedX());
                System.out.println("Bruh value: " + (bossOrc.getSpeedX() + Game.bossOrc.getAccelerationX()));
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> bossOrc.push()));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.setOnFinished(event -> bossOrc.setSpeedX(0));
                if (bossOrc.getSpeedX() + Game.bossOrc.getAccelerationX() <= 0) {
                    if (!bossOrc.isAttacked()) {
                        //bossOrc.setPushed(false);
                        bossOrc.setAttacked(true);
                        System.out.println("entered");
                        bossOrc.setSpeedX(-10);
                    }
                    else {
                        bossOrc.setPushed(false);
                        System.out.println("First else boss speed here " + bossOrc.getSpeedX());
                        System.out.println("Non Bruh value: " + (bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX()));
                        if (bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                            bossOrc.setSpeedX(bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX());
                        }
                        else {
                            System.out.println("It's the plus");
                            bossOrc.setSpeedX(bossOrc.getSpeedX() + Game.bossOrc.getAccelerationX());
                        }
                        timeline.play();
                    }

                }
                else {
                    if (bossOrc.isAttacked()) {
                        System.out.println("else - if speed here " + bossOrc.getSpeedX());
                        bossOrc.setPushed(false);
                        System.out.println("Non Bruh value: " + (bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX()));
                        if (bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                            bossOrc.setSpeedX(bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX());
                        }
                        else {
                            System.out.println("It's the second plus");
                            bossOrc.setSpeedX(bossOrc.getSpeedX() + Game.bossOrc.getAccelerationX());
                        }
                        bossOrc.push();
                    }
                    else {
                        System.out.println("else - else speed here " + bossOrc.getSpeedX());
                        bossOrc.setPushed(false);
                        System.out.println("Non Bruh value: " + (bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX()));
                        if (bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                            bossOrc.setSpeedX(bossOrc.getSpeedX() - Game.bossOrc.getAccelerationX());
                        }
                        else {
                            System.out.println("It's the third plus");
                            bossOrc.setSpeedX(bossOrc.getSpeedX() + Game.bossOrc.getAccelerationX());
                        }
                        bossOrc.push();
                    }
                }
            }
            if (bossOrc.getBottomRectangle().getBoundsInParent().intersects(mediumPlatform.getmPlatformPolygon().getBoundsInParent()) || bossOrc.getBottomRectangle().getBoundsInParent().intersects(smallPlatform.getsPlatformPolygon().getBoundsInParent()) || bossOrc.getBottomRectangle().getBoundsInParent().intersects(bigPlatform.getbPlatformPolygon().getBoundsInParent())) {
                if (!bossOrc.isKilled()) {
                    bossOrc.setCurrentJumpHeight(0);
                    bossOrc.setSpeedY(-Game.bossOrc.getJumpSlice());
                }
            }
            // Player Movement
            if (!player.getHero().isLeaped()) {  // Stop Y axis motion when player leaps
                if (player.getHero().getCurrentJumpHeight() > player.getHero().getJumpHeight()) {
                    //System.out.println("Lol this");
                    player.getHero().setSetY(player.getHero().getSpeedY() - Game.mainHero.getAccelerationY());
                    player.getHero().setSpeedY(player.getHero().getSetY());
                    player.getHero().jump();
                    player.getHero().setCurrentJumpHeight(player.getHero().getCurrentJumpHeight() + player.getHero().getSetY());
                } else {
                    //System.out.println("Main Else");
                    player.getHero().setSetY(player.getHero().getSpeedY() + Game.mainHero.getAccelerationY());
                    player.getHero().setSpeedY(player.getHero().getSetY());
                    player.getHero().jump();
                    //jumpHeight += setY;
                }
            }
            if (player.getHero().isLeaped()) {
                player.getHero().setSpeedY(0);
                if (player.getHero().getCurrentLeapLength() < player.getHero().getLeapLength()) {
                    player.getHero().setSetX(player.getHero().getSpeedX() + Game.mainHero.getAccelerationX());
                    player.getHero().setSpeedX(player.getHero().getSetX());
                    player.getHero().leap();
                    player.getHero().setCurrentLeapLength(player.getHero().getCurrentLeapLength() + player.getHero().getSetX());
                } else {
                    // Called at the end of every leap
                    player.getHero().setSpeedY(player.getHero().getSetY());  // At the end of the leap, set Y speed back to before and X to 0.
                    player.getHero().setSpeedX(0);
                    player.getHero().setCurrentLeapLength(0);
                    player.getHero().setLeaped(false);
                    if (player.getHero().getHeroPolygon().getLayoutY() < 300)  // To prevent user from gaining score from leaping mid fall
                        player.increaseScore();
                }
            }
            if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(mediumPlatform.getmPlatformPolygon().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(smallPlatform.getsPlatformPolygon().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(bigPlatform.getbPlatformPolygon().getBoundsInParent())) {
                GlobalVariables.playerJumpSound.stop();
                GlobalVariables.playerJumpSound.play();
                player.getHero().setCurrentJumpHeight(0);
                player.getHero().setSpeedY(-Game.mainHero.getJumpSlice());
            }

            // Player collision with Red Orc
            if (player.getHero().collision_detected(redOrc)) {
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                    if (!redOrc.isPushed()) {
                        redOrc.setSpeedX((2 * Game.mainHero.getWeight() * player.getHero().getSpeedX()) / (Game.mainHero.getWeight() + Game.redOrc.getWeight()));
                        redOrc.setPushed(true);
                        player.getHero().setSpeedX(((Game.mainHero.getWeight() - Game.redOrc.getWeight()) * player.getHero().getSpeedX()) / (Game.mainHero.getWeight() + Game.redOrc.getWeight()));
                        player.getHero().leap();
                        player.getHero().setSpeedX(0);
                        player.getHero().setCurrentLeapLength(0);
                        player.getHero().setLeaped(false);
                    }
                }
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getTopRectangle().getBoundsInParent()) && !(player.getHero().getSpeedX() > 0) && !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getLeftRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getBottomRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getRightRectangle().getBoundsInParent()))) {
                    GlobalVariables.playerJumpSound.stop();
                    GlobalVariables.playerJumpSound.play();
                    player.getHero().setCurrentJumpHeight(0);
                    player.getHero().setSpeedY(-Game.mainHero.getJumpSlice());
                }

                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getBottomRectangle().getBoundsInParent()) &&  // Only when the player hits the bottom rectangle, he's considered dead
                    !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getLeftRectangle().getBoundsInParent()) ||
                    player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getTopRectangle().getBoundsInParent()) ||
                    player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getRightRectangle().getBoundsInParent()))) {
                        playerDeath(1);
                }
            }

            // Player collision with Green Orc
            if (player.getHero().collision_detected(greenOrc)) {
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                    if (!greenOrc.isPushed()) {
                        greenOrc.setSpeedX((2 * Game.mainHero.getWeight() * player.getHero().getSpeedX()) / (Game.mainHero.getWeight() + Game.greenOrc.getWeight()));
                        greenOrc.setPushed(true);
                        player.getHero().setSpeedX(((Game.mainHero.getWeight() - Game.greenOrc.getWeight()) * player.getHero().getSpeedX()) / (Game.mainHero.getWeight() + Game.greenOrc.getWeight()));
                        player.getHero().leap();
                        player.getHero().setSpeedX(0);
                        player.getHero().setCurrentLeapLength(0);
                        player.getHero().setLeaped(false);
                    }
                }
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getTopRectangle().getBoundsInParent()) && !(player.getHero().getSpeedX() > 0) && !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getLeftRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getBottomRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getRightRectangle().getBoundsInParent()))) {
                    GlobalVariables.playerJumpSound.stop();
                    GlobalVariables.playerJumpSound.play();
                    player.getHero().setCurrentJumpHeight(0);
                    player.getHero().setSpeedY(-Game.mainHero.getJumpSlice());
                }

                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getBottomRectangle().getBoundsInParent()) && !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getLeftRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getTopRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenOrc.getRightRectangle().getBoundsInParent()))) {
                    playerDeath(1);
                }
            }
            // Green orc collision with boss orc
            if (greenOrc.collision_detected(bossOrc)) { // Left collision for push
                    System.out.println("REACHED");
                    if (!bossOrc.isPushed()) {
                        System.out.println("INSIDE");
                        bossOrc.setSpeedX((2 * Game.greenOrc.getWeight() * greenOrc.getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight()));
                        bossOrc.setPushed(true);
                        greenOrc.setSpeedX(((Game.greenOrc.getWeight() - Game.bossOrc.getWeight()) * greenOrc.getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight()));
                        System.out.println("Green orc after rebound: " + greenOrc.getSpeedX());
                        greenOrc.push();
                        greenOrc.setSpeedX(0);
                    }
                if (greenOrc.getBottomRectangle().getBoundsInParent().intersects(bossOrc.getTopRectangle().getBoundsInParent()) && !(player.getHero().getSpeedX() > 0) && !(greenOrc.getBottomRectangle().getBoundsInParent().intersects(bossOrc.getLeftRectangle().getBoundsInParent()) || greenOrc.getBottomRectangle().getBoundsInParent().intersects(bossOrc.getBottomRectangle().getBoundsInParent()) || greenOrc.getBottomRectangle().getBoundsInParent().intersects(bossOrc.getRightRectangle().getBoundsInParent()))) {
                    GlobalVariables.playerJumpSound.stop();
                    GlobalVariables.playerJumpSound.play();
                    greenOrc.setCurrentJumpHeight(0);
                    greenOrc.setSpeedY(-Game.greenOrc.getJumpSlice());
                }
            }
            // Player collision with Boss Orc
            if (player.getHero().collision_detected(bossOrc)) {
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                    if (!bossOrc.isPushed()) {
                        System.out.println("Player speed before collision: " + player.getHero().getSpeedX());
                        bossOrc.setSpeedX(((2 * Game.mainHero.getWeight() * player.getHero().getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())) + (((Game.bossOrc.getWeight() - Game.mainHero.getWeight()) * bossOrc.getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())));
                        bossOrc.setPushed(true);
                        player.getHero().setSpeedX((((Game.mainHero.getWeight() - Game.bossOrc.getWeight()) * player.getHero().getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())) + ((2 * Game.bossOrc.getWeight() * bossOrc.getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())) - 0.01);
                        System.out.println("Player speed after collision: " + player.getHero().getSpeedX());
                        System.out.println("Boss speed after collision: " + bossOrc.getSpeedX());
                        // Boss orc comes forward and attacks if speed is negative
                        player.getHero().leap();
                        player.getHero().setSpeedX(0);
                        player.getHero().setCurrentLeapLength(0);
                        player.getHero().setLeaped(false);
                    }
                }
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getTopRectangle().getBoundsInParent()) && (player.getHero().getSpeedX() <= 0) && !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getLeftRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getRightRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getBottomRectangle().getBoundsInParent()))) {
                    GlobalVariables.playerJumpSound.stop();
                    GlobalVariables.playerJumpSound.play();
                    player.getHero().setCurrentJumpHeight(0);
                    player.getHero().setSpeedY(-Game.mainHero.getJumpSlice());
                }

                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getBottomRectangle().getBoundsInParent()) && !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getLeftRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getTopRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(bossOrc.getRightRectangle().getBoundsInParent()))) {
                    playerDeath(1);
                }
            }
            if (player.getHero().collision_detected(chest)) {
                if (!chest.isActivated()) {
                    chest.playChestAnimation(player);
                    chest.setActivated(true);
                }
            }
            if (player.getHero().collision_detected(chest2)) {
                if (!chest2.isActivated()) {
                    chest2.playChestAnimation(player);
                    chest2.setActivated(true);

                    System.out.println("Weapon type: " + chest2.getWeaponType());
                }
            }
            if (tnt.collision_detected(player.getHero())) {
                if (!tnt.isActivated()) {
                    tnt.setActivated(true);
                    tnt.playTNTAnimation();  // Resets explosion flags inside
                }
                else if (tnt.isExplosionActivated()) {
                    playerDeath(1);
                }
            }
            if (coin1.collision_detected(player.getHero())) {
                if (!coin1.isCollected()) {
                    player.increaseCoins(1);
                    coin1.playCoinAnimation();
                }
            }
            if (coin2.collision_detected(player.getHero())) {
                if (!coin2.isCollected()) {
                    player.increaseCoins(1);
                    coin2.playCoinAnimation();
                }
            }
            if (coin3.collision_detected(player.getHero())) {
                if (!coin3.isCollected()) {
                    player.increaseCoins(1);
                    coin3.playCoinAnimation();
                }
            }

            if (redOrc.collision_detected(tnt)) {
                if (!tnt.isActivated()) {
                    tnt.setActivated(true);
                    tnt.playTNTAnimation();
                }
                else if (tnt.isExplosionActivated()) {
                    if (!redOrc.isKilled()) {
                        redOrc.setKilled(true);
                        redOrc.playDeathAnimation(1, player);
                    }
                }
            }

            if (player.getHero().getHeroPolygon().getLayoutY() > 780) {  // Death fall detection
                playerDeath(0);
            }
            if (redOrc.getTopRectangle().getLayoutY() > 780) {  // Death fall detection
                redOrc.setSpeedY(0);
                if (!redOrc.isKilled()) {
                    redOrc.setKilled(true);
                    redOrc.playDeathAnimation(0, player);
                }
            }

            for (int i = 0; i < player.getHero().getShurikens().size(); i++) {
                if ((player.getHero().getShurikens().get(i)).isThrown()) {
                    (player.getHero().getShurikens().get(i)).setSpeedX((player.getHero().getShurikens().get(i)).getSpeedX() + Game.Shuriken.getAccelerationX());
                    (player.getHero().getShurikens().get(i)).throwShuriken();
                    if ((player.getHero().getShurikens().get(i)).collision_detected(bossOrc)) {
                        Glow glow1 = new Glow();
                        Glow glow2 = new Glow();
                        glow1.setLevel(0.5);
                        glow2.setLevel(0);
                        Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(bossOrc.getBossOrc().effectProperty(), glow1)));
                        Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(bossOrc.getBossOrc().effectProperty(), glow2)));
                        timeline1.setOnFinished(event -> timeline2.play());
                        timeline1.play();
                        bossOrc.setHealth(bossOrc.getHealth() - (player.getHero().getShurikens().get(i)).getDamage());
                        (player.getHero().getShurikens().get(i)).setThrown(false);
                        (player.getHero().getShurikens().get(i)).setSpeedX(0);
                        (player.getHero().getShurikens().get(i)).getShuriken().setVisible(false);
                        (player.getHero().getShurikens().get(i)).getShuriken().setDisable(true);
                        (player.getHero().getShurikens().get(i)).getShurikenPolygon().setVisible(false);
                        (player.getHero().getShurikens().get(i)).getShurikenPolygon().setDisable(true);
                        if (bossOrc.getHealth() <= 0 && !bossOrc.isKilled()) {
                            bossOrc.setKilled(true);
                            bossOrc.playDeathAnimation(1, player);
                        }

                        player.getHero().getShurikens().remove(i);
                    }
                    else if ((player.getHero().getShurikens().get(i)).getTotalDistance() >= 500) {
                        System.out.println("Shuriken Attack 2");
                        (player.getHero().getShurikens().get(i)).setThrown(false);
                        (player.getHero().getShurikens().get(i)).setSpeedX(0);
                        (player.getHero().getShurikens().get(i)).getShuriken().setVisible(false);
                        (player.getHero().getShurikens().get(i)).getShuriken().setDisable(true);
                        (player.getHero().getShurikens().get(i)).getShurikenPolygon().setVisible(false);
                        (player.getHero().getShurikens().get(i)).getShurikenPolygon().setDisable(true);
                        player.getHero().getShurikens().remove(i);
                    }
                }
            }
//            for (int i = 0; i < gameObjects.size(); i++) {
//                if (gameObjects.get(i) instanceof mainHero) {
//                    if (player.getHero().getHeroPolygon().getLayoutY() > 780) {  // Death fall detection
//                        GlobalVariables.playerFallSound.stop();
//                        GlobalVariables.playerFallSound.play();
//                        System.out.println("Game over!");  // Make game over scene and fade transition (bring down opacity of the game scene)
//                        //GlobalVariables.timeline.pause();
//    //                try {
//    //                    TimeUnit.SECONDS.sleep(2);
//    //                } catch (InterruptedException e) {
//    //                    e.printStackTrace();
//    //                }
//                        // Play player falling translate transition animation and then GlobalVariables.timeline.stop()
//                        GlobalVariables.timeline.setRate(0.1);  // Slow down time on death
//                        //GlobalVariables.timeline.stop();
//                        gameStarted = false;
//                    }
//                }
//                if (gameObjects.get(i) instanceof redOrc){
//                    if (((redOrc) gameObjects.get(i)).getRedOrcPolygon().getLayoutY() > 780){
//                        // Adding coins to player and remove from arraylist
//                        player.increaseCoins(3);
//                        //gameObjects.remove(i);
//                    }
//                }
//                if (gameObjects.get(i) instanceof greenOrc){
//                    if (((greenOrc) gameObjects.get(i)).getGreenOrcPolygon().getLayoutY() > 780){
//                        // Adding coins to player and remove from arraylist
//                        player.increaseCoins(1);
//                        //gameObjects.remove(i);
//                    }
//                }
//                if (gameObjects.get(i) instanceof coinChest) {
//                    if (((coinChest) gameObjects.get(i)).getCoinChestPolygon().getLayoutY() > 780){
//                        // remove from arraylist
//                        //gameObjects.remove(i);
//                    }
//                }
//                if (gameObjects.get(i) instanceof weaponChest) {
//                    if (((weaponChest) gameObjects.get(i)).getWeaponChestPolygon().getLayoutY() > 780){
//                        // remove from arraylist
//                        //gameObjects.remove(i);
//                    }
//                }
//            }

        }
    }
}
