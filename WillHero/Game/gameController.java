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
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
import java.util.LinkedList;
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
    @FXML
    private MediaView videoMediaView;


    private Camera camera;
    private Main game;
    private Player player;
    private boolean gameStarted;
    private ArrayList<GameObject> gameObjects2;
    private LinkedList<GameObject> gameObjects;
    private Color scoreColor;

    // FPS computation
    private final long[] frameTimes = new long[100];
    private int frameIndex = 0;
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
        //gameObjects = new ArrayList<>();
        gameObjects = new LinkedList<>();   

        gameStarted = true;  // local to the game
        scoreColor = Color.BLACK;
        if (GlobalVariables.difficulty == 50) {  // Hard mode
            videoMediaView.setMediaPlayer(GlobalVariables.backgroundVideo);
            scoreColor = Color.WHITE;
            gameAnchorPane.setOpacity(0.5);
            GlobalVariables.sound = false;
            GlobalVariables.backgroundVideo.setCycleCount(MediaPlayer.INDEFINITE);
            GlobalVariables.backgroundVideo.stop();
            GlobalVariables.backgroundVideo.play();
            GlobalVariables.eerieMusic.setCycleCount(MediaPlayer.INDEFINITE);
            GlobalVariables.eerieMusic.stop();
            GlobalVariables.eerieMusic.play();
        }
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
        if (player.getCoins() >= 10) {  // 100 coins to revive
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
            //System.out.println("here");
            player.setHero(new mainHero(player.getHero().getHero().getLayoutX() - 200, player.getHero().getHero().getLayoutY() + 800));
            //GlobalVariables.timeline.setRate(1);  // reset rate
            gameStarted = true;
            //GlobalVariables.timeline.play();
            if (GlobalVariables.sound) {
                GlobalVariables.playerReviveSound.stop();
                GlobalVariables.playerReviveSound.play();
            }
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
                new KeyFrame(Duration.millis(250), new KeyValue(bgAnchorPane.effectProperty(), gaussianBlur)),
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

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Insert code to save game state

            // Close current Pause Menu stage
            stage = (Stage) (exitButton.getScene().getWindow());
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
        this.game = game;  // Passed from playController
        this.player = game.getPlayer();
        GlobalVariables.gameObjects.add(0, player.getHero());
        GlobalVariables.scene.setOnMousePressed(mouseEvent -> {
            if (!mouseEvent.isControlDown()) {
                if (mouseEvent.getPickResult().getIntersectedNode().getId() == null || !mouseEvent.getPickResult().getIntersectedNode().getId().equals("settingsButton")) {
                    if (GlobalVariables.sound) {
                        GlobalVariables.playerLeapSound.stop();
                        GlobalVariables.playerLeapSound.play();
                    }
                    System.out.println("Mouse clicked");
                    player.getHero().setSpeedX(Game.mainHero.getLeapSlice());
                    player.getHero().setLeaped(true);
                    if (player.getHero().getCurrentWeapon() instanceof Shuriken) {
                        Shuriken shuriken = new Shuriken(player.getHero().getHero().getLayoutX(), player.getHero().getHero().getLayoutY(), player.getHero().getCurrentWeapon().getLevel());
                        shuriken.setSpeedX(Shuriken.getThrowSlice());
                        shuriken.setThrown(true);
                        shuriken.addToScreen(gameAnchorPane);
                        gameObjects.add(shuriken);  // Adding shuriken to gameObjects when hero clicks
                        player.getHero().addShuriken();
                    } else if (player.getHero().getCurrentWeapon() instanceof Sword) {
                        ((Sword) player.getHero().getCurrentWeapon()).setUsed(true);  // Play sword animation
                    }
                    // Reset all flags here if any
                }
            }
        });
    }

    public void displayFPS() {
        now = System.nanoTime();  // Current time in nanoseconds
        long prevFrameTime = frameTimes[frameIndex];
        frameTimes[frameIndex] = now;
        frameIndex = (frameIndex + 1) % frameTimes.length;
        if (frameIndex == 0) {
            arrayFilled = true;
        }
        if (arrayFilled) {
            long elapsedNanoseconds = now - prevFrameTime;
            long elapsedNanosecondsPerFrame = elapsedNanoseconds / frameTimes.length;
            double frameRate = 1_000_000_000.0 / elapsedNanosecondsPerFrame;
            fpsLabel.setText(String.format("%.1f", frameRate));  // Display the current FPS on the screen
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
                } else {
                    swordImage.setOpacity(1);
                    swordLevel.setText(String.format("%d", ((Sword) player.getHero().getUnlockedWeapons().get(i)).getLevel()));
                }
            }
        } else if (player.getHero().getUnlockedWeapons().size() == 1) {
            if (player.getHero().getUnlockedWeapons().get(0) instanceof Shuriken) {
                shurikenImage.setOpacity(1);
                shurikenLevel.setText(String.format("%d", ((Shuriken) player.getHero().getUnlockedWeapons().get(0)).getLevel()));
            } else {
                swordImage.setOpacity(1);
                swordLevel.setText(String.format("%d", ((Sword) player.getHero().getUnlockedWeapons().get(0)).getLevel()));
            }
        } else {
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
            if (GlobalVariables.gameObjects.get(i).getP().getX() - player.getHero().getHero().getLayoutX() <= 700 && !GlobalVariables.gameObjects.get(i).isAdded()) {
                System.out.println("added this item bro: " + GlobalVariables.gameObjects.get(i));
                gameObjects.add(GlobalVariables.gameObjects.get(i));
                GlobalVariables.gameObjects.get(i).setAdded(true);
                if (GlobalVariables.gameObjects.get(i) instanceof mainHero) {
                    ((mainHero) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof smallPlatform) {
                    ((smallPlatform) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof mediumPlatform) {
                    ((mediumPlatform) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof bigPlatform) {
                    ((bigPlatform) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof greenOrc) {
                    ((greenOrc) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof redOrc) {
                    ((redOrc) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof bossOrc) {
                    ((bossOrc) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof coinChest) {
                    ((coinChest) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof weaponChest) {
                    ((weaponChest) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof TNT) {
                    ((TNT) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                } else if (GlobalVariables.gameObjects.get(i) instanceof Coin) {
                    ((Coin) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
            }
        }
    }

    // Remove objects from the screen and local arraylist for optimized rendering
    public void destroyObjects() {
        for (int i = 0; i < gameObjects.size(); i++) {
            if (gameObjects.get(i) instanceof smallPlatform) {
                if (player.getHero().getHero().getLayoutX() - ((smallPlatform) gameObjects.get(i)).getsPlatform().getLayoutX() >= 600) {
                    ((smallPlatform) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof mediumPlatform) {
                if (player.getHero().getHero().getLayoutX() - ((mediumPlatform) gameObjects.get(i)).getmPlatform().getLayoutX() >= 600) {
                    ((mediumPlatform) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof bigPlatform) {
                if (player.getHero().getHero().getLayoutX() - ((bigPlatform) gameObjects.get(i)).getbPlatform().getLayoutX() >= 800) {
                    ((bigPlatform) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof greenOrc) {
                if (player.getHero().getHero().getLayoutX() - ((greenOrc) gameObjects.get(i)).getGreenOrc().getLayoutX() >= 600) {
                    ((greenOrc) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof redOrc) {
                if (player.getHero().getHero().getLayoutX() - ((redOrc) gameObjects.get(i)).getRedOrc().getLayoutX() >= 600) {
                    ((redOrc) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof bossOrc) {
                if (player.getHero().getHero().getLayoutX() - ((bossOrc) gameObjects.get(i)).getBossOrc().getLayoutX() >= 600) {
                    ((bossOrc) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof coinChest) {
                if (player.getHero().getHero().getLayoutX() - ((coinChest) gameObjects.get(i)).getCoinChestImageView().getLayoutX() >= 600) {
                    ((coinChest) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof weaponChest) {
                if (player.getHero().getHero().getLayoutX() - ((weaponChest) gameObjects.get(i)).getWeaponChestImageView().getLayoutX() >= 600) {
                    ((weaponChest) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            } else if (gameObjects.get(i) instanceof TNT) {
                if (player.getHero().getHero().getLayoutX() - ((TNT) gameObjects.get(i)).getTntImage().getLayoutX() >= 600) {
                    ((TNT) gameObjects.get(i)).removeFromScreen();
                    System.out.println("Removed: " + gameObjects.get(i));
                    gameObjects.remove(gameObjects.get(i));
                }
            }
        }
    }

    public void playerDeath(int deathType) {  // 0 for fall death, 1 for normal death
        GlobalVariables.timeline.pause();
        // Pause time
        if (deathType == 0 && GlobalVariables.sound) {
            GlobalVariables.playerFallSound.stop();
            GlobalVariables.playerFallSound.play();
        } else if (deathType == 1 && GlobalVariables.sound) {
            GlobalVariables.playerDeathSound.stop();
            GlobalVariables.playerDeathSound.play();
        }
        gaussianBlur.setRadius(15);
        if (player.isRevived()) {
            reviveButton.setDisable(true);
            reviveButton.setOpacity(0.2);
        }
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
        }));
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            Animations.translateTransition(gameOverAnchorPane, 0, -550, 500, 1, false).play();
            Animations.translateTransition(scoreLabel, 0, 125, 500, 1, false).play();
        }));
        Timeline timeline3 = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(gameAnchorPane.effectProperty(), gaussianBlur)),
                new KeyFrame(Duration.millis(250), new KeyValue(bgAnchorPane.effectProperty(), gaussianBlur)),
                new KeyFrame(Duration.millis(500), new KeyValue(scoreLabel.scaleXProperty(), 2)),
                new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleYProperty(), 2)),
                new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.textFillProperty(), scoreColor)));
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
        //GlobalVariables.timeline.pause();

        //GlobalVariables.timeline.stop();
        gameStarted = false;
    }

    public void run() {
        System.out.println("Size of local: " + gameObjects.size());
        displayFPS();
        displayScore();
        displayCoins();
        displayPlayerWeapons();
        generateObjects();  // Checks each time whether the next item in queue is within a specified distance of the player (camera)
        destroyObjects();
        GlobalVariables.timeline.setRate((428.746463 * Math.pow(0.868535805, gameObjects.size()) / GlobalVariables.difficulty));
        camera.update(player.getHero(), gameAnchorPane, bgAnchorPane);  // Follow the player
        if (gameStarted) {
            for (int i = 0; i < gameObjects.size(); i++) {
                for (GameObject gameObject : gameObjects) {
                    if (gameObjects.get(i) instanceof mainHero) {
                        // Player Movement
                        if (!((mainHero) gameObjects.get(i)).isLeaped()) {  // Stop Y axis motion when player leaps
                            if (((mainHero) gameObjects.get(i)).getCurrentJumpHeight() > ((mainHero) gameObjects.get(i)).getJumpHeight()) {
                                ((mainHero) gameObjects.get(i)).setSetY(((mainHero) gameObjects.get(i)).getSpeedY() - Game.mainHero.getAccelerationY());
                                ((mainHero) gameObjects.get(i)).setSpeedY(((mainHero) gameObjects.get(i)).getSetY());
                                ((mainHero) gameObjects.get(i)).jump();
                                ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(((mainHero) gameObjects.get(i)).getCurrentJumpHeight() + ((mainHero) gameObjects.get(i)).getSetY());
                            } else {
                                ((mainHero) gameObjects.get(i)).setSetY(((mainHero) gameObjects.get(i)).getSpeedY() + Game.mainHero.getAccelerationY());
                                ((mainHero) gameObjects.get(i)).setSpeedY(((mainHero) gameObjects.get(i)).getSetY());
                                ((mainHero) gameObjects.get(i)).jump();
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
                                // Uncomment below code at the end
                                //((mainHero) gameObjects.get(i)).setSpeedY(((mainHero) gameObjects.get(i)).getSetY());  // At the end of the leap, set Y speed back to before and X to 0.
                                ((mainHero) gameObjects.get(i)).setSpeedX(0);
                                ((mainHero) gameObjects.get(i)).setCurrentLeapLength(0);
                                ((mainHero) gameObjects.get(i)).setLeaped(false);
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getLayoutY() < 300)  // To prevent user from gaining score from leaping mid fall
                                    player.increaseScore();
                            }
                        }
                        if (gameObject instanceof smallPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (GlobalVariables.sound) {
                                    GlobalVariables.playerJumpSound.stop();
                                    GlobalVariables.playerJumpSound.play();
                                }
                                ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                System.out.println("Speed on small: " + ((mainHero) gameObjects.get(i)).getSpeedY());
                            }
                        } else if (gameObject instanceof mediumPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (GlobalVariables.sound) {
                                    GlobalVariables.playerJumpSound.stop();
                                    GlobalVariables.playerJumpSound.play();
                                }
                                ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                System.out.println("Speed on medium: " + ((mainHero) gameObjects.get(i)).getSpeedY());
                            }
                        } else if (gameObject instanceof bigPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (GlobalVariables.sound) {
                                    GlobalVariables.playerJumpSound.stop();
                                    GlobalVariables.playerJumpSound.play();
                                }
                                ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                System.out.println("Speed on big: " + ((mainHero) gameObjects.get(i)).getSpeedY());
                            }
                        } else if (gameObject instanceof redOrc) {
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
//                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getSpeedX() > 0) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
//                                    if (GlobalVariables.sound) {
//                                        GlobalVariables.playerJumpSound.stop();
//                                        GlobalVariables.playerJumpSound.play();
//                                    }
//                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
//                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
//                                }
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getSpeedX() > 0) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    if (GlobalVariables.sound) {
                                        GlobalVariables.playerJumpSound.stop();
                                        GlobalVariables.playerJumpSound.play();
                                    }
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                }
                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getBottomRectangle().getBoundsInParent()) &&  // Only when the player hits the bottom rectangle, he's considered dead
                                        !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent()) ||
                                                ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getTopRectangle().getBoundsInParent()) ||
                                                ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((redOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    gameStarted = false;
                                    playerDeath(1);
                                }
                            }
                        } else if (gameObject instanceof greenOrc) {
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
                                    if (GlobalVariables.sound) {
                                        GlobalVariables.playerJumpSound.stop();
                                        GlobalVariables.playerJumpSound.play();
                                    }
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                }

                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getBottomRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getTopRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    gameStarted = false;
                                    playerDeath(1);
                                }
                            }
                        } else if (gameObject instanceof bossOrc) {
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
                                    if (GlobalVariables.sound) {
                                        GlobalVariables.playerJumpSound.stop();
                                        GlobalVariables.playerJumpSound.play();
                                    }
                                    ((mainHero) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((mainHero) gameObjects.get(i)).setSpeedY(-Game.mainHero.getJumpSlice());
                                }

                                if (((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getBottomRectangle().getBoundsInParent()) && !(((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getTopRectangle().getBoundsInParent()) || ((mainHero) gameObjects.get(i)).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    gameStarted = false;
                                    playerDeath(1);
                                }
                            }
                        } else if (gameObject instanceof weaponChest) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((weaponChest) gameObject).isActivated()) {
                                    ((weaponChest) gameObject).playChestAnimation(player);
                                    ((weaponChest) gameObject).setActivated(true);
                                    // Upgrade existing weapon
                                    if (((mainHero) gameObjects.get(i)).getCurrentWeapon() != null) {
                                        if (((weaponChest) gameObject).getWeaponType() == ((mainHero) gameObjects.get(i)).getCurrentWeapon().getWeaponType() && ((mainHero) gameObjects.get(i)).getCurrentWeapon().getLevel() == 1) {
                                            ((mainHero) gameObjects.get(i)).getCurrentWeapon().upgrade();
                                        }
                                    }
                                }
                            }
                        } else if (gameObject instanceof coinChest) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((coinChest) gameObject).isActivated()) {
                                    ((coinChest) gameObject).playChestAnimation(player);
                                    ((coinChest) gameObject).setActivated(true);
                                }
                            }
                        } else if (gameObject instanceof TNT) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((TNT) gameObject).isActivated()) {
                                    ((TNT) gameObject).setActivated(true);
                                    ((TNT) gameObject).playTNTAnimation();  // Resets explosion flags inside
                                } else if (((TNT) gameObject).isExplosionActivated()) {
                                    gameStarted = false;
                                    playerDeath(1);
                                }
                            }
                        } else if (gameObject instanceof Coin) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((Coin) gameObject).isCollected()) {
                                    player.increaseCoins(1);
                                    ((Coin) gameObject).playCoinAnimation();
                                }
                            }
                        }

                        if (((mainHero) gameObjects.get(i)).getHeroPolygon().getLayoutY() > 780) {  // player death fall detection
                            gameStarted = false;
                            playerDeath(0);
                        }
                    } else if (gameObjects.get(i) instanceof smallPlatform) {

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
                            if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent())) {
                                System.out.println("Green orc killed: " + ((greenOrc) gameObjects.get(i)).isKilled());
                                if (!((greenOrc) gameObjects.get(i)).isKilled()) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof greenOrc && !gameObject.equals(gameObjects.get(i))) {  // Check for green orc other than self
                            // Green orc collision with green orc
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("green hit green");
                                if (((greenOrc) gameObjects.get(i)).getRightRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((greenOrc) gameObject).isPushed()) {
                                        System.out.println("Player speed before collision: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                        ((greenOrc) gameObject).setSpeedX(((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.greenOrc.getWeight())) + (((0.0) * ((greenOrc) gameObject).getSpeedX()) / (Game.greenOrc.getWeight() + Game.greenOrc.getWeight())));
                                        ((greenOrc) gameObject).setPushed(true);
                                        ((greenOrc) gameObjects.get(i)).setSpeedX((((0.0) * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.greenOrc.getWeight())) + ((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObject).getSpeedX()) / (Game.greenOrc.getWeight() + Game.greenOrc.getWeight())) - 0.01);
                                        System.out.println("Player speed after collision: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                        System.out.println("Boss speed after collision: " + ((greenOrc) gameObject).getSpeedX());
                                        System.out.println("Green orc after rebound: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                        ((greenOrc) gameObjects.get(i)).push();
                                        ((greenOrc) gameObjects.get(i)).setSpeedX(0);
                                    }
                                }
                                if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((greenOrc) gameObjects.get(i)).getSpeedX() > 0) && !(((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof redOrc) {
                            // Green orc collision with red orc
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("green hit red");
                                if (((greenOrc) gameObjects.get(i)).getRightRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((redOrc) gameObject).isPushed()) {
                                        System.out.println("Player speed before collision: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                        ((redOrc) gameObject).setSpeedX(((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.redOrc.getWeight())) + (((Game.redOrc.getWeight() - Game.greenOrc.getWeight()) * ((redOrc) gameObject).getSpeedX()) / (Game.greenOrc.getWeight() + Game.redOrc.getWeight())));
                                        ((redOrc) gameObject).setPushed(true);
                                        ((greenOrc) gameObjects.get(i)).setSpeedX((((Game.greenOrc.getWeight() - Game.redOrc.getWeight()) * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.redOrc.getWeight())) + ((2 * Game.redOrc.getWeight() * ((redOrc) gameObject).getSpeedX()) / (Game.greenOrc.getWeight() + Game.redOrc.getWeight())) - 0.01);
                                        System.out.println("Player speed after collision: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                        System.out.println("Boss speed after collision: " + ((redOrc) gameObject).getSpeedX());
                                        // Boss orc comes forward and attacks if speed is negative
                                        ((greenOrc) gameObjects.get(i)).push();
                                        ((greenOrc) gameObjects.get(i)).setSpeedX(0);
                                    }
                                }
                                if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((greenOrc) gameObjects.get(i)).getSpeedX() > 0) && !(((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof bossOrc) {
                            // Green orc collision with boss orc
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (((greenOrc) gameObjects.get(i)).getRightRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((bossOrc) gameObject).isPushed()) {
                                        System.out.println("Player speed before collision: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                        ((bossOrc) gameObject).setSpeedX(((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight())) + (((Game.bossOrc.getWeight() - Game.greenOrc.getWeight()) * ((bossOrc) gameObject).getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight())));
                                        ((bossOrc) gameObject).setPushed(true);
                                        ((greenOrc) gameObjects.get(i)).setSpeedX((((Game.greenOrc.getWeight() - Game.bossOrc.getWeight()) * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight())) + ((2 * Game.bossOrc.getWeight() * ((bossOrc) gameObject).getSpeedX()) / (Game.greenOrc.getWeight() + Game.bossOrc.getWeight())) - 0.01);
                                        System.out.println("Player speed after collision: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                        System.out.println("Boss speed after collision: " + ((bossOrc) gameObject).getSpeedX());
                                        // Boss orc comes forward and attacks if speed is negative
                                        ((greenOrc) gameObjects.get(i)).push();
                                        ((greenOrc) gameObjects.get(i)).setSpeedX(0);
                                    }
                                }
                                if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((greenOrc) gameObjects.get(i)).getSpeedX() > 0) && !(((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof coinChest) {
                            // Green orc collision with coin chest
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (!((coinChest) gameObject).isPushed()) {
                                    System.out.println("INSIDE");
                                    ((coinChest) gameObject).setSpeedX((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.coinChest.getWeight()));
                                    ((coinChest) gameObject).setPushed(true);
                                    ((greenOrc) gameObjects.get(i)).setSpeedX(((Game.greenOrc.getWeight() - Game.coinChest.getWeight()) * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.coinChest.getWeight()));
                                    System.out.println("Green orc after rebound: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                    ((greenOrc) gameObjects.get(i)).push();
                                    ((greenOrc) gameObjects.get(i)).setSpeedX(0);
                                }
                                if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent()) && !(((greenOrc) gameObjects.get(i)).getSpeedX() > 0)) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof weaponChest) {
                            // Green orc collision with weapon chest
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (!((weaponChest) gameObject).isPushed()) {
                                    System.out.println("INSIDE");
                                    ((weaponChest) gameObject).setSpeedX((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.weaponChest.getWeight()));
                                    ((weaponChest) gameObject).setPushed(true);
                                    ((greenOrc) gameObjects.get(i)).setSpeedX(((Game.greenOrc.getWeight() - Game.weaponChest.getWeight()) * ((greenOrc) gameObjects.get(i)).getSpeedX()) / (Game.greenOrc.getWeight() + Game.weaponChest.getWeight()));
                                    System.out.println("Green orc after rebound: " + ((greenOrc) gameObjects.get(i)).getSpeedX());
                                    ((greenOrc) gameObjects.get(i)).push();
                                    ((greenOrc) gameObjects.get(i)).setSpeedX(0);
                                }
                                if (((greenOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent()) && !(((greenOrc) gameObjects.get(i)).getSpeedX() > 0)) {
                                    ((greenOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((greenOrc) gameObjects.get(i)).setSpeedY(-Game.greenOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof TNT) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((TNT) gameObject).isActivated()) {
                                    ((TNT) gameObject).setActivated(true);
                                    ((TNT) gameObject).playTNTAnimation();
                                } else if (((TNT) gameObject).isExplosionActivated()) {
                                    if (!((greenOrc) gameObjects.get(i)).isKilled()) {
                                        ((greenOrc) gameObjects.get(i)).setKilled(true);
                                        ((greenOrc) gameObjects.get(i)).playDeathAnimation(1, player);
                                    }
                                }
                            }
                        }

                        if (((greenOrc) gameObjects.get(i)).getTopRectangle().getLayoutY() > 780) {  // Death fall detection
                            ((greenOrc) gameObjects.get(i)).setSpeedY(0);
                            if (!((greenOrc) gameObjects.get(i)).isKilled()) {
                                ((greenOrc) gameObjects.get(i)).setKilled(true);
                                ((greenOrc) gameObjects.get(i)).playDeathAnimation(0, player);
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
                        else if (gameObject instanceof greenOrc) {
                            // Red orc collision with green orc
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("Red hit green");
                                if (((redOrc) gameObjects.get(i)).getRightRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((greenOrc) gameObject).isPushed()) {
                                        System.out.println("Player speed before collision: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                        ((greenOrc) gameObject).setSpeedX(((2 * Game.redOrc.getWeight() * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.greenOrc.getWeight())) + (((Game.greenOrc.getWeight() - Game.redOrc.getWeight()) * ((greenOrc) gameObject).getSpeedX()) / (Game.redOrc.getWeight() + Game.greenOrc.getWeight())));
                                        ((greenOrc) gameObject).setPushed(true);
                                        ((redOrc) gameObjects.get(i)).setSpeedX((((Game.redOrc.getWeight() - Game.greenOrc.getWeight()) * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.greenOrc.getWeight())) + ((2 * Game.greenOrc.getWeight() * ((greenOrc) gameObject).getSpeedX()) / (Game.redOrc.getWeight() + Game.greenOrc.getWeight())) - 0.01);
                                        System.out.println("Player speed after collision: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                        System.out.println("Boss speed after collision: " + ((greenOrc) gameObject).getSpeedX());
                                        // Boss orc comes forward and attacks if speed is negative
                                        ((redOrc) gameObjects.get(i)).push();
                                        ((redOrc) gameObjects.get(i)).setSpeedX(0);
                                    }
                                }
                                if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((redOrc) gameObjects.get(i)).getSpeedX() > 0) && !(((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        }

                        else if (gameObject instanceof redOrc) {
                            // Red orc collision with red orc
                            if (gameObjects.get(i).collision_detected(gameObject) && !gameObject.equals(gameObjects.get(i))) { // Left collision for push
                                System.out.println("REACHED");
                                if (((redOrc) gameObjects.get(i)).getRightRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((redOrc) gameObject).isPushed()) {
                                        System.out.println("Player speed before collision: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                        ((redOrc) gameObject).setSpeedX(((2 * Game.redOrc.getWeight() * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.redOrc.getWeight())) + (((0.0) * ((redOrc) gameObject).getSpeedX()) / (Game.redOrc.getWeight() + Game.redOrc.getWeight())));
                                        ((redOrc) gameObject).setPushed(true);
                                        ((redOrc) gameObjects.get(i)).setSpeedX((((0.0) * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.redOrc.getWeight())) + ((2 * Game.redOrc.getWeight() * ((redOrc) gameObject).getSpeedX()) / (Game.redOrc.getWeight() + Game.redOrc.getWeight())) - 0.01);
                                        System.out.println("Player speed after collision: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                        System.out.println("Boss speed after collision: " + ((redOrc) gameObject).getSpeedX());
                                        ((redOrc) gameObjects.get(i)).push();
                                        ((redOrc) gameObjects.get(i)).setSpeedX(0);
                                    }
                                }
                                if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((redOrc) gameObjects.get(i)).getSpeedX() > 0) && !(((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((redOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        }

                        else if (gameObject instanceof bossOrc) {
                            // Red orc collision with boss orc
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (((redOrc) gameObjects.get(i)).getRightRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                    if (!((bossOrc) gameObject).isPushed()) {
                                        System.out.println("Player speed before collision: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                        ((bossOrc) gameObject).setSpeedX(((2 * Game.redOrc.getWeight() * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.bossOrc.getWeight())) + (((Game.bossOrc.getWeight() - Game.redOrc.getWeight()) * ((bossOrc) gameObject).getSpeedX()) / (Game.redOrc.getWeight() + Game.bossOrc.getWeight())));
                                        ((bossOrc) gameObject).setPushed(true);
                                        ((redOrc) gameObjects.get(i)).setSpeedX((((Game.redOrc.getWeight() - Game.bossOrc.getWeight()) * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.bossOrc.getWeight())) + ((2 * Game.bossOrc.getWeight() * ((bossOrc) gameObject).getSpeedX()) / (Game.redOrc.getWeight() + Game.bossOrc.getWeight())) - 0.01);
                                        System.out.println("Player speed after collision: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                        System.out.println("Boss speed after collision: " + ((bossOrc) gameObject).getSpeedX());
                                        // Boss orc comes forward and attacks if speed is negative
                                        ((redOrc) gameObjects.get(i)).push();
                                        ((redOrc) gameObjects.get(i)).setSpeedX(0);
                                    }
                                }
                                if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getTopRectangle().getBoundsInParent()) && !(((redOrc) gameObjects.get(i)).getSpeedX() > 0) && !(((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getLeftRectangle().getBoundsInParent()) || ((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getBottomRectangle().getBoundsInParent()) || ((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) gameObject).getRightRectangle().getBoundsInParent()))) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof coinChest) {
                            // Red orc collision with coin chest
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (!((coinChest) gameObject).isPushed()) {
                                    System.out.println("INSIDE");
                                    ((coinChest) gameObject).setSpeedX((2 * Game.redOrc.getWeight() * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.coinChest.getWeight()));
                                    ((coinChest) gameObject).setPushed(true);
                                    ((redOrc) gameObjects.get(i)).setSpeedX(((Game.redOrc.getWeight() - Game.coinChest.getWeight()) * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.coinChest.getWeight()));
                                    System.out.println("Green orc after rebound: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                    ((redOrc) gameObjects.get(i)).push();
                                    ((redOrc) gameObjects.get(i)).setSpeedX(0);
                                }
                                if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent()) && !(((redOrc) gameObjects.get(i)).getSpeedX() > 0)) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof weaponChest) {
                            // Red orc collision with weapon chest
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                if (!((weaponChest) gameObject).isPushed()) {
                                    System.out.println("INSIDE");
                                    ((weaponChest) gameObject).setSpeedX((2 * Game.redOrc.getWeight() * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.weaponChest.getWeight()));
                                    ((weaponChest) gameObject).setPushed(true);
                                    ((redOrc) gameObjects.get(i)).setSpeedX(((Game.redOrc.getWeight() - Game.weaponChest.getWeight()) * ((redOrc) gameObjects.get(i)).getSpeedX()) / (Game.redOrc.getWeight() + Game.weaponChest.getWeight()));
                                    System.out.println("Green orc after rebound: " + ((redOrc) gameObjects.get(i)).getSpeedX());
                                    ((redOrc) gameObjects.get(i)).push();
                                    ((redOrc) gameObjects.get(i)).setSpeedX(0);
                                }
                                if (((redOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent()) && !(((redOrc) gameObjects.get(i)).getSpeedX() > 0)) {
                                    ((redOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((redOrc) gameObjects.get(i)).setSpeedY(-Game.redOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof TNT) {
                            // Red orc collision with TNT
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
                            ((bossOrc) gameObjects.get(i)).setSetY(((bossOrc) gameObjects.get(i)).getSpeedY() + Game.bossOrc.getAccelerationY());
                            ((bossOrc) gameObjects.get(i)).setSpeedY(((bossOrc) gameObjects.get(i)).getSetY());
                            ((bossOrc) gameObjects.get(i)).jump();
                            //jumpHeight += setY;
                        }
                        if (((bossOrc) gameObjects.get(i)).isPushed()) {
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
                                    if (((bossOrc) gameObjects.get(i)).getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                                        ((bossOrc) gameObjects.get(i)).setSpeedX(((bossOrc) gameObjects.get(i)).getSpeedX() - Game.bossOrc.getAccelerationX());
                                    } else {
                                        ((bossOrc) gameObjects.get(i)).setSpeedX(((bossOrc) gameObjects.get(i)).getSpeedX() + Game.bossOrc.getAccelerationX());
                                    }
                                    timeline.play();
                                }

                            } else {
                                if (((bossOrc) gameObject).isAttacked()) {
                                    ((bossOrc) gameObject).setPushed(false);
                                    if (((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                                        ((bossOrc) gameObject).setSpeedX(((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX());
                                    } else {
                                        ((bossOrc) gameObject).setSpeedX(((bossOrc) gameObject).getSpeedX() + Game.bossOrc.getAccelerationX());
                                    }
                                    ((bossOrc) gameObject).push();
                                } else {
                                    ((bossOrc) gameObject).setPushed(false);
                                    if (((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX() <= 0) {
                                        ((bossOrc) gameObject).setSpeedX(((bossOrc) gameObject).getSpeedX() - Game.bossOrc.getAccelerationX());
                                    } else {
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
                        } else if (gameObject instanceof coinChest) {
                            // Boss orc collision with coin chest
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (!((coinChest) gameObject).isPushed()) {
                                    System.out.println("INSIDE");
                                    ((coinChest) gameObject).setSpeedX((2 * Game.bossOrc.getWeight() * ((bossOrc) gameObjects.get(i)).getSpeedX()) / (Game.bossOrc.getWeight() + Game.coinChest.getWeight()));
                                    ((coinChest) gameObject).setPushed(true);
                                    ((bossOrc) gameObjects.get(i)).setSpeedX(((Game.bossOrc.getWeight() - Game.coinChest.getWeight()) * ((bossOrc) gameObjects.get(i)).getSpeedX()) / (Game.bossOrc.getWeight() + Game.coinChest.getWeight()));
                                    System.out.println("Green orc after rebound: " + ((bossOrc) gameObjects.get(i)).getSpeedX());
                                    ((bossOrc) gameObjects.get(i)).push();
                                    ((bossOrc) gameObjects.get(i)).setSpeedX(0);
                                }
                                if (((bossOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent()) && !(((bossOrc) gameObjects.get(i)).getSpeedX() > 0)) {
                                    ((bossOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((bossOrc) gameObjects.get(i)).setSpeedY(-Game.bossOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof weaponChest) {
                            // Boss orc collision with weapon chest
                            if (gameObjects.get(i).collision_detected(gameObject)) { // Left collision for push
                                System.out.println("REACHED");
                                if (!((weaponChest) gameObject).isPushed()) {
                                    System.out.println("INSIDE");
                                    ((weaponChest) gameObject).setSpeedX((2 * Game.bossOrc.getWeight() * ((bossOrc) gameObjects.get(i)).getSpeedX()) / (Game.bossOrc.getWeight() + Game.weaponChest.getWeight()));
                                    ((weaponChest) gameObject).setPushed(true);
                                    ((bossOrc) gameObjects.get(i)).setSpeedX(((Game.bossOrc.getWeight() - Game.weaponChest.getWeight()) * ((bossOrc) gameObjects.get(i)).getSpeedX()) / (Game.bossOrc.getWeight() + Game.weaponChest.getWeight()));
                                    ((bossOrc) gameObjects.get(i)).push();
                                    ((bossOrc) gameObjects.get(i)).setSpeedX(0);
                                }
                                if (((bossOrc) gameObjects.get(i)).getBottomRectangle().getBoundsInParent().intersects(((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent()) && !(((bossOrc) gameObjects.get(i)).getSpeedX() > 0)) {
                                    ((bossOrc) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((bossOrc) gameObjects.get(i)).setSpeedY(-Game.bossOrc.getJumpSlice());
                                }
                            }
                        } else if (gameObject instanceof TNT) {
                            // Boss orc collision with TNT
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                if (!((TNT) gameObject).isActivated()) {
                                    ((TNT) gameObject).setActivated(true);
                                    ((TNT) gameObject).playTNTAnimation();
                                } else if (((TNT) gameObject).isExplosionActivated()) {
                                    if (!((bossOrc) gameObjects.get(i)).isKilled()) {
                                        ((bossOrc) gameObjects.get(i)).setKilled(true);
                                        ((bossOrc) gameObjects.get(i)).playDeathAnimation(1, player);
                                    }
                                }
                            }
                        }
                    }

                    else if (gameObjects.get(i) instanceof coinChest) {
                        // coin chest movements
                        if (((coinChest) gameObjects.get(i)).getCurrentJumpHeight() > ((coinChest) gameObjects.get(i)).getJumpHeight()) {
                            ((coinChest) gameObjects.get(i)).setSetY(((coinChest) gameObjects.get(i)).getSpeedY() - Game.coinChest.getAccelerationY());
                            ((coinChest) gameObjects.get(i)).setSpeedY(((coinChest) gameObjects.get(i)).getSetY());
                            ((coinChest) gameObjects.get(i)).jump();
                            ((coinChest) gameObjects.get(i)).setCurrentJumpHeight(((coinChest) gameObjects.get(i)).getCurrentJumpHeight() + ((coinChest) gameObjects.get(i)).getSetY());

                        } else {
                            ((coinChest) gameObjects.get(i)).setSetY(((coinChest) gameObjects.get(i)).getSpeedY() + Game.coinChest.getAccelerationY());
                            ((coinChest) gameObjects.get(i)).setSpeedY(((coinChest) gameObjects.get(i)).getSetY());
                            ((coinChest) gameObjects.get(i)).jump();
                            //jumpHeight += setY;
                        }
                        if (((coinChest) gameObjects.get(i)).isPushed()) {
                            if (((coinChest) gameObjects.get(i)).getSpeedX() <= 0 || ((coinChest) gameObjects.get(i)).getSpeedX() + Game.coinChest.getAccelerationX() <= 0) {
                                ((coinChest) gameObjects.get(i)).setPushed(false);
                            } else {
                                ((coinChest) gameObjects.get(i)).setSpeedX(((coinChest) gameObjects.get(i)).getSpeedX() + Game.coinChest.getAccelerationX());
                                ((coinChest) gameObjects.get(i)).push();
                            }
                        }

                        if (gameObject instanceof smallPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                System.out.println("This is collision with small platform sir");
                                    ((coinChest) gameObjects.get(i)).setCurrentJumpHeight(0);
                                    ((coinChest) gameObjects.get(i)).setSpeedY(-Game.coinChest.getJumpSlice());
                            }
                        } else if (gameObject instanceof mediumPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                ((coinChest) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((coinChest) gameObjects.get(i)).setSpeedY(-Game.coinChest.getJumpSlice());
                            }
                        } else if (gameObject instanceof bigPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                ((coinChest) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((coinChest) gameObjects.get(i)).setSpeedY(-Game.coinChest.getJumpSlice());

                            }
                        }

                        if (((coinChest) gameObjects.get(i)).getCoinChestPolygon().getLayoutY() > 780){
                            ((coinChest) gameObjects.get(i)).setSpeedY(0);  // Stop y axis motion for easy garbage collection
                        }
                    }

                    else if (gameObjects.get(i) instanceof weaponChest) {
                        // coin chest movements
                        if (((weaponChest) gameObjects.get(i)).getCurrentJumpHeight() > ((weaponChest) gameObjects.get(i)).getJumpHeight()) {
                            ((weaponChest) gameObjects.get(i)).setSetY(((weaponChest) gameObjects.get(i)).getSpeedY() - Game.weaponChest.getAccelerationY());
                            ((weaponChest) gameObjects.get(i)).setSpeedY(((weaponChest) gameObjects.get(i)).getSetY());
                            ((weaponChest) gameObjects.get(i)).jump();
                            ((weaponChest) gameObjects.get(i)).setCurrentJumpHeight(((weaponChest) gameObjects.get(i)).getCurrentJumpHeight() + ((weaponChest) gameObjects.get(i)).getSetY());

                        } else {
                            ((weaponChest) gameObjects.get(i)).setSetY(((weaponChest) gameObjects.get(i)).getSpeedY() + Game.weaponChest.getAccelerationY());
                            ((weaponChest) gameObjects.get(i)).setSpeedY(((weaponChest) gameObjects.get(i)).getSetY());
                            ((weaponChest) gameObjects.get(i)).jump();
                            //jumpHeight += setY;
                        }
                        if (((weaponChest) gameObjects.get(i)).isPushed()) {
                            if (((weaponChest) gameObjects.get(i)).getSpeedX() <= 0 || ((weaponChest) gameObjects.get(i)).getSpeedX() + Game.weaponChest.getAccelerationX() <= 0) {
                                ((weaponChest) gameObjects.get(i)).setPushed(false);
                            } else {
                                ((weaponChest) gameObjects.get(i)).setSpeedX(((weaponChest) gameObjects.get(i)).getSpeedX() + Game.weaponChest.getAccelerationX());
                                ((weaponChest) gameObjects.get(i)).push();
                            }
                        }

                        if (gameObject instanceof smallPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                ((weaponChest) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((weaponChest) gameObjects.get(i)).setSpeedY(-Game.weaponChest.getJumpSlice());
                            }
                        } else if (gameObject instanceof mediumPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                ((weaponChest) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((weaponChest) gameObjects.get(i)).setSpeedY(-Game.weaponChest.getJumpSlice());

                            }
                        } else if (gameObject instanceof bigPlatform) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                ((weaponChest) gameObjects.get(i)).setCurrentJumpHeight(0);
                                ((weaponChest) gameObjects.get(i)).setSpeedY(-Game.weaponChest.getJumpSlice());
                            }
                        }

                        if (((weaponChest) gameObjects.get(i)).getWeaponChestPolygon().getLayoutY() > 780) {
                            ((weaponChest) gameObjects.get(i)).setSpeedY(0);  // Stop y axis motion for easy garbage collection
                        }
                    }
                    else if (gameObjects.get(i) instanceof Shuriken) {
                        if (((Shuriken) gameObjects.get(i)).isThrown()) {
                            ((Shuriken) gameObjects.get(i)).setSpeedX(((Shuriken) gameObjects.get(i)).getSpeedX() + Game.Shuriken.getAccelerationX());
                            ((Shuriken) gameObjects.get(i)).throwShuriken();
                            ((Shuriken) gameObjects.get(i)).setTotalDistance(((Shuriken) gameObjects.get(i)).getTotalDistance() + ((Shuriken) gameObjects.get(i)).getSpeedX() + Game.Shuriken.getAccelerationX());
                        }

                        if (gameObject instanceof greenOrc) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                Glow glow1 = new Glow();
                                Glow glow2 = new Glow();
                                glow1.setLevel(0.5);
                                glow2.setLevel(0);
                                Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((greenOrc) gameObject).getGreenOrc().effectProperty(), glow1)));
                                Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((greenOrc) gameObject).getGreenOrc().effectProperty(), glow2)));
                                timeline1.setOnFinished(event -> timeline2.play());
                                timeline1.play();
                                ((greenOrc) gameObject).setHealth(((greenOrc) gameObject).getHealth() - (((Shuriken) gameObjects.get(i))).getDamage());
                                gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShuriken());
                                gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShurikenPolygon());
                                if (((greenOrc) gameObject).getHealth() <= 0 && !((greenOrc) gameObject).isKilled()) {
                                    ((greenOrc) gameObject).setKilled(true);
                                    ((greenOrc) gameObject).playDeathAnimation(1, player);
                                }
                                gameObjects.remove(i);  // Remove from gameObjects after collision with orc
                                player.getHero().removeShuriken();  // To reduce FPS fluctuation
                            }
                        }
                        else if (gameObject instanceof redOrc) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                Glow glow1 = new Glow();
                                Glow glow2 = new Glow();
                                glow1.setLevel(0.5);
                                glow2.setLevel(0);
                                Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((redOrc) gameObject).getRedOrc().effectProperty(), glow1)));
                                Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((redOrc) gameObject).getRedOrc().effectProperty(), glow2)));
                                timeline1.setOnFinished(event -> timeline2.play());
                                timeline1.play();
                                ((redOrc) gameObject).setHealth(((redOrc) gameObject).getHealth() - (((Shuriken) gameObjects.get(i))).getDamage());
                                gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShuriken());
                                gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShurikenPolygon());
                                if (((redOrc) gameObject).getHealth() <= 0 && !((redOrc) gameObject).isKilled()) {
                                    ((redOrc) gameObject).setKilled(true);
                                    ((redOrc) gameObject).playDeathAnimation(1, player);
                                }
                                gameObjects.remove(i);  // Remove from gameObjects after collision with orc
                                player.getHero().removeShuriken();  // To reduce FPS fluctuation
                            }
                        }
                        else if (gameObject instanceof bossOrc) {
                            if (gameObjects.get(i).collision_detected(gameObject)) {
                                Glow glow1 = new Glow();
                                Glow glow2 = new Glow();
                                glow1.setLevel(0.5);
                                glow2.setLevel(0);
                                Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((bossOrc) gameObject).getBossOrc().effectProperty(), glow1)));
                                Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((bossOrc) gameObject).getBossOrc().effectProperty(), glow2)));
                                timeline1.setOnFinished(event -> timeline2.play());
                                timeline1.play();
                                ((bossOrc) gameObject).setHealth(((bossOrc) gameObject).getHealth() - (((Shuriken) gameObjects.get(i))).getDamage());
                                gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShuriken());
                                gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShurikenPolygon());
                                if (((bossOrc) gameObject).getHealth() <= 0 && !((bossOrc) gameObject).isKilled()) {
                                    ((bossOrc) gameObject).setKilled(true);
                                    ((bossOrc) gameObject).playDeathAnimation(1, player);
                                }
                                gameObjects.remove(i);  // Remove from gameObjects after collision with orc
                                player.getHero().removeShuriken();  // To reduce FPS fluctuation
                            }
                        }
                        if (((Shuriken) gameObjects.get(i)).getTotalDistance() >= 400) {
                            System.out.println("Shuriken Attack 2");
                            gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShuriken());
                            gameAnchorPane.getChildren().remove(((Shuriken) gameObjects.get(i)).getShurikenPolygon());
                            gameObjects.remove(i);  // Remove shuriken from gameObjects after 500 distance (for an ever so little better rendering)
                            player.getHero().removeShuriken();  // To reduce FPS fluctuation
                        }
                    }
                }
            }
        }
    }
}
