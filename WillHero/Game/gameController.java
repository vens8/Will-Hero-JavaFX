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
        bossOrc = new bossOrc(5000, 173);
        bossOrc.addToScreen(gameAnchorPane);
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
        //System.out.println("Game restarted!");  // Make game over scene and fade transition (bring down opacity of the game scene)

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
        GlobalVariables.gameObjects.add(player.getHero());
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
            for (int i = 0; i < ((mainHero) gameObjects.get(i)).getUnlockedWeapons().size(); i++) {
                if (((mainHero) gameObjects.get(i)).getUnlockedWeapons().get(i) instanceof Shuriken) {
                    shurikenImage.setOpacity(1);
                    shurikenLevel.setText(String.format("%d", ((Shuriken) ((mainHero) gameObjects.get(i)).getUnlockedWeapons().get(i)).getLevel()));
                }
                else {
                    swordImage.setOpacity(1);
                    swordLevel.setText(String.format("%d", ((Sword) ((mainHero) gameObjects.get(i)).getUnlockedWeapons().get(i)).getLevel()));
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
        System.out.println("Global size: " + GlobalVariables.gameObjects.size());
        for (int i = 0; i < GlobalVariables.gameObjects.size(); i++) {
            if (GlobalVariables.gameObjects.get(i).getP().getX() - player.getHero().getHero().getLayoutX() <= 1000 && !GlobalVariables.gameObjects.get(i).isAdded()) {
                System.out.println("added this item bro: " + GlobalVariables.gameObjects.get(i));
                gameObjects.add(GlobalVariables.gameObjects.get(i));
                GlobalVariables.gameObjects.get(i).setAdded(true);
                if (GlobalVariables.gameObjects.get(i) instanceof mainHero) {
                    ((mainHero) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
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
                if (GlobalVariables.gameObjects.get(i) instanceof Coin) {
                    ((Coin) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
            }
        }
    }

    // Remove objects from the screen and local arraylist for optimized rendering
    public void destroyObjects() {
        for (int i = 0; i < gameObjects.size(); i++) {
            if (gameObjects.get(i) instanceof smallPlatform) {
                if (player.getHero().getHero().getLayoutX() - ((smallPlatform) gameObjects.get(i)).getsPlatform().getLayoutX() >= 1000) {
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
        if (player.getHero().getSpeedY() > Math.abs(player.getHero().getMaxSpeed())) {
            player.getHero().setMaxSpeed(player.getHero().getSpeedY());
        }
        System.out.println("Current speed: " + player.getHero().getSpeedY());
        System.out.println("Max: " + player.getHero().getMaxSpeed());
        camera.update(player.getHero(), gameAnchorPane, bgAnchorPane);  // Follow the player
        if (gameStarted) {
            //System.out.println("Size of local gameObjects: " + gameObjects.size());
            for (int i = 0; i < gameObjects.size(); i++) {
                //System.out.println("Outside object : " + gameObjects.get(i) + "Added: " + gameObjects.get(i).isAdded());
                for (GameObject gameObject : gameObjects) {
                    //System.out.println("Inside object : " + gameObject + "Added: " + gameObject.isAdded());
                    if (gameObjects.get(i) instanceof mainHero) {
                        // Player Movement
                        if (!((mainHero) gameObjects.get(i)).isLeaped()) {  // Stop Y axis motion when player leaps
                            if (((mainHero) gameObjects.get(i)).getCurrentJumpHeight() > ((mainHero) gameObjects.get(i)).getJumpHeight()) {
                                //System.out.println("Lol this");
                                ((mainHero) gameObjects.get(i)).setSetY(((mainHero) gameObjects.get(i)).getSpeedY() - Game.mainHero.getAccelerationY());
                                ((mainHero) gameObjects.get(i)).setSpeedY(((mainHero) gameObjects.get(i)).getSetY());
                                ((mainHero) gameObjects.get(i)).jump();
                                ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(((mainHero) gameObjects.get(i)).getCurrentJumpHeight() + ((mainHero) gameObjects.get(i)).getSetY());
                            } else {
                                //System.out.println("Main Else");
                                ((mainHero) gameObjects.get(i)).setSetY(((mainHero) gameObjects.get(i)).getSpeedY() + Game.mainHero.getAccelerationY());
                                ((mainHero) gameObjects.get(i)).setSpeedY(((mainHero) gameObjects.get(i)).getSetY());
                                ((mainHero) gameObjects.get(i)).jump();
                                //jumpHeight += setY;
                            }
                        }
                        if (((mainHero) gameObjects.get(i)).isLeaped()) {
                            ((mainHero) gameObjects.get(i)).setSpeedY(0);
                            if (((mainHero) gameObjects.get(i)).getCurrentLeapLength() < ((mainHero) gameObjects.get(i)).getLeapLength()) {
                                ((mainHero) gameObjects.get(i)).setSetX(((mainHero) gameObjects.get(i)).getSpeedX() + Game.mainHero.getAccelerationX());
                                ((mainHero) gameObjects.get(i)).setSpeedX(((mainHero) gameObjects.get(i)).getSetX());
                                ((mainHero) gameObjects.get(i)).leap();
                                ((mainHero) gameObjects.get(i)).setCurrentLeapLength(((mainHero) gameObjects.get(i)).getCurrentLeapLength() + ((mainHero) gameObjects.get(i)).getSetX());
                            } else {
                                // Called at the end of every leap
                                ((mainHero) gameObjects.get(i)).setSpeedY(((mainHero) gameObjects.get(i)).getSetY());  // At the end of the leap, set Y speed back to before and X to 0.
                                ((mainHero) gameObjects.get(i)).setSpeedX(0);
                                ((mainHero) gameObjects.get(i)).setCurrentLeapLength(0);
                                ((mainHero) gameObjects.get(i)).setLeaped(false);
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getLayoutY() < 300)  // To prevent user from gaining score from leaping mid fall
                                    player.increaseScore();
                            }
                        }
                        if (gameObject instanceof smallPlatform) {
                            if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent())) {
                                System.out.println("This is collision with small platform sir");
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                            }
                        } else if (gameObject instanceof mediumPlatform) {
                            if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent())) {
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                            }
                        } else if (gameObject instanceof bigPlatform) {
                            System.out.println("green and big");
                            System.out.println("Green: " + gameObjects.get(i));
                            System.out.println("Big: " + gameObject);
                            if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent())) {
                                ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                            }
                        }
                        else if (gameObject instanceof redOrc) {
                            // Player collision with Red Orc
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((redOrc) gameObject).isPushed()) {
                                        ((redOrc) gameObject).setSpeedX((2 * Game.mainHero.getWeight() * ((mainHero) gameObjects.get(i)).getSpeedX()) / (Game.mainHero.getWeight() + Game.redOrc.getWeight()));
                                        ((redOrc) gameObject).setPushed(true);
                                        ((mainHero) gameObjects.get(i)).setSpeedX(((Game.mainHero.getWeight() - Game.redOrc.getWeight()) * ((mainHero) gameObjects.get(i)).getSpeedX()) / (Game.mainHero.getWeight() + Game.redOrc.getWeight()));
                                        ((mainHero) gameObjects.get(i)).leap();
                                        ((mainHero) gameObjects.get(i)).setSpeedX(0);
                                        ((mainHero) gameObjects.get(i)).setCurrentLeapLength(0);
                                        ((mainHero) gameObjects.get(i)).setLeaped(false);
                                    }
                                }
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getSpeedX() > 0) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    GlobalVariables.playerJumpSound.stop();
                                    GlobalVariables.playerJumpSound.play();
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                }

                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getBottomRectangle().getBoundsInParent()) &&  // Only when the player hits the bottom rectangle, he's considered dead
                                        !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent()) ||
                                                ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getTopRectangle().getBoundsInParent()) ||
                                                ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    playerDeath(1);
                                }
                            }
                        }
                        else if (gameObject instanceof greenOrc) {
                            // Player collision with Green Orc
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((greenOrc) gameObject).isPushed()) {
                                        ((greenOrc) gameObject).setSpeedX((2 * Game.mainHero.getWeight() * ((mainHero) gameObjects.get(i)).getSpeedX()) / (Game.mainHero.getWeight() + Game.greenOrc.getWeight()));
                                        ((greenOrc) gameObject).setPushed(true);
                                        ((mainHero) gameObjects.get(i)).setSpeedX(((Game.mainHero.getWeight() - Game.greenOrc.getWeight()) * ((mainHero) gameObjects.get(i)).getSpeedX()) / (Game.mainHero.getWeight() + Game.greenOrc.getWeight()));
                                        ((mainHero) gameObjects.get(i)).leap();
                                        ((mainHero) gameObjects.get(i)).setSpeedX(0);
                                        ((mainHero) gameObjects.get(i)).setCurrentLeapLength(0);
                                        ((mainHero) gameObjects.get(i)).setLeaped(false);
                                    }
                                }
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getSpeedX() > 0) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    GlobalVariables.playerJumpSound.stop();
                                    GlobalVariables.playerJumpSound.play();
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                }

                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getBottomRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getTopRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    playerDeath(1);
                                }
                            }
                        }

                        else if (gameObject instanceof bossOrc) {
                            // Player collision with Boss Orc
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((bossOrc) gameObject).isPushed()) {
                                        System.out.println("Player speed before collision: " + ((mainHero) gameObjects.get(i)).getSpeedX());
                                        ((bossOrc) gameObject).setSpeedX(((2 * Game.mainHero.getWeight() * ((mainHero) gameObjects.get(i)).getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())) + (((Game.bossOrc.getWeight() - Game.mainHero.getWeight()) * ((bossOrc) gameObject).getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())));
                                        ((bossOrc) gameObject).setPushed(true);
                                        ((mainHero) gameObjects.get(i)).setSpeedX((((Game.mainHero.getWeight() - Game.bossOrc.getWeight()) * ((mainHero) gameObjects.get(i)).getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())) + ((2 * Game.bossOrc.getWeight() * ((bossOrc) gameObject).getSpeedX()) / (Game.mainHero.getWeight() + Game.bossOrc.getWeight())) - 0.01);
                                        System.out.println("Player speed after collision: " + ((mainHero) gameObjects.get(i)).getSpeedX());
                                        System.out.println("Boss speed after collision: " + ((bossOrc) gameObject).getSpeedX());
                                        // Boss orc comes forward and attacks if speed is negative
                                        ((mainHero) gameObjects.get(i)).leap();
                                        ((mainHero) gameObjects.get(i)).setSpeedX(0);
                                        ((mainHero) gameObjects.get(i)).setCurrentLeapLength(0);
                                        ((mainHero) gameObjects.get(i)).setLeaped(false);
                                    }
                                }
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getTopRectangle().getBoundsInParent()) && (((mainHero) gameObjects.get(i)).getSpeedX() <= 0) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getRightRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getBottomRectangle().getBoundsInParent()))) {
                                    GlobalVariables.playerJumpSound.stop();
                                    GlobalVariables.playerJumpSound.play();
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                }

                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getBottomRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getTopRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    playerDeath(1);
                                }
                            }
                        }
                        else if (gameObject instanceof weaponChest) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((weaponChest) gameObject).isActivated()) {
                                    ((weaponChest) gameObject).playChestAnimation(player);
                                    ((weaponChest) gameObject).setActivated(true);
                                }
                            }
                        }
                        else if (gameObject instanceof TNT) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((TNT) gameObject).isActivated()) {
                                    ((TNT) gameObject).setActivated(true);
                                    ((TNT) gameObject).playTNTAnimation();  // Resets explosion flags inside
                                } else if (((TNT) gameObject).isExplosionActivated()) {
                                    playerDeath(1);
                                }
                            }
                        }
                        else if (gameObject instanceof Coin) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((Coin) gameObject).isCollected()) {
                                    player.increaseCoins(1);
                                    ((Coin) gameObject).playCoinAnimation();
                                }
                            }
                        }

                        if (((mainHero) gameObjects.get(i)).getHeroPolygon().getLayoutY() > 780) {  // player death fall detection
                            System.out.println("Max: " + player.getHero().getMaxSpeed());
                            System.out.println("Min: " + player.getHero().getMinSpeed());
                            playerDeath(0);
                        }
                    }
                    else if (gameObjects.get(i) instanceof smallPlatform) {

                    } else if (gameObjects.get(i) instanceof mediumPlatform) {

                    } else if (gameObjects.get(i) instanceof bigPlatform) {

                    } else if (gameObjects.get(i) instanceof greenOrc) {
                        // Green Orc movements
                        if (((greenOrc) gameObjects.get(i)).getCurrentJumpHeight() > ((greenOrc) gameObjects.get(i)).getJumpHeight()) {
                            ((greenOrc) gameObjects.get(i)).setSetY(((greenOrc) gameObjects.get(i)).getSpeedY() - Game.greenOrc.getAccelerationY());
                            ((greenOrc) gameObjects.get(i)).setSpeedY(((greenOrc) gameObjects.get(i)).getSetY());
                            ((greenOrc) gameObjects.get(i)).jump();
                            ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(((greenOrc) gameObjects.get(i)).getCurrentJumpHeight() + ((greenOrc) gameObjects.get(i)).getSetY());

                        } else {
                            //System.out.println("Main Else");
                            ((greenOrc) gameObjects.get(i)).setSetY(((greenOrc) gameObjects.get(i)).getSpeedY() + Game.greenOrc.getAccelerationY());
                            ((greenOrc) gameObjects.get(i)).setSpeedY(((greenOrc) gameObjects.get(i)).getSetY());
                            ((greenOrc) gameObjects.get(i)).jump();
                            //jumpHeight += setY;
                        }
                        if (((greenOrc) gameObjects.get(i)).isPushed()) {
                            System.out.println("Green Orc speed: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                            if (((greenOrc) gameObjects.get(i)).getSpeedX() <= 0 || ((greenOrc) gameObjects.get(i)).getSpeedX() + Game.greenOrc.getAccelerationX() <= 0) {
                                ((greenOrc) gameObjects.get(i)).setPushed(false);
                            } else {
                                ((greenOrc) gameObjects.get(i)).setSpeedX(((greenOrc) gameObjects.get(i)).getSpeedX() + Game.greenOrc.getAccelerationX());
                                ((greenOrc) gameObjects.get(i)).push();
                            }
                        }
                        if (gameObject instanceof smallPlatform) {
                            if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent())) {
                                System.out.println("This is collision with small platform sir");
                                if (!((greenOrc) gameObjects.get(i)).isKilled()) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof mediumPlatform) {
                            if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent())) {
                                if (!((greenOrc) gameObjects.get(i)).isKilled()) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof bigPlatform) {
                            System.out.println("green and big");
                            System.out.println("Green: " + gameObjects.get(i));
                            System.out.println("Big: " + gameObject);
                            if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent())) {
                                System.out.println("Green orc killed: " + ((greenOrc) gameObjects.get(i)).isKilled());
                                if (!((greenOrc) gameObjects.get(i)).isKilled()) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        }
                        if (gameObject instanceof bossOrc) {
                            // Green orc collision with boss orc
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (!((bossOrc) gameObject).isPushed()) {
                                    System.out.println("INSIDE");
                                    ((bossOrc) gameObject).setSpeedX((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight()));
                                    ((bossOrc) gameObject).setPushed(true);
                                    ((greenOrc) gameObjects.get(i)).setSpeedX(((Game.greenOrc.getWeight() - Game.bossOrc.getWeight()) * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight()));
                                    System.out.println("Green orc after rebound: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                    ((greenOrc) gameObjects.get(i)).push();
                                    ((greenOrc) gameObjects.get(i)).setSpeedX(0);
                                }
                                if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(player.getHero().getSpeedX() > 0) && !(((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    GlobalVariables.playerJumpSound.stop();
                                    GlobalVariables.playerJumpSound.play();
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        }
                    } else if (gameObjects.get(i) instanceof redOrc) {
                        // Red Orc movements
                        if (((redOrc) gameObjects.get(i)).getCurrentJumpHeight() > ((redOrc) gameObjects.get(i)).getJumpHeight()) {
                            ((redOrc) gameObjects.get(i)).setSetY(((redOrc) gameObjects.get(i)).getSpeedY() - Game.redOrc.getAccelerationY());
                            ((redOrc) gameObjects.get(i)).setSpeedY(((redOrc) gameObjects.get(i)).getSetY());
                            ((redOrc) gameObjects.get(i)).jump();
                            ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(((redOrc) gameObjects.get(i)).getCurrentJumpHeight() + ((redOrc) gameObjects.get(i)).getSetY());

                        } else {
                            ((redOrc) gameObjects.get(i)).setSetY(((redOrc) gameObjects.get(i)).getSpeedY() + Game.redOrc.getAccelerationY());
                            ((redOrc) gameObjects.get(i)).setSpeedY(((redOrc) gameObjects.get(i)).getSetY());
                            ((redOrc) gameObjects.get(i)).jump();
                            //jumpHeight += setY;
                        }
                        if (((redOrc) gameObjects.get(i)).isPushed()) {
                            System.out.println("Orc speed: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                            if (((redOrc) gameObjects.get(i)).getSpeedX() <= 0 || ((redOrc) gameObjects.get(i)).getSpeedX() + Game.redOrc.getAccelerationX() <= 0) {
                                ((redOrc) gameObjects.get(i)).setPushed(false);
                            } else {
                                ((redOrc) gameObjects.get(i)).setSpeedX(((redOrc) gameObjects.get(i)).getSpeedX() + Game.redOrc.getAccelerationX());
                                ((redOrc) gameObjects.get(i)).push();
                            }
                        }
                        if (gameObject instanceof smallPlatform) {
                            if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent())) {
                                if (!((redOrc) gameObjects.get(i)).isKilled()) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof mediumPlatform) {
                            if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent())) {
                                if (!((redOrc) gameObjects.get(i)).isKilled()) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof bigPlatform) {
                            if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent())) {
                                if (!((redOrc) gameObjects.get(i)).isKilled()) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        }
                        else if (gameObject instanceof TNT) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((TNT) gameObject).isActivated()) {
                                    ((TNT) gameObject).setActivated(true);
                                    ((TNT) gameObject).playTNTAnimation();
                                } else if (((TNT) gameObject).isExplosionActivated()) {
                                    if (!((redOrc) gameObjects.get(i)).isKilled()) {
                                        ((redOrc) gameObjects.get(i)).setKilled(true);
                                        ((redOrc) gameObjects.get(i)).playDeathAnimation(1, player);
                                    }
                                }
                            }
                        }
                        if (((redOrc) gameObjects.get(i)).getTopRectangle().getLayoutY() > 780) {  // Death fall detection
                            ((redOrc) gameObjects.get(i)).setSpeedY(0);
                            if (!((redOrc) gameObjects.get(i)).isKilled()) {
                                ((redOrc) gameObjects.get(i)).setKilled(true);
                                ((redOrc) gameObjects.get(i)).playDeathAnimation(0, player);
                            }
                        }
                    } else if (gameObjects.get(i) instanceof bossOrc) {
                        // Boss Orc movements
                        if (((bossOrc) gameObjects.get(i)).getCurrentJumpHeight() > bossOrc.getJumpHeight()) {
                            ((bossOrc) gameObjects.get(i)).setSetY(((bossOrc) gameObjects.get(i)).getSpeedY() - Game.bossOrc.getAccelerationY());
                            ((bossOrc) gameObjects.get(i)).setSpeedY(((bossOrc) gameObjects.get(i)).getSetY());
                            ((bossOrc) gameObjects.get(i)).jump();
                            ((bossOrc) gameObjects.get(i)).setCurrentJumpHeight(((bossOrc) gameObjects.get(i)).getCurrentJumpHeight() + ((bossOrc) gameObjects.get(i)).getSetY());

                        } else {
                            //System.out.println("Main Else");
                            ((bossOrc) gameObjects.get(i)).setSetY(((bossOrc) gameObjects.get(i)).getSpeedY() + Game.bossOrc.getAccelerationY());
                            ((bossOrc) gameObjects.get(i)).setSpeedY(((bossOrc) gameObjects.get(i)).getSetY());
                            ((bossOrc) gameObjects.get(i)).jump();
                            //jumpHeight += setY;
                        }
                        if (((bossOrc) gameObjects.get(i)).isPushed()) {
                            System.out.println("Boss Orc speed: " + ((bossOrc) gameObjects.get(i)).getSpeedX());
                            System.out.println("Bruh value: " + (((bossOrc) gameObjects.get(i)).getSpeedX() + Game.bossOrc.getAccelerationX()));
                            int finalI1 = i;
                            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> ((bossOrc) gameObjects.get(finalI1)).push()));
                            timeline.setCycleCount(Timeline.INDEFINITE);
                            int finalI = i;
                            timeline.setOnFinished(event -> ((bossOrc) gameObjects.get(finalI)).setSpeedX(0));
                            if (((bossOrc) gameObjects.get(i)).getSpeedX() + Game.bossOrc.getAccelerationX() <= 0) {
                                if (!((bossOrc) gameObjects.get(i)).isAttacked()) {
                                    ((bossOrc) gameObjects.get(i)).setAttacked(true);
                                    System.out.println("entered");
                                    ((bossOrc) gameObjects.get(i)).setSpeedX(-10);
                                } else {
                                    ((bossOrc) gameObjects.get(i)).setPushed(false);
                                    System.out.println("First else boss speed here " + ((bossOrc) gameObjects.get(i)).getSpeedX());
                                    System.out.println("Non Bruh value: " + (((bossOrc) gameObjects.get(i)).getSpeedX() - Game.bossOrc.getAccelerationX()));
                                    if (((bossOrc) gameObjects.get(i)).getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                                        ((bossOrc) gameObjects.get(i)).setSpeedX(((bossOrc) gameObjects.get(i)).getSpeedX() - Game.bossOrc.getAccelerationX());
                                    } else {
                                        System.out.println("It's the plus");
                                        ((bossOrc) gameObjects.get(i)).setSpeedX(((bossOrc) gameObjects.get(i)).getSpeedX() + Game.bossOrc.getAccelerationX());
                                    }
                                    timeline.play();
                                }

                            } else {
                                if (((bossOrc) gameObject).isAttacked()) {
                                    System.out.println("else - if speed here " + ((bossOrc) gameObject).getSpeedX());
                                    ((bossOrc) gameObject).setPushed(false);
                                    System.out.println("Non Bruh value: " + (((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX()));
                                    if (((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                                        ((bossOrc) gameObject).setSpeedX(((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX());
                                    } else {
                                        System.out.println("It's the second plus");
                                        ((bossOrc) gameObject).setSpeedX(((bossOrc) gameObject).getSpeedX() + Game.bossOrc.getAccelerationX());
                                    }
                                    ((bossOrc) gameObject).push();
                                } else {
                                    System.out.println("else - else speed here " + ((bossOrc) gameObject).getSpeedX());
                                    ((bossOrc) gameObject).setPushed(false);
                                    System.out.println("Non Bruh value: " + (((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX()));
                                    if (((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                                        ((bossOrc) gameObject).setSpeedX(((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX());
                                    } else {
                                        System.out.println("It's the third plus");
                                        ((bossOrc) gameObject).setSpeedX(((bossOrc) gameObject).getSpeedX() + Game.bossOrc.getAccelerationX());
                                    }
                                    ((bossOrc) gameObject).push();
                                }
                            }
                        }
                        if (gameObject instanceof smallPlatform) {
                            if (((bossOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent())) {
                                if (!((bossOrc) gameObjects.get(i)).isKilled()) {
                                    ((bossOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((bossOrc) gameObjects.get(i)).setSpeedY(-Game.bossOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof mediumPlatform) {
                            if (((bossOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent())) {
                                if (!((bossOrc) gameObjects.get(i)).isKilled()) {
                                    ((bossOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((bossOrc) gameObjects.get(i)).setSpeedY(-Game.bossOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof bigPlatform) {
                            if (((bossOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent())) {
                                if (!((bossOrc) gameObjects.get(i)).isKilled()) {
                                    ((bossOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((bossOrc) gameObjects.get(i)).setSpeedY(-Game.bossOrc.getJumpSlice());
                                }
                            }
                        }

                    } else if (gameObjects.get(i) instanceof coinChest) {

                    } else if (gameObjects.get(i) instanceof weaponChest) {

                    } else if (gameObjects.get(i) instanceof TNT) {

                    }
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
                        player.getHero().getShurikens().get(i).setThrown(false);
                        player.getHero().getShurikens().get(i).setSpeedX(0);
                        player.getHero().getShurikens().get(i).getShuriken().setVisible(false);
                        player.getHero().getShurikens().get(i).getShuriken().setDisable(true);
                        player.getHero().getShurikens().get(i).getShurikenPolygon().setVisible(false);
                        player.getHero().getShurikens().get(i).getShurikenPolygon().setDisable(true);
                        if (bossOrc.getHealth() <= 0 && !bossOrc.isKilled()) {
                            bossOrc.setKilled(true);
                            bossOrc.playDeathAnimation(1, player);
                        }

                        ((mainHero) gameObjects.get(i)).getShurikens().remove(i);
                    }
                    else if ((player.getHero().getShurikens().get(i)).getTotalDistance() >= 500) {
                        System.out.println("Shuriken Attack 2");
                        player.getHero().getShurikens().get(i).setThrown(false);
                        player.getHero().getShurikens().get(i).setSpeedX(0);
                        player.getHero().getShurikens().get(i).getShuriken().setVisible(false);
                        player.getHero().getShurikens().get(i).getShuriken().setDisable(true);
                        player.getHero().getShurikens().get(i).getShurikenPolygon().setVisible(false);
                        player.getHero().getShurikens().get(i).getShurikenPolygon().setDisable(true);
                        player.getHero().getShurikens().remove(i);
                    }
                }
            }
//            for (int i = 0; i < gameObjects.size(); i++) {
//                if (gameObjects.get(i) instanceof mainHero) {
//                    if (((mainHero) gameObjects.get(i)).getHeroPolygon().getLayoutY() > 780) {  // Death fall detection
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
