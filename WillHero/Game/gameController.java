package Game;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.*;

public class gameController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

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
    @FXML
    private ImageView endMessage;
    private ImageView reviveStrip;
    private ImageView angelWings;
    private ImageView blackBar1, blackBar2;


    private Camera camera;
    private Main game;
    private Player player;
    private boolean gameStarted;
    private LinkedList<GameObject> gameObjects;
    private ArrayList<GameObject> unlockedWeapons;
    private Weapon currentWeapon;
    private Color scoreColor;
    private int numberOfCoins;  // For FPS optimization

    // FPS computation
    private final long[] frameTimes = new long[100];
    private int frameIndex = 0;
    private boolean arrayFilled = false;
    long now;  // Current time

    GaussianBlur gaussianBlur = new GaussianBlur();
    Glow glow = new Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GlobalVariables.gameAnchorPane = gameAnchorPane;
        // Use reference of Main game here.
        camera = new Camera(0, 0);
        gameStarted = false;
        gameObjects = new LinkedList<>();
        unlockedWeapons = new ArrayList<>();
        scoreColor = Color.BLACK;
        if (GlobalVariables.difficulty == 50) {  // Hard mode
            GlobalVariables.mediaPlayer.stop();
            videoMediaView.setMediaPlayer(GlobalVariables.backgroundVideo);
            scoreColor = Color.WHITE;
            gameAnchorPane.setOpacity(0.5);
            GlobalVariables.sound = false;
            GlobalVariables.backgroundVideo.setCycleCount(MediaPlayer.INDEFINITE);
            GlobalVariables.backgroundVideo.play();
            GlobalVariables.eerieMusic.setCycleCount(MediaPlayer.INDEFINITE);
            if (GlobalVariables.music) {
                GlobalVariables.eerieMusic.stop();
                GlobalVariables.eerieMusic.play();
            }
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

    public void reviveButtonClicked() throws IOException {
        if (player.getCoins() < 100) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not enough coins");
            alert.setHeaderText("You don't have enough coins to revive :(");
            alert.showAndWait();
        }
        else if (player.getCoins() >= 100) { // 100 coins to revive
            System.out.println(gameObjects);
            player.setRevived(true);
            player.useCoins(100);  // uncomment
            gaussianBlur.setRadius(0);
            Animations.translateTransition(gameOverAnchorPane, 0, 550, 500, 1, false).play();
            Animations.translateTransition(scoreLabel, 0, -125, 500, 1, false).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(gameAnchorPane.effectProperty(), gaussianBlur)),
                    new KeyFrame(Duration.millis(250), new KeyValue(bgAnchorPane.effectProperty(), gaussianBlur)),
                    new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleXProperty(), 1)),
                    new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.scaleYProperty(), 1)),
                    new KeyFrame(Duration.millis(250), new KeyValue(scoreLabel.textFillProperty(), Color.rgb(97, 90, 90))));
            timeline.play();
            //GlobalVariables.timeline.pause();

            // Reopen game stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGame.fxml"));
            GlobalVariables.root = loader.load();
            stage = (Stage) (restartButton.getScene().getWindow());
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
            GlobalVariables.scene = new Scene(GlobalVariables.root);
            reviveStrip = new ImageView();
            reviveStrip.setFitWidth(117);
            reviveStrip.setFitHeight(675);
            reviveStrip.setPreserveRatio(false);
            reviveStrip.setOpacity(0);
            reviveStrip.setImage(new Image("/Resources/reviveStrip.jpg", true));
            angelWings = new ImageView();
            angelWings.setFitWidth(200);
            angelWings.setFitHeight(150);
            angelWings.setPreserveRatio(true);
            angelWings.setOpacity(0);
            angelWings.setImage(new Image("/Resources/angelWings.png", true));
            reviveStrip.setLayoutY(GlobalVariables.lastJumpLocationY - 630);
            reviveStrip.setLayoutX(GlobalVariables.lastJumpLocationX - 30);
            angelWings.setLayoutX(GlobalVariables.lastJumpLocationX - 70);
            angelWings.setLayoutY(GlobalVariables.lastJumpLocationY - 90);
            GlobalVariables.gameAnchorPane.getChildren().add(reviveStrip);
            GlobalVariables.gameAnchorPane.getChildren().add(angelWings);
            Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
                Animations.fadeTransition(reviveStrip, 0, 1,1000, 1, false).play();
                Animations.translateTransition(player.getHero().getHero(), 0, GlobalVariables.lastJumpLocationY - 20, 500, 1, false).play();
                Animations.fadeTransition(angelWings, 0, 1,1000, 1, false).play();
            }));

            timeline2.setOnFinished(event -> {
                player.setHero(new mainHero(GlobalVariables.lastJumpLocationX, GlobalVariables.lastJumpLocationY - 800));
                player.getHero().setUnlockedWeapons(unlockedWeapons);
                if (currentWeapon != null) {
                    if (currentWeapon.getWeaponType() == 0) {
                        player.getHero().setCurrentWeapon(new Shuriken(player.getHero().getHero().getLayoutX() - 10, player.getHero().getHero().getLayoutY(), currentWeapon.getLevel()));
                        player.getHero().getCurrentWeapon().setWeaponType(0);
                    } else {
                        player.getHero().setCurrentWeapon(new Sword(player.getHero().getHero().getLayoutX() - 45, player.getHero().getHero().getLayoutY() + 22));
                        player.getHero().getCurrentWeapon().setWeaponType(1);
                    }
                }
                gameStarted = true;
                resetFlags();  // Reset the flags of gameObjects
                GlobalVariables.gameData = new gameData(game.getGameMode());
                gameController gameController = loader.getController();
                gameController.setupScene(game);
                stage.setScene(GlobalVariables.scene);
                stage.show();
                //GlobalVariables.timeline.play();
                if (GlobalVariables.sound) {
                    GlobalVariables.playerReviveSound.stop();
                    GlobalVariables.playerReviveSound.play();
                }
                Timeline timeline3 = new Timeline(new KeyFrame(Duration.millis(1000), event1 -> {
                    if (GlobalVariables.sound) {
                        GlobalVariables.playerReviveThudMusic.stop();
                        GlobalVariables.playerReviveThudMusic.play();
                    }
                    Animations.fadeTransition(reviveStrip, 1, 0,1000, 1, false).play();
                    Animations.fadeTransition(angelWings, 1, 0,1000, 1, false).play();
                    GlobalVariables.gameAnchorPane.getChildren().remove(reviveStrip);
                    GlobalVariables.gameAnchorPane.getChildren().remove(angelWings);
                }));
                timeline3.play();
            });
            timeline2.play();
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
        // Reopen game stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGame.fxml"));
        GlobalVariables.root = loader.load();
        stage = (Stage) (restartButton.getScene().getWindow());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Resources/icon.png"))));
        GlobalVariables.scene = new Scene(GlobalVariables.root);
        // Reset player properties
        resetFlags();  // Reset the flags of gameObjects
        player.updateCoins();
        GlobalVariables.gameData = new gameData(game.getGameMode());
        game.getPlayer().setScore(0);
        game.getPlayer().setHero(new mainHero(50, 290));
        game.getPlayer().setRevived(false);
        gameController gameController = loader.getController();
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
        GlobalVariables.game.getPlayer().updateCoins();

        // Close current Pause Menu stage
        stage = (Stage) (exitButton.getScene().getWindow());
        stage.close();

        // Open Main Menu stage
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainMenu.fxml")));
        stage = GlobalVariables.mainMenuStage;
        stage.setScene(new Scene(root));
        stage.show();
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
        player.getHero().setKilled(false);
        GlobalVariables.gameObjects.add(0, player.getHero());
        gameStarted = true;
        GlobalVariables.scene.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                if (mouseEvent.getPickResult().getIntersectedNode().getId() == null || !mouseEvent.getPickResult().getIntersectedNode().getId().equals("settingsButton") && !player.getHero().isStopMotion()) {
                    if (GlobalVariables.sound) {
                        GlobalVariables.playerLeapSound.stop();
                        GlobalVariables.playerLeapSound.play();
                    }
                    player.getHero().setSpeedX(Game.mainHero.getLeapSlice());
                    player.getHero().setLeaped(true);
                    if (player.getHero().getCurrentWeapon() instanceof Shuriken) {
                        Shuriken shuriken = new Shuriken(player.getHero().getHero().getLayoutX(), player.getHero().getHero().getLayoutY(), player.getHero().getCurrentWeapon().getLevel());
                        shuriken.setSpeedX(Shuriken.getThrowSlice());
                        shuriken.setThrown(true);
                        shuriken.addToScreen(gameAnchorPane);
                        if (GlobalVariables.sound) {
                            GlobalVariables.shurikenThrowSound.stop();
                            GlobalVariables.shurikenThrowSound.play();
                        }
                        try {
                            gameObjects.add(shuriken);  // Adding shuriken to gameObjects when hero clicks
                        }
                        catch (ConcurrentModificationException e) {
                            System.out.println("Shuriken added");
                        }
                        player.getHero().addShuriken();
                    } else if (player.getHero().getCurrentWeapon() instanceof Sword) {
                        ((Sword) player.getHero().getCurrentWeapon()).setUsed(true);  // Play sword animation
                        ((Sword) player.getHero().getCurrentWeapon()).useSword();
                    }
                    // Reset all flags here if any
                }
            }
        });
        GlobalVariables.scene.setOnKeyPressed(keyEvent -> {
            if (player.getHero().getCurrentWeapon() != null) {
                if (keyEvent.getCode() == KeyCode.DIGIT1) {
                    for (GameObject unlockedWeapon: player.getHero().getUnlockedWeapons()) {
                        if (((Weapon) unlockedWeapon).getWeaponType() == 0) {
                            if (player.getHero().getCurrentWeapon().getWeaponType() == 1) {
                                gameObjects.remove(player.getHero().getCurrentWeapon());
                                player.getHero().setCurrentWeapon(new Shuriken(player.getHero().getHero().getLayoutX() - 10, player.getHero().getHero().getLayoutY(), player.getHero().getCurrentWeapon().getLevel()));
                                ((Shuriken) player.getHero().getCurrentWeapon()).addToScreen(GlobalVariables.gameAnchorPane);
                                player.getHero().getCurrentWeapon().setWeaponType(0);
                            }
                        }
                    }
                } else if (keyEvent.getCode() == KeyCode.DIGIT2) {
                    for (GameObject unlockedWeapon : player.getHero().getUnlockedWeapons()) {
                        if (((Weapon) unlockedWeapon).getWeaponType() == 1) {
                            if (player.getHero().getCurrentWeapon().getWeaponType() == 0) {
                                player.getHero().setCurrentWeapon(new Sword(player.getHero().getHero().getLayoutX() - 45, player.getHero().getHero().getLayoutY() + 22));
                                player.getHero().getCurrentWeapon().setWeaponType(1);
                                ((Sword) player.getHero().getCurrentWeapon()).addToScreen(GlobalVariables.gameAnchorPane);
                                gameObjects.add(player.getHero().getCurrentWeapon());
                            }
                        }
                    }
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
        GlobalVariables.gameObjects.clear();  // Clear the local gameObjects arrayList
        gameObjects.clear();  // Clear the local gameObjects linkedList
    }

    // Add objects to the screen as the player moves
    public void generateObjects() {
        //System.out.println("Global size: " + GlobalVariables.gameObjects.size());

        for (int i = 0; i < GlobalVariables.gameObjects.size(); i++) {
            if (GlobalVariables.gameObjects.get(i).getP().getX() - player.getHero().getHero().getLayoutX() <= 700 && !GlobalVariables.gameObjects.get(i).isAdded()) {
                //System.out.println("added: " + GlobalVariables.gameObjects.get(i));
                try {
                    gameObjects.add(GlobalVariables.gameObjects.get(i));
                }
                catch (ConcurrentModificationException e) {
                    System.out.println("Object added");
                }
                GlobalVariables.gameObjects.get(i).setAdded(true);
                if (GlobalVariables.gameObjects.get(i) instanceof mainHero) {
                    ((mainHero) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                    if (player.isRevived()) {
                        if (player.getHero().getCurrentWeapon() != null) {
                            if (player.getHero().getCurrentWeapon().getWeaponType() == 0) {
                                ((Shuriken) player.getHero().getCurrentWeapon()).addToScreen(GlobalVariables.gameAnchorPane);
                            } else {
                                ((Sword) player.getHero().getCurrentWeapon()).addToScreen(GlobalVariables.gameAnchorPane);
                                gameObjects.add(player.getHero().getCurrentWeapon());
                            }
                        }
                    }
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
                    numberOfCoins += 1;
                    ((Coin) GlobalVariables.gameObjects.get(i)).addToScreen(gameAnchorPane);
                }
            }
        }
    }

    // Remove objects from the screen and local arraylist for optimized rendering
    public void destroyObjects() {
        try {
            for (Iterator<GameObject> iterator = gameObjects.iterator(); iterator.hasNext(); ) {
                GameObject outerObject = iterator.next();
                if (outerObject instanceof smallPlatform) {
                    if (player.getHero().getHero().getLayoutX() - ((smallPlatform) outerObject).getsPlatform().getLayoutX() >= 600) {
                        ((smallPlatform) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof mediumPlatform) {
                    if (player.getHero().getHero().getLayoutX() - ((mediumPlatform) outerObject).getmPlatform().getLayoutX() >= 600) {
                        ((mediumPlatform) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof bigPlatform) {
                    if (player.getHero().getHero().getLayoutX() - ((bigPlatform) outerObject).getbPlatform().getLayoutX() >= 800) {
                        ((bigPlatform) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof greenOrc) {
                    if (player.getHero().getHero().getLayoutX() - ((greenOrc) outerObject).getGreenOrc().getLayoutX() >= 600) {
                        ((greenOrc) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof redOrc) {
                    if (player.getHero().getHero().getLayoutX() - ((redOrc) outerObject).getRedOrc().getLayoutX() >= 600) {
                        ((redOrc) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof bossOrc) {
                    if (player.getHero().getHero().getLayoutX() - ((bossOrc) outerObject).getBossOrc().getLayoutX() >= 600) {
                        ((bossOrc) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof coinChest) {
                    if (player.getHero().getHero().getLayoutX() - ((coinChest) outerObject).getCoinChestImageView().getLayoutX() >= 600) {
                        ((coinChest) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof weaponChest) {
                    if (player.getHero().getHero().getLayoutX() - ((weaponChest) outerObject).getWeaponChestImageView().getLayoutX() >= 600) {
                        ((weaponChest) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                } else if (outerObject instanceof TNT) {
                    if (player.getHero().getHero().getLayoutX() - ((TNT) outerObject).getTntImage().getLayoutX() >= 600) {
                        ((TNT) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                }
                else if (outerObject instanceof Coin) {
                    if (player.getHero().getHero().getLayoutX() - ((Coin) outerObject).getCoinImage().getLayoutX() >= 600) {
                        ((Coin) outerObject).removeFromScreen();
                        System.out.println("Removing: " + outerObject);
                        numberOfCoins -= 1;
                        iterator.remove();
                    }
                }
                else if (outerObject instanceof Shuriken) {
                    if (player.getHero().getHero().getLayoutX() - ((Shuriken) outerObject).getShuriken().getLayoutX() >= 600) {
                        GlobalVariables.gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShuriken());
                        System.out.println("Removing: " + outerObject);
                        iterator.remove();
                    }
                }
            }
        }
        catch (ConcurrentModificationException e) {
            System.out.println("Object destroyed");
        }
    }

    public void gameOver() {  // Final cinematic scene after the boss is killed
        double currentX = player.getHero().getHero().getLayoutX();
        GlobalVariables.mediaPlayer.stop();
        player.getHero().setStopMotion(true);
        Image blackBar = new Image("/Resources/blackBar.jpg", true);
        blackBar1 = new ImageView();
        blackBar2 = new ImageView();
        blackBar1.setFitHeight(113);
        blackBar1.setFitWidth(1003);
        blackBar1.setLayoutX(-1);
//        blackBar1.setLayoutY(0);
        blackBar1.setLayoutY(-113);
        blackBar1.setImage(blackBar);
        blackBar2.setFitHeight(113);
        blackBar2.setFitWidth(1003);
        blackBar2.setLayoutX(-1);
//        blackBar2.setLayoutY(563);
        blackBar2.setLayoutY(676);
        blackBar2.setImage(blackBar);
        staticAnchorPane.getChildren().addAll(blackBar1, blackBar2);
        System.out.println("Location before transition: " + player.getHero().getHero().getLayoutX() + " " + player.getHero().getHero().getLayoutY());
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            Animations.translateTransition(blackBar1, 0, 113, 500, 1, false).play();
            Animations.translateTransition(blackBar2, 0, -113, 500, 1, false).play();
        }));
        timeline1.setOnFinished(event -> {
            GlobalVariables.victoryMusicPlay.stop();
            GlobalVariables.victoryMusicPlay.play();
            player.getHero().getHero().setLayoutY(300);
            gameData.addPrincess(gameAnchorPane, currentX + 650);  // Add princess and animation
            Path path = new Path();
            MoveTo moveTo1 = new MoveTo();
            moveTo1.setX(100);
            QuadCurveTo quadCurveTo1 = new QuadCurveTo();
            quadCurveTo1.setControlX(75);
            quadCurveTo1.setControlY(-120);
            MoveTo moveTo2 = new MoveTo();
            moveTo2.setX(100);
            QuadCurveTo quadCurveTo2 = new QuadCurveTo();
            quadCurveTo2.setControlX(180);
            quadCurveTo2.setControlY(-110);
            quadCurveTo2.setX(200);
            MoveTo moveTo3 = new MoveTo();
            moveTo3.setX(200);
            QuadCurveTo quadCurveTo3 = new QuadCurveTo();
            quadCurveTo3.setControlX(280);
            quadCurveTo3.setControlY(-100);
            quadCurveTo3.setX(300);
            MoveTo moveTo4 = new MoveTo();
            moveTo4.setX(300);
            QuadCurveTo quadCurveTo4 = new QuadCurveTo();
            quadCurveTo4.setControlX(380);
            quadCurveTo4.setControlY(-80);
            quadCurveTo4.setX(400);
            MoveTo moveTo5 = new MoveTo();
            moveTo5.setX(400);
            QuadCurveTo quadCurveTo5 = new QuadCurveTo();
            quadCurveTo5.setControlX(450);
            quadCurveTo5.setControlY(-60);
            quadCurveTo5.setX(500);
            path.setLayoutX(15850);
            path.setLayoutY(55);
            path.getElements().addAll(moveTo1, quadCurveTo1, moveTo2, quadCurveTo2, moveTo3, quadCurveTo3, moveTo4, quadCurveTo4, moveTo5, quadCurveTo5);
            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(player.getHero().getHero());
            pathTransition.setPath(path);
            pathTransition.setCycleCount(1);
            pathTransition.setAutoReverse(false);
            pathTransition.setDuration(Duration.millis(10000));
            pathTransition.setOnFinished(event1 -> playerDeath(5));
            pathTransition.play();
            Animations.translateTransition(player.getHero().getHero(), 500, 0, 10000, 1, true).play();
        });
        timeline1.play();
    }

    public void playerDeath(int deathType) {  // 0 for fall death, 1 for normal death
        // Pause time
        GlobalVariables.timeline.pause();
        if (game.getPlayer().getScore() > GlobalVariables.highscore) {
            game.setHighScore(game.getPlayer().getScore());
        }
        unlockedWeapons = player.getHero().getUnlockedWeapons();
        currentWeapon = player.getHero().getCurrentWeapon();
        player.getHero().setKilled(true);
        player.updateCoins();
        GlobalVariables.gameObjects.remove(game.getPlayer().getHero());
        if (deathType == 5) {
            GlobalVariables.victorySound.play();
            endMessage.setImage(new Image("/Resources/youWin.png", true));
            gaussianBlur.setRadius(15);
            reviveButton.setVisible(false);
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
            gameStarted = false;
        }

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
        gameStarted = false;
    }

    public void run() throws ConcurrentModificationException {
        if (gameStarted) {
//            System.out.println("Size of local: " + gameObjects.size());
//            System.out.println("local: " + gameObjects);
            displayFPS();
            displayScore();
            displayCoins();
            displayPlayerWeapons();
            generateObjects();  // Checks each time whether the next item in queue is within a specified distance of the player (camera)
            destroyObjects();
            GlobalVariables.timeline.setRate((428.746463 * Math.pow(0.868535805, gameObjects.size() - numberOfCoins/2) / GlobalVariables.difficulty));  // Subtract number of coins here
            camera.update(player.getHero(), gameAnchorPane, bgAnchorPane);  // Follow the player
            try {
                for (Iterator<GameObject> iterator = gameObjects.iterator(); iterator.hasNext(); ) {
                    GameObject outerObject = iterator.next();
                    for (Iterator<GameObject> iterator2 = gameObjects.iterator(); iterator2.hasNext(); ) {
                        GameObject innerObject = iterator2.next();
                        if (outerObject instanceof mainHero) {
                            // Player Movement   -- Comment the if statement below for Admin mode :) --
                            if (!((mainHero) outerObject).isLeaped()) {  // Stop Y axis motion when player leaps
                                if (((mainHero) outerObject).getCurrentJumpHeight() > ((mainHero) outerObject).getJumpHeight()) {
//                                    ((mainHero) outerObject).setWidthX(-0.01);
//                                    ((mainHero) outerObject).deformX();
//                                    ((mainHero) outerObject).setWidthY(0.08);
//                                    ((mainHero) outerObject).deformY();
                                    ((mainHero) outerObject).setSetY(((mainHero) outerObject).getSpeedY() - mainHero.getAccelerationY());
                                    ((mainHero) outerObject).setSpeedY(((mainHero) outerObject).getSetY());
                                    ((mainHero) outerObject).jump();
                                    ((mainHero) outerObject).setCurrentJumpHeight(((mainHero) outerObject).getCurrentJumpHeight() + ((mainHero) outerObject).getSetY());
                                } else {
//                                    ((mainHero) outerObject).setWidthX(0.01);
//                                    ((mainHero) outerObject).setWidthY(-0.08);
//                                    ((mainHero) outerObject).deformY();
//                                    ((mainHero) outerObject).deformX();
                                    ((mainHero) outerObject).setSetY(((mainHero) outerObject).getSpeedY() + mainHero.getAccelerationY());
                                    ((mainHero) outerObject).setSpeedY(((mainHero) outerObject).getSetY());
                                    ((mainHero) outerObject).jump();
                                }
                            }
                            if (((mainHero) outerObject).isLeaped()) {
                                ((mainHero) outerObject).setSpeedY(0);
                                ((mainHero) outerObject).getHero().setFitWidth(mainHero.getWIDTH());
                                ((mainHero) outerObject).getHero().setFitHeight(mainHero.getHEIGHT());
                                if (((mainHero) outerObject).getCurrentLeapLength() < ((mainHero) outerObject).getLeapLength()) {
                                    ((mainHero) outerObject).setSetX(((mainHero) outerObject).getSpeedX() + mainHero.getAccelerationX());
                                    ((mainHero) outerObject).setSpeedX(((mainHero) outerObject).getSetX());
                                    ((mainHero) outerObject).leap();
                                    ((mainHero) outerObject).setCurrentLeapLength(((mainHero) outerObject).getCurrentLeapLength() + ((mainHero) outerObject).getSetX());
                                } else {
                                    // Called at the end of every leap
                                    // Uncomment below code at the end (don't for now)
                                    //((mainHero) outerObject).setSpeedY(((mainHero) outerObject).getSetY());  // At the end of the leap, set Y speed back to before and X to 0.
                                    ((mainHero) outerObject).setSpeedX(0);
                                    ((mainHero) outerObject).setCurrentLeapLength(0);
                                    ((mainHero) outerObject).setLeaped(false);
                                }
                            }
                            if ((((mainHero) outerObject).getHero().getLayoutX() - 50 >= ((player.getScore() + 1) * 140.25)) && ((mainHero) outerObject).getHero().getLayoutY() < 600) { // To prevent user from gaining score from leaping during fall
                                player.increaseScore();
                            }
                            if (innerObject instanceof smallPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    if (GlobalVariables.sound) {
                                        GlobalVariables.playerJumpSound.stop();
                                        GlobalVariables.playerJumpSound.play();
                                    }
                                    GlobalVariables.lastJumpLocationX = ((mainHero) outerObject).getHero().getLayoutX();
                                    GlobalVariables.lastJumpLocationY = ((mainHero) outerObject).getHero().getLayoutY();
                                    ((mainHero) outerObject).setCurrentJumpHeight(0);
                                    ((mainHero) outerObject).setSpeedY(-mainHero.getJumpSlice());
                                }
                            } else if (innerObject instanceof mediumPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    if (GlobalVariables.sound) {
                                        GlobalVariables.playerJumpSound.stop();
                                        GlobalVariables.playerJumpSound.play();
                                    }
                                    GlobalVariables.lastJumpLocationX = ((mainHero) outerObject).getHero().getLayoutX();
                                    GlobalVariables.lastJumpLocationY = ((mainHero) outerObject).getHero().getLayoutY();
                                    ((mainHero) outerObject).setCurrentJumpHeight(0);
                                    ((mainHero) outerObject).setSpeedY(-mainHero.getJumpSlice());
                                }
                            } else if (innerObject instanceof bigPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    if (GlobalVariables.sound) {
                                        GlobalVariables.playerJumpSound.stop();
                                        GlobalVariables.playerJumpSound.play();
                                    }
                                    GlobalVariables.lastJumpLocationX = ((mainHero) outerObject).getHero().getLayoutX();
                                    GlobalVariables.lastJumpLocationY = ((mainHero) outerObject).getHero().getLayoutY();
                                    ((mainHero) outerObject).setCurrentJumpHeight(0);
                                    ((mainHero) outerObject).setSpeedY(-mainHero.getJumpSlice());
                                }
                            } else if (innerObject instanceof redOrc) {
                                // Player collision with Red Orc
                                if (outerObject.collision_detected(innerObject)) {
                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((redOrc) innerObject).isPushed()) {
                                            ((redOrc) innerObject).setSpeedX(((2 * mainHero.getWeight() * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + redOrc.getWeight())) + (((redOrc.getWeight() - mainHero.getWeight()) * ((redOrc) innerObject).getSpeedX()) / (mainHero.getWeight() + redOrc.getWeight())));
                                            ((redOrc) innerObject).setPushed(true);
                                            ((mainHero) outerObject).setSpeedX((((mainHero.getWeight() - redOrc.getWeight()) * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + redOrc.getWeight())) + ((2 * redOrc.getWeight() * ((redOrc) innerObject).getSpeedX()) / (mainHero.getWeight() + redOrc.getWeight())));
                                            ((mainHero) outerObject).leap();
                                            ((mainHero) outerObject).setSpeedX(0);
                                            ((mainHero) outerObject).setCurrentLeapLength(0);
                                            ((mainHero) outerObject).setLeaped(false);
                                        }
                                    }
                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((mainHero) outerObject).getSpeedX() > 0) && !(((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (GlobalVariables.sound) {
                                            GlobalVariables.playerJumpSound.stop();
                                            GlobalVariables.playerJumpSound.play();
                                        }
                                        ((mainHero) outerObject).setCurrentJumpHeight(0);
                                        ((mainHero) outerObject).setSpeedY(-mainHero.getJumpSlice());
                                    }
                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getBottomRectangle().getBoundsInParent()) && !(((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getTopRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((redOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (((mainHero) outerObject).getCurrentWeapon() != null && ((mainHero) outerObject).getCurrentWeapon() instanceof Sword) {
                                            if (!((Sword) ((mainHero) outerObject).getCurrentWeapon()).isUsed()) {
                                                if (!((redOrc) innerObject).isKilled()) {
                                                    gameStarted = false;
                                                    playerDeath(1);
                                                }
                                            }
                                        }
                                        else {
                                            if (!((redOrc) innerObject).isKilled()) {
                                                gameStarted = false;
                                                playerDeath(1);
                                            }
                                        }
                                    }
                                }
                            } else if (innerObject instanceof greenOrc) {
                                // Player collision with Green Orc
                                if (outerObject.collision_detected(innerObject)) {
                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((greenOrc) innerObject).isPushed()) {
                                            ((greenOrc) innerObject).setSpeedX(((2 * mainHero.getWeight() * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + greenOrc.getWeight())) + (((greenOrc.getWeight() - mainHero.getWeight()) * ((greenOrc) innerObject).getSpeedX()) / (mainHero.getWeight() + greenOrc.getWeight())));
                                            ((greenOrc) innerObject).setPushed(true);
                                            ((mainHero) outerObject).setSpeedX((((mainHero.getWeight() - greenOrc.getWeight()) * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + greenOrc.getWeight())) + ((2 * greenOrc.getWeight() * ((greenOrc) innerObject).getSpeedX()) / (mainHero.getWeight() + greenOrc.getWeight())));
                                            ((mainHero) outerObject).leap();
                                            ((mainHero) outerObject).setSpeedX(0);
                                            ((mainHero) outerObject).setCurrentLeapLength(0);
                                            ((mainHero) outerObject).setLeaped(false);
                                        }
                                    }
                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((mainHero) outerObject).getSpeedX() > 0) && !(((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (GlobalVariables.sound) {
                                            GlobalVariables.playerJumpSound.stop();
                                            GlobalVariables.playerJumpSound.play();
                                        }
                                        ((mainHero) outerObject).setCurrentJumpHeight(0);
                                        ((mainHero) outerObject).setSpeedY(-mainHero.getJumpSlice());
                                    }

                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getBottomRectangle().getBoundsInParent()) && !(((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getTopRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((greenOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (((mainHero) outerObject).getCurrentWeapon() instanceof Sword) {
                                            if (!((Sword) ((mainHero) outerObject).getCurrentWeapon()).isUsed()) {
                                                if (!((greenOrc) innerObject).isKilled()) {
                                                    gameStarted = false;
                                                    playerDeath(1);
                                                }
                                            }
                                        }
                                        else {
                                            if (!((greenOrc) innerObject).isKilled()) {
                                                gameStarted = false;
                                                playerDeath(1);
                                            }
                                        }
                                    }
                                }
                            } else if (innerObject instanceof bossOrc) {
                                // Player collision with Boss Orc
                                if (outerObject.collision_detected(innerObject)) {
                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((bossOrc) innerObject).isPushed()) {
                                            ((bossOrc) innerObject).setSpeedX(((2 * mainHero.getWeight() * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + bossOrc.getWeight())) + (((bossOrc.getWeight() - mainHero.getWeight()) * ((bossOrc) innerObject).getSpeedX()) / (mainHero.getWeight() + bossOrc.getWeight())));
                                            ((bossOrc) innerObject).setPushed(true);
                                            ((mainHero) outerObject).setSpeedX((((mainHero.getWeight() - bossOrc.getWeight()) * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + bossOrc.getWeight())) + ((2 * bossOrc.getWeight() * ((bossOrc) innerObject).getSpeedX()) / (mainHero.getWeight() + bossOrc.getWeight())) - 0.01);
                                            // Boss orc comes forward and attacks if speed is negative
                                            ((mainHero) outerObject).leap();
                                            ((mainHero) outerObject).setSpeedX(0);
                                            ((mainHero) outerObject).setCurrentLeapLength(0);
                                            ((mainHero) outerObject).setLeaped(false);
                                        }
                                    }
                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getTopRectangle().getBoundsInParent()) && (((mainHero) outerObject).getSpeedX() <= 0) && !(((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getRightRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getBottomRectangle().getBoundsInParent()))) {
                                        if (GlobalVariables.sound) {
                                            GlobalVariables.playerJumpSound.stop();
                                            GlobalVariables.playerJumpSound.play();
                                        }
                                        ((mainHero) outerObject).setCurrentJumpHeight(0);
                                        ((mainHero) outerObject).setSpeedY(-mainHero.getJumpSlice());
                                    }

                                    if (((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getBottomRectangle().getBoundsInParent()) && !(((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getTopRectangle().getBoundsInParent()) || ((mainHero) outerObject).getHeroPolygon().getBoundsInParent().intersects(((bossOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (((mainHero) outerObject).getCurrentWeapon() instanceof Sword) {
                                            if (!((Sword) ((mainHero) outerObject).getCurrentWeapon()).isUsed()) {
                                                if (!((bossOrc) innerObject).isKilled()) {
                                                    gameStarted = false;
                                                    playerDeath(1);
                                                }
                                            }
                                        }
                                        else {
                                            if (!((bossOrc) innerObject).isKilled()) {
                                                gameStarted = false;
                                                playerDeath(1);
                                            }
                                        }
                                    }
                                }
                            } else if (innerObject instanceof weaponChest) {
                                if (outerObject.collision_detected(innerObject)) {
                                    if (!((weaponChest) innerObject).isActivated()) {
                                        ((weaponChest) innerObject).playChestAnimation(player);
                                        ((weaponChest) innerObject).setActivated(true);
                                        int returned = ((mainHero) outerObject).addWeapon(((weaponChest) innerObject).getWeaponType());
                                        if (returned == 1) {
                                            gameObjects.removeIf(gameObject -> gameObject instanceof Sword);
                                            try {
                                                if (((weaponChest) innerObject).getWeaponType() == 1)
                                                    gameObjects.add(((mainHero) outerObject).getCurrentWeapon());  // Adding sword to gameObjects
                                            } catch (ConcurrentModificationException e) {
                                                System.out.println("Sword added");
                                            }
                                        }

                                        else if (returned == 3) {
                                            try {
                                                if (((weaponChest) innerObject).getWeaponType() == 1)
                                                    gameObjects.add(((mainHero) outerObject).getCurrentWeapon());  // Adding sword to gameObjects
                                            }
                                            catch (ConcurrentModificationException e) {
                                                System.out.println("Sword added");
                                            }
                                        }
                                    }
                                }
                            } else if (innerObject instanceof coinChest) {
                                if (outerObject.collision_detected(innerObject)) {
                                    if (!((coinChest) innerObject).isActivated()) {
                                        ((coinChest) innerObject).playChestAnimation(player);
                                        ((coinChest) innerObject).setActivated(true);
                                    }
                                }
                            } else if (innerObject instanceof TNT) {
                                // player collision with TNT
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (!((TNT) innerObject).isPushed()) {
                                        ((TNT) innerObject).setSpeedX(((2 * mainHero.getWeight() * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + TNT.getWeight())) + (((TNT.getWeight() - mainHero.getWeight()) * ((TNT) innerObject).getSpeedX()) / (mainHero.getWeight() + TNT.getWeight())));
                                        ((TNT) innerObject).setPushed(true);
                                        ((mainHero) outerObject).setSpeedX((((mainHero.getWeight() - TNT.getWeight()) * ((mainHero) outerObject).getSpeedX()) / (mainHero.getWeight() + TNT.getWeight())) + ((2 * TNT.getWeight() * ((TNT) innerObject).getSpeedX()) / (mainHero.getWeight() + TNT.getWeight())));
                                        ((mainHero) outerObject).leap();
                                        ((mainHero) outerObject).setSpeedX(0);
                                        ((mainHero) outerObject).setCurrentLeapLength(0);
                                        ((mainHero) outerObject).setLeaped(false);
                                    }
                                    if (!((TNT) innerObject).isActivated()) {
                                        ((TNT) innerObject).setActivated(true);
                                        ((TNT) innerObject).playTNTAnimation();  // Resets explosion flags inside
                                    } else if (((TNT) innerObject).isExplosionActivated()) {
                                        gameStarted = false;
                                        playerDeath(1);
                                    }
                                }
                            } else if (innerObject instanceof Coin) {
                                if (outerObject.collision_detected(innerObject)) {
                                    if (!((Coin) innerObject).isCollected()) {
                                        player.increaseCoins(1);
                                        ((Coin) innerObject).playCoinAnimation();
                                        numberOfCoins -= 1;
                                        iterator2.remove();  // Removing coin from list for ever-so-little optimization
                                    }
                                }
                            }

                            if (((mainHero) outerObject).getHeroPolygon().getLayoutY() > 780) {  // player death fall detection
                                gameStarted = false;
                                playerDeath(0);
                            }
                        } else if (outerObject instanceof smallPlatform) {
                            if (((smallPlatform) outerObject).getCurrentJumpHeight() < ((smallPlatform) outerObject).getJumpHeight()) {
                                ((smallPlatform) outerObject).setSetY(((smallPlatform) outerObject).getSpeedY() + smallPlatform.getAccelerationY());
                                ((smallPlatform) outerObject).setSpeedY(((smallPlatform) outerObject).getSetY());
                                ((smallPlatform) outerObject).jump();
                                ((smallPlatform) outerObject).setCurrentJumpHeight(((smallPlatform) outerObject).getCurrentJumpHeight() + ((smallPlatform) outerObject).getSetY());
                            } else {
                                ((smallPlatform) outerObject).setSetY(((smallPlatform) outerObject).getSpeedY() - smallPlatform.getAccelerationY());
                                ((smallPlatform) outerObject).setSpeedY(((smallPlatform) outerObject).getSetY());
                                ((smallPlatform) outerObject).jump();
                                ((smallPlatform) outerObject).setCurrentJumpHeight(((smallPlatform) outerObject).getCurrentJumpHeight() + ((smallPlatform) outerObject).getSetY());
                            }

                        } else if (outerObject instanceof mediumPlatform) {
                            if (((mediumPlatform) outerObject).getCurrentJumpHeight() < ((mediumPlatform) outerObject).getJumpHeight()) {
                                ((mediumPlatform) outerObject).setSetY(((mediumPlatform) outerObject).getSpeedY() + mediumPlatform.getAccelerationY());
                                ((mediumPlatform) outerObject).setSpeedY(((mediumPlatform) outerObject).getSetY());
                                ((mediumPlatform) outerObject).jump();
                                ((mediumPlatform) outerObject).setCurrentJumpHeight(((mediumPlatform) outerObject).getCurrentJumpHeight() + ((mediumPlatform) outerObject).getSetY());
                            } else {
                                ((mediumPlatform) outerObject).setSetY(((mediumPlatform) outerObject).getSpeedY() - mediumPlatform.getAccelerationY());
                                ((mediumPlatform) outerObject).setSpeedY(((mediumPlatform) outerObject).getSetY());
                                ((mediumPlatform) outerObject).jump();
                                ((mediumPlatform) outerObject).setCurrentJumpHeight(((mediumPlatform) outerObject).getCurrentJumpHeight() + ((mediumPlatform) outerObject).getSetY());
                            }

                        } else if (outerObject instanceof bigPlatform) {
                            if (((bigPlatform) outerObject).getCurrentJumpHeight() < ((bigPlatform) outerObject).getJumpHeight()) {
                                ((bigPlatform) outerObject).setSetY(((bigPlatform) outerObject).getSpeedY() + bigPlatform.getAccelerationY());
                                ((bigPlatform) outerObject).setSpeedY(((bigPlatform) outerObject).getSetY());
                                ((bigPlatform) outerObject).jump();
                                ((bigPlatform) outerObject).setCurrentJumpHeight(((bigPlatform) outerObject).getCurrentJumpHeight() + ((bigPlatform) outerObject).getSetY());
                            } else {
                                ((bigPlatform) outerObject).setSetY(((bigPlatform) outerObject).getSpeedY() - bigPlatform.getAccelerationY());
                                ((bigPlatform) outerObject).setSpeedY(((bigPlatform) outerObject).getSetY());
                                ((bigPlatform) outerObject).jump();
                                ((bigPlatform) outerObject).setCurrentJumpHeight(((bigPlatform) outerObject).getCurrentJumpHeight() + ((bigPlatform) outerObject).getSetY());
                            }

                        } else if (outerObject instanceof greenOrc) {
                            // Green Orc movements
                            if (((greenOrc) outerObject).getCurrentJumpHeight() > ((greenOrc) outerObject).getJumpHeight()) {
                                ((greenOrc) outerObject).setSetY(((greenOrc) outerObject).getSpeedY() - greenOrc.getAccelerationY());
                                ((greenOrc) outerObject).setSpeedY(((greenOrc) outerObject).getSetY());
                                ((greenOrc) outerObject).jump();
                                ((greenOrc) outerObject).setCurrentJumpHeight(((greenOrc) outerObject).getCurrentJumpHeight() + ((greenOrc) outerObject).getSetY());

                            } else {
                                ((greenOrc) outerObject).setSetY(((greenOrc) outerObject).getSpeedY() + greenOrc.getAccelerationY());
                                ((greenOrc) outerObject).setSpeedY(((greenOrc) outerObject).getSetY());
                                ((greenOrc) outerObject).jump();
                            }
                            if (((greenOrc) outerObject).isPushed()) {
                                if (((greenOrc) outerObject).getSpeedX() <= 0 || ((greenOrc) outerObject).getSpeedX() + greenOrc.getAccelerationX() <= 0) {
                                    ((greenOrc) outerObject).setPushed(false);
                                } else {
                                    ((greenOrc) outerObject).setSpeedX(((greenOrc) outerObject).getSpeedX() + greenOrc.getAccelerationX());
                                    ((greenOrc) outerObject).push();
                                }
                            }
                            if (innerObject instanceof smallPlatform) {
                                if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((smallPlatform) innerObject).getsPlatformPolygon().getBoundsInParent())) {
                                    if (!((greenOrc) outerObject).isKilled()) {
                                        ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                        ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof mediumPlatform) {
                                if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((mediumPlatform) innerObject).getmPlatformPolygon().getBoundsInParent())) {
                                    if (!((greenOrc) outerObject).isKilled()) {
                                        ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                        ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof bigPlatform) {
                                if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bigPlatform) innerObject).getbPlatformPolygon().getBoundsInParent())) {
                                    if (!((greenOrc) outerObject).isKilled()) {
                                        ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                        ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof greenOrc && !innerObject.equals(outerObject)) {  // Check for green orc other than self
                                // Green orc collision with green orc
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (((greenOrc) outerObject).getRightRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((greenOrc) innerObject).isPushed()) {
                                            ((greenOrc) innerObject).setSpeedX(((2 * greenOrc.getWeight() * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + greenOrc.getWeight())) + (((0.0) * ((greenOrc) innerObject).getSpeedX()) / (greenOrc.getWeight() + greenOrc.getWeight())));
                                            ((greenOrc) innerObject).setPushed(true);
                                            ((greenOrc) outerObject).setSpeedX((((0.0) * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + greenOrc.getWeight())) + ((2 * greenOrc.getWeight() * ((greenOrc) innerObject).getSpeedX()) / (greenOrc.getWeight() + greenOrc.getWeight())) - 0.01);
                                            ((greenOrc) outerObject).push();
                                            ((greenOrc) outerObject).setSpeedX(0);
                                        }
                                    }
                                    if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((greenOrc) outerObject).getSpeedX() > 0) && !(((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (!((greenOrc) outerObject).isKilled()) {
                                            ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                            ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof redOrc) {
                                // Green orc collision with red orc
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (((greenOrc) outerObject).getRightRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((redOrc) innerObject).isPushed()) {
                                            ((redOrc) innerObject).setSpeedX(((2 * greenOrc.getWeight() * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + redOrc.getWeight())) + (((redOrc.getWeight() - greenOrc.getWeight()) * ((redOrc) innerObject).getSpeedX()) / (greenOrc.getWeight() + redOrc.getWeight())));
                                            ((redOrc) innerObject).setPushed(true);
                                            ((greenOrc) outerObject).setSpeedX((((greenOrc.getWeight() - redOrc.getWeight()) * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + redOrc.getWeight())) + ((2 * redOrc.getWeight() * ((redOrc) innerObject).getSpeedX()) / (greenOrc.getWeight() + redOrc.getWeight())) - 0.01);
                                            // Boss orc comes forward and attacks if speed is negative
                                            ((greenOrc) outerObject).push();
                                            ((greenOrc) outerObject).setSpeedX(0);
                                        }
                                    }
                                    if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((greenOrc) outerObject).getSpeedX() > 0) && !(((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (!((greenOrc) outerObject).isKilled()) {
                                            ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                            ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof bossOrc) {
                                // Green orc collision with boss orc
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (((greenOrc) outerObject).getRightRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((bossOrc) innerObject).isPushed()) {
                                            ((bossOrc) innerObject).setSpeedX(((2 * greenOrc.getWeight() * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + bossOrc.getWeight())) + (((bossOrc.getWeight() - greenOrc.getWeight()) * ((bossOrc) innerObject).getSpeedX()) / (greenOrc.getWeight() + bossOrc.getWeight())));
                                            ((bossOrc) innerObject).setPushed(true);
                                            ((greenOrc) outerObject).setSpeedX((((greenOrc.getWeight() - bossOrc.getWeight()) * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + bossOrc.getWeight())) + ((2 * bossOrc.getWeight() * ((bossOrc) innerObject).getSpeedX()) / (greenOrc.getWeight() + bossOrc.getWeight())) - 0.01);
                                            // Boss orc comes forward and attacks if speed is negative
                                            ((greenOrc) outerObject).push();
                                            ((greenOrc) outerObject).setSpeedX(0);
                                        }
                                    }
                                    if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((greenOrc) outerObject).getSpeedX() > 0) && !(((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (!((greenOrc) outerObject).isKilled()) {
                                            ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                            ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof coinChest) {
                                // Green orc collision with coin chest
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (!((coinChest) innerObject).isPushed()) {
                                        ((coinChest) innerObject).setSpeedX(((2 * greenOrc.getWeight() * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + coinChest.getWeight())) + (((coinChest.getWeight() - greenOrc.getWeight()) * ((coinChest) innerObject).getSpeedX()) / (greenOrc.getWeight() + coinChest.getWeight())));
                                        ((coinChest) innerObject).setPushed(true);
                                        ((greenOrc) outerObject).setSpeedX((((greenOrc.getWeight() - coinChest.getWeight()) * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + coinChest.getWeight())) + ((2 * coinChest.getWeight() * ((coinChest) innerObject).getSpeedX()) / (greenOrc.getWeight() + coinChest.getWeight())));
                                        ((greenOrc) outerObject).push();
                                        ((greenOrc) outerObject).setSpeedX(0);
                                    }
                                    if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((coinChest) innerObject).getCoinChestPolygon().getBoundsInParent()) && !(((greenOrc) outerObject).getSpeedX() > 0)) {
                                        if (!((greenOrc) outerObject).isKilled()) {
                                            ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                            ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof weaponChest) {
                                // Green orc collision with weapon chest
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (!((weaponChest) innerObject).isPushed()) {
                                        ((weaponChest) innerObject).setSpeedX((2 * greenOrc.getWeight() * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + weaponChest.getWeight()));
                                        ((weaponChest) innerObject).setPushed(true);
                                        ((greenOrc) outerObject).setSpeedX(((greenOrc.getWeight() - weaponChest.getWeight()) * ((greenOrc) outerObject).getSpeedX()) / (greenOrc.getWeight() + weaponChest.getWeight()));
                                        ((greenOrc) outerObject).push();
                                        ((greenOrc) outerObject).setSpeedX(0);
                                    }
                                    if (((greenOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((weaponChest) innerObject).getWeaponChestPolygon().getBoundsInParent()) && !(((greenOrc) outerObject).getSpeedX() > 0)) {
                                        if (!((greenOrc) outerObject).isKilled()) {
                                            ((greenOrc) outerObject).setCurrentJumpHeight(0);
                                            ((greenOrc) outerObject).setSpeedY(-greenOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof TNT) {
                                if (outerObject.collision_detected(innerObject)) {
                                    if (!((TNT) innerObject).isActivated()) {
                                        ((TNT) innerObject).setActivated(true);
                                        ((TNT) innerObject).playTNTAnimation();
                                    } else if (((TNT) innerObject).isExplosionActivated()) {
                                        if (!((greenOrc) outerObject).isKilled()) {
                                            ((greenOrc) outerObject).setKilled(true);
                                            ((greenOrc) outerObject).playDeathAnimation(1, player);
                                        }
                                    }
                                }
                            }

                            if (((greenOrc) outerObject).getTopRectangle().getLayoutY() > 780) {  // Death fall detection
                                ((greenOrc) outerObject).setSpeedY(0);
                                if (!((greenOrc) outerObject).isKilled()) {
                                    ((greenOrc) outerObject).setKilled(true);
                                    ((greenOrc) outerObject).playDeathAnimation(0, player);
                                }
                            }

                        } else if (outerObject instanceof redOrc) {
                            // Red Orc movements
                            if (((redOrc) outerObject).getCurrentJumpHeight() > ((redOrc) outerObject).getJumpHeight()) {
                                ((redOrc) outerObject).setSetY(((redOrc) outerObject).getSpeedY() - redOrc.getAccelerationY());
                                ((redOrc) outerObject).setSpeedY(((redOrc) outerObject).getSetY());
                                ((redOrc) outerObject).jump();
                                ((redOrc) outerObject).setCurrentJumpHeight(((redOrc) outerObject).getCurrentJumpHeight() + ((redOrc) outerObject).getSetY());

                            } else {
                                ((redOrc) outerObject).setSetY(((redOrc) outerObject).getSpeedY() + redOrc.getAccelerationY());
                                ((redOrc) outerObject).setSpeedY(((redOrc) outerObject).getSetY());
                                ((redOrc) outerObject).jump();
                                //jumpHeight += setY;
                            }
                            if (((redOrc) outerObject).isPushed()) {
                                if (((redOrc) outerObject).getSpeedX() <= 0 || ((redOrc) outerObject).getSpeedX() + redOrc.getAccelerationX() <= 0) {
                                    ((redOrc) outerObject).setPushed(false);
                                } else {
                                    ((redOrc) outerObject).setSpeedX(((redOrc) outerObject).getSpeedX() + redOrc.getAccelerationX());
                                    ((redOrc) outerObject).push();
                                }
                            }
                            if (innerObject instanceof smallPlatform) {
                                if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((smallPlatform) innerObject).getsPlatformPolygon().getBoundsInParent())) {
                                    if (!((redOrc) outerObject).isKilled()) {
                                        ((redOrc) outerObject).setCurrentJumpHeight(0);
                                        ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof mediumPlatform) {
                                if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((mediumPlatform) innerObject).getmPlatformPolygon().getBoundsInParent())) {
                                    if (!((redOrc) outerObject).isKilled()) {
                                        ((redOrc) outerObject).setCurrentJumpHeight(0);
                                        ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof bigPlatform) {
                                if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bigPlatform) innerObject).getbPlatformPolygon().getBoundsInParent())) {
                                    if (!((redOrc) outerObject).isKilled()) {
                                        ((redOrc) outerObject).setCurrentJumpHeight(0);
                                        ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof greenOrc) {
                                // Red orc collision with green orc
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (((redOrc) outerObject).getRightRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((greenOrc) innerObject).isPushed()) {
                                            ((greenOrc) innerObject).setSpeedX(((2 * redOrc.getWeight() * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + greenOrc.getWeight())) + (((greenOrc.getWeight() - redOrc.getWeight()) * ((greenOrc) innerObject).getSpeedX()) / (redOrc.getWeight() + greenOrc.getWeight())));
                                            ((greenOrc) innerObject).setPushed(true);
                                            ((redOrc) outerObject).setSpeedX((((redOrc.getWeight() - greenOrc.getWeight()) * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + greenOrc.getWeight())) + ((2 * greenOrc.getWeight() * ((greenOrc) innerObject).getSpeedX()) / (redOrc.getWeight() + greenOrc.getWeight())));
                                            // Boss orc comes forward and attacks if speed is negative
                                            ((redOrc) outerObject).push();
                                            ((redOrc) outerObject).setSpeedX(0);
                                        }
                                    }
                                    if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((redOrc) outerObject).getSpeedX() > 0) && !(((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((greenOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (!((redOrc) outerObject).isKilled()) {
                                            ((redOrc) outerObject).setCurrentJumpHeight(0);
                                            ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof redOrc) {
                                // Red orc collision with red orc
                                if (outerObject.collision_detected(innerObject) && !innerObject.equals(outerObject)) { // Left collision for push
                                    if (((redOrc) outerObject).getRightRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((redOrc) innerObject).isPushed()) {
                                            ((redOrc) innerObject).setSpeedX(((2 * redOrc.getWeight() * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + redOrc.getWeight())) + (((0.0) * ((redOrc) innerObject).getSpeedX()) / (redOrc.getWeight() + redOrc.getWeight())));
                                            ((redOrc) innerObject).setPushed(true);
                                            ((redOrc) outerObject).setSpeedX((((0.0) * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + redOrc.getWeight())) + ((2 * redOrc.getWeight() * ((redOrc) innerObject).getSpeedX()) / (redOrc.getWeight() + redOrc.getWeight())));
                                            ((redOrc) outerObject).push();
                                            ((redOrc) outerObject).setSpeedX(0);
                                        }
                                    }
                                    if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((redOrc) outerObject).getSpeedX() > 0) && !(((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((redOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (!((redOrc) outerObject).isKilled()) {
                                            ((redOrc) outerObject).setCurrentJumpHeight(0);
                                            ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof bossOrc) {
                                // Red orc collision with boss orc
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (((redOrc) outerObject).getRightRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getLeftRectangle().getBoundsInParent())) {  // Left collision for push
                                        if (!((bossOrc) innerObject).isPushed()) {
                                            ((bossOrc) innerObject).setSpeedX(((2 * redOrc.getWeight() * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + bossOrc.getWeight())) + (((bossOrc.getWeight() - redOrc.getWeight()) * ((bossOrc) innerObject).getSpeedX()) / (redOrc.getWeight() + bossOrc.getWeight())));
                                            ((bossOrc) innerObject).setPushed(true);
                                            ((redOrc) outerObject).setSpeedX((((redOrc.getWeight() - bossOrc.getWeight()) * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + bossOrc.getWeight())) + ((2 * bossOrc.getWeight() * ((bossOrc) innerObject).getSpeedX()) / (redOrc.getWeight() + bossOrc.getWeight())));
                                            ((redOrc) outerObject).push();
                                            ((redOrc) outerObject).setSpeedX(0);
                                        }
                                    }
                                    if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getTopRectangle().getBoundsInParent()) && !(((redOrc) outerObject).getSpeedX() > 0) && !(((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getLeftRectangle().getBoundsInParent()) || ((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getBottomRectangle().getBoundsInParent()) || ((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bossOrc) innerObject).getRightRectangle().getBoundsInParent()))) {
                                        if (!((redOrc) outerObject).isKilled()) {
                                            ((redOrc) outerObject).setCurrentJumpHeight(0);
                                            ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof coinChest) {
                                // Red orc collision with coin chest
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (!((coinChest) innerObject).isPushed()) {
                                        ((coinChest) innerObject).setSpeedX(((2 * redOrc.getWeight() * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + coinChest.getWeight())) + (((coinChest.getWeight() - redOrc.getWeight()) * ((coinChest) innerObject).getSpeedX()) / (redOrc.getWeight() + coinChest.getWeight())));
                                        ((coinChest) innerObject).setPushed(true);
                                        ((redOrc) outerObject).setSpeedX((((redOrc.getWeight() - coinChest.getWeight()) * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + coinChest.getWeight())) + ((2 * coinChest.getWeight() * ((coinChest) innerObject).getSpeedX()) / (redOrc.getWeight() + coinChest.getWeight())));
                                        ((redOrc) outerObject).push();
                                        ((redOrc) outerObject).setSpeedX(0);
                                    }
                                    if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((coinChest) innerObject).getCoinChestPolygon().getBoundsInParent()) && !(((redOrc) outerObject).getSpeedX() > 0)) {
                                        if (!((redOrc) outerObject).isKilled()) {
                                            ((redOrc) outerObject).setCurrentJumpHeight(0);
                                            ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof weaponChest) {
                                // Red orc collision with weapon chest
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (!((weaponChest) innerObject).isPushed()) {
                                        ((weaponChest) innerObject).setSpeedX(((2 * redOrc.getWeight() * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + weaponChest.getWeight())) + (((weaponChest.getWeight() - redOrc.getWeight()) * ((weaponChest) innerObject).getSpeedX()) / (redOrc.getWeight() + weaponChest.getWeight())));
                                        ((weaponChest) innerObject).setPushed(true);
                                        ((redOrc) outerObject).setSpeedX((((redOrc.getWeight() - weaponChest.getWeight()) * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + weaponChest.getWeight())) + ((2 * weaponChest.getWeight() * ((weaponChest) innerObject).getSpeedX()) / (redOrc.getWeight() + weaponChest.getWeight())));
                                        ((redOrc) outerObject).push();
                                        ((redOrc) outerObject).setSpeedX(0);
                                    }
                                    if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((weaponChest) innerObject).getWeaponChestPolygon().getBoundsInParent()) && !(((redOrc) outerObject).getSpeedX() > 0)) {
                                        if (!((redOrc) outerObject).isKilled()) {
                                            ((redOrc) outerObject).setCurrentJumpHeight(0);
                                            ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof TNT) {
                                // Red orc collision with TNT
                                if (!((TNT) innerObject).isPushed()) {
                                    ((TNT) innerObject).setSpeedX(((2 * redOrc.getWeight() * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + TNT.getWeight())) + (((TNT.getWeight() - redOrc.getWeight()) * ((TNT) innerObject).getSpeedX()) / (redOrc.getWeight() + TNT.getWeight())));
                                    ((TNT) innerObject).setPushed(true);
                                    ((redOrc) outerObject).setSpeedX((((redOrc.getWeight() - TNT.getWeight()) * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + TNT.getWeight())) + ((2 * TNT.getWeight() * ((TNT) innerObject).getSpeedX()) / (redOrc.getWeight() + TNT.getWeight())));
                                    ((redOrc) outerObject).push();
                                    ((redOrc) outerObject).setSpeedX(0);
                                }
                                if (((redOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((TNT) innerObject).getTntPolygon().getBoundsInParent()) && !(((redOrc) outerObject).getSpeedX() > 0)) {
                                    if (!((redOrc) outerObject).isKilled()) {
                                        ((redOrc) outerObject).setCurrentJumpHeight(0);
                                        ((redOrc) outerObject).setSpeedY(-redOrc.getJumpSlice());
                                    }
                                }
                                if (outerObject.collision_detected(innerObject)) {
                                    if (!((TNT) innerObject).isActivated()) {
                                        ((TNT) innerObject).setActivated(true);
                                        ((TNT) innerObject).playTNTAnimation();
                                    } else if (((TNT) innerObject).isExplosionActivated()) {
                                        if (!((redOrc) outerObject).isKilled()) {
                                            ((redOrc) outerObject).setKilled(true);
                                            ((redOrc) outerObject).playDeathAnimation(1, player);
                                        }
                                    }
                                }
                            }
                            if (((redOrc) outerObject).getTopRectangle().getLayoutY() > 780) {  // Death fall detection
                                ((redOrc) outerObject).setSpeedY(0);
                                if (!((redOrc) outerObject).isKilled()) {
                                    ((redOrc) outerObject).setKilled(true);
                                    ((redOrc) outerObject).playDeathAnimation(0, player);
                                }
                            }
                        } else if (outerObject instanceof bossOrc) {
                            // Boss Orc movements
                            if (((bossOrc) outerObject).getCurrentJumpHeight() > bossOrc.getJumpHeight()) {
                                ((bossOrc) outerObject).setSetY(((bossOrc) outerObject).getSpeedY() - bossOrc.getAccelerationY());
                                ((bossOrc) outerObject).setSpeedY(((bossOrc) outerObject).getSetY());
                                ((bossOrc) outerObject).jump();
                                ((bossOrc) outerObject).setCurrentJumpHeight(((bossOrc) outerObject).getCurrentJumpHeight() + ((bossOrc) outerObject).getSetY());

                            } else {
                                ((bossOrc) outerObject).setSetY(((bossOrc) outerObject).getSpeedY() + bossOrc.getAccelerationY());
                                ((bossOrc) outerObject).setSpeedY(((bossOrc) outerObject).getSetY());
                                ((bossOrc) outerObject).jump();
                                //jumpHeight += setY;
                            }
                            if (((bossOrc) outerObject).isPushed()) {
                                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> ((bossOrc) outerObject).push()));
                                timeline.setCycleCount(Timeline.INDEFINITE);
                                timeline.setOnFinished(event -> ((bossOrc) outerObject).setSpeedX(0));
                                if (((bossOrc) outerObject).getSpeedX() + bossOrc.getAccelerationX() <= 0) {
                                    if (!((bossOrc) outerObject).isAttacked()) {
                                        ((bossOrc) outerObject).setAttacked(true);
                                        ((bossOrc) outerObject).setSpeedX(-10);
                                    } else {
                                        ((bossOrc) outerObject).setPushed(false);
                                        if (((bossOrc) outerObject).getSpeedX() - bossOrc.getAccelerationX() <= 0) {
                                            ((bossOrc) outerObject).setSpeedX(((bossOrc) outerObject).getSpeedX() - bossOrc.getAccelerationX());
                                        } else {
                                            ((bossOrc) outerObject).setSpeedX(((bossOrc) outerObject).getSpeedX() + bossOrc.getAccelerationX());
                                        }
                                        timeline.play();
                                    }

                                } else {
                                    if (((bossOrc) outerObject).isAttacked()) {
                                        ((bossOrc) outerObject).setPushed(false);
                                        if (((bossOrc) outerObject).getSpeedX() - bossOrc.getAccelerationX() <= 0) {
                                            ((bossOrc) outerObject).setSpeedX(((bossOrc) outerObject).getSpeedX() - bossOrc.getAccelerationX());
                                        } else {
                                            ((bossOrc) outerObject).setSpeedX(((bossOrc) outerObject).getSpeedX() + bossOrc.getAccelerationX());
                                        }
                                        ((bossOrc) outerObject).push();
                                    } else {
                                        ((bossOrc) outerObject).setPushed(false);
                                        if (((bossOrc) outerObject).getSpeedX() - bossOrc.getAccelerationX() <= 0) {
                                            ((bossOrc) outerObject).setSpeedX(((bossOrc) outerObject).getSpeedX() - bossOrc.getAccelerationX());
                                        } else {
                                            ((bossOrc) outerObject).setSpeedX(((bossOrc) outerObject).getSpeedX() + bossOrc.getAccelerationX());
                                        }
                                        ((bossOrc) outerObject).push();
                                    }
                                }
                            }
                            if (innerObject instanceof smallPlatform) {
                                if (((bossOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((smallPlatform) innerObject).getsPlatformPolygon().getBoundsInParent())) {
                                    if (!((bossOrc) outerObject).isKilled()) {
                                        ((bossOrc) outerObject).setCurrentJumpHeight(0);
                                        ((bossOrc) outerObject).setSpeedY(-bossOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof mediumPlatform) {
                                if (((bossOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((mediumPlatform) innerObject).getmPlatformPolygon().getBoundsInParent())) {
                                    if (!((bossOrc) outerObject).isKilled()) {
                                        ((bossOrc) outerObject).setCurrentJumpHeight(0);
                                        ((bossOrc) outerObject).setSpeedY(-bossOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof bigPlatform) {
                                if (((bossOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((bigPlatform) innerObject).getbPlatformPolygon().getBoundsInParent())) {
                                    if (!((bossOrc) outerObject).isKilled()) {
                                        ((bossOrc) outerObject).setCurrentJumpHeight(0);
                                        ((bossOrc) outerObject).setSpeedY(-bossOrc.getJumpSlice());
                                    }
                                }
                            } else if (innerObject instanceof coinChest) {
                                // Boss orc collision with coin chest
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (!((coinChest) innerObject).isPushed()) {
                                        ((coinChest) innerObject).setSpeedX(((2 * redOrc.getWeight() * ((redOrc) outerObject).getSpeedX()) / (redOrc.getWeight() + coinChest.getWeight())) + (((coinChest.getWeight() - redOrc.getWeight()) * ((coinChest) innerObject).getSpeedX()) / (redOrc.getWeight() + coinChest.getWeight())));
                                        ((coinChest) innerObject).setPushed(true);
                                        ((bossOrc) outerObject).setSpeedX((((bossOrc.getWeight() - coinChest.getWeight()) * ((bossOrc) outerObject).getSpeedX()) / (bossOrc.getWeight() + coinChest.getWeight())) + ((2 * coinChest.getWeight() * ((coinChest) innerObject).getSpeedX()) / (bossOrc.getWeight() + coinChest.getWeight())));
                                        ((bossOrc) outerObject).push();
                                        ((bossOrc) outerObject).setSpeedX(0);
                                    }
                                    if (((bossOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((coinChest) innerObject).getCoinChestPolygon().getBoundsInParent()) && !(((bossOrc) outerObject).getSpeedX() > 0)) {
                                        if (!((bossOrc) outerObject).isKilled()) {
                                            ((bossOrc) outerObject).setCurrentJumpHeight(0);
                                            ((bossOrc) outerObject).setSpeedY(-bossOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof weaponChest) {
                                // Boss orc collision with weapon chest
                                if (outerObject.collision_detected(innerObject)) { // Left collision for push
                                    if (!((weaponChest) innerObject).isPushed()) {
                                        ((weaponChest) innerObject).setSpeedX(((2 * bossOrc.getWeight() * ((bossOrc) outerObject).getSpeedX()) / (bossOrc.getWeight() + weaponChest.getWeight())) + (((weaponChest.getWeight() - bossOrc.getWeight()) * ((weaponChest) innerObject).getSpeedX()) / (bossOrc.getWeight() + weaponChest.getWeight())));
                                        ((weaponChest) innerObject).setPushed(true);
                                        ((bossOrc) outerObject).setSpeedX((((bossOrc.getWeight() - weaponChest.getWeight()) * ((bossOrc) outerObject).getSpeedX()) / (bossOrc.getWeight() + weaponChest.getWeight())) + ((2 * weaponChest.getWeight() * ((weaponChest) innerObject).getSpeedX()) / (bossOrc.getWeight() + weaponChest.getWeight())));
                                        ((bossOrc) outerObject).push();
                                        ((bossOrc) outerObject).setSpeedX(0);
                                    }
                                    if (((bossOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((weaponChest) innerObject).getWeaponChestPolygon().getBoundsInParent()) && !(((bossOrc) outerObject).getSpeedX() > 0)) {
                                        if (!((bossOrc) outerObject).isKilled()) {
                                            ((bossOrc) outerObject).setCurrentJumpHeight(0);
                                            ((bossOrc) outerObject).setSpeedY(-bossOrc.getJumpSlice());
                                        }
                                    }
                                }
                            } else if (innerObject instanceof TNT) {
                                // Boss orc collision with TNT
                                if (!((TNT) innerObject).isPushed()) {
                                    ((TNT) innerObject).setSpeedX((2 * bossOrc.getWeight() * ((bossOrc) outerObject).getSpeedX()) / (bossOrc.getWeight() + TNT.getWeight()));
                                    ((TNT) innerObject).setPushed(true);
                                    ((bossOrc) outerObject).setSpeedX(((bossOrc.getWeight() - TNT.getWeight()) * ((bossOrc) outerObject).getSpeedX()) / (bossOrc.getWeight() + TNT.getWeight()));
                                    ((bossOrc) outerObject).push();
                                    ((bossOrc) outerObject).setSpeedX(0);
                                }
                                if (((bossOrc) outerObject).getBottomRectangle().getBoundsInParent().intersects(((TNT) innerObject).getTntPolygon().getBoundsInParent()) && !(((bossOrc) outerObject).getSpeedX() > 0)) {
                                    if (!((bossOrc) outerObject).isKilled()) {
                                        ((bossOrc) outerObject).setCurrentJumpHeight(0);
                                        ((bossOrc) outerObject).setSpeedY(-bossOrc.getJumpSlice());
                                    }
                                }
                                if (outerObject.collision_detected(innerObject)) {
                                    if (!((TNT) innerObject).isActivated()) {
                                        ((TNT) innerObject).setActivated(true);
                                        ((TNT) innerObject).playTNTAnimation();
                                    } else if (((TNT) innerObject).isExplosionActivated()) {
                                        if (!((bossOrc) outerObject).isKilled()) {
                                            ((bossOrc) outerObject).setKilled(true);
                                            ((bossOrc) outerObject).playDeathAnimation(1, player);
                                            gameOver();  // match end
                                        }
                                    }
                                }
                            }
                        } else if (outerObject instanceof coinChest) {
                            // coin chest movements
                            if (((coinChest) outerObject).getCurrentJumpHeight() > ((coinChest) outerObject).getJumpHeight()) {
                                ((coinChest) outerObject).setSetY(((coinChest) outerObject).getSpeedY() - coinChest.getAccelerationY());
                                ((coinChest) outerObject).setSpeedY(((coinChest) outerObject).getSetY());
                                ((coinChest) outerObject).jump();
                                ((coinChest) outerObject).setCurrentJumpHeight(((coinChest) outerObject).getCurrentJumpHeight() + ((coinChest) outerObject).getSetY());

                            } else {
                                ((coinChest) outerObject).setSetY(((coinChest) outerObject).getSpeedY() + coinChest.getAccelerationY());
                                ((coinChest) outerObject).setSpeedY(((coinChest) outerObject).getSetY());
                                ((coinChest) outerObject).jump();
                                //jumpHeight += setY;
                            }
                            if (((coinChest) outerObject).isPushed()) {
                                if (((coinChest) outerObject).getSpeedX() <= 0 || ((coinChest) outerObject).getSpeedX() + coinChest.getAccelerationX() <= 0) {
                                    ((coinChest) outerObject).setPushed(false);
                                } else {
                                    ((coinChest) outerObject).setSpeedX(((coinChest) outerObject).getSpeedX() + coinChest.getAccelerationX());
                                    ((coinChest) outerObject).push();
                                }
                            }

                            if (innerObject instanceof smallPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((coinChest) outerObject).setCurrentJumpHeight(0);
                                    ((coinChest) outerObject).setSpeedY(-coinChest.getJumpSlice());
                                }
                            } else if (innerObject instanceof mediumPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((coinChest) outerObject).setCurrentJumpHeight(0);
                                    ((coinChest) outerObject).setSpeedY(-coinChest.getJumpSlice());
                                }
                            } else if (innerObject instanceof bigPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((coinChest) outerObject).setCurrentJumpHeight(0);
                                    ((coinChest) outerObject).setSpeedY(-coinChest.getJumpSlice());

                                }
                            }

                            if (((coinChest) outerObject).getCoinChestPolygon().getLayoutY() > 780) {
                                ((coinChest) outerObject).setSpeedY(0);  // Stop y axis motion for easy garbage collection
                            }
                        } else if (outerObject instanceof weaponChest) {
                            // coin chest movements
                            if (((weaponChest) outerObject).getCurrentJumpHeight() > ((weaponChest) outerObject).getJumpHeight()) {
                                ((weaponChest) outerObject).setSetY(((weaponChest) outerObject).getSpeedY() - weaponChest.getAccelerationY());
                                ((weaponChest) outerObject).setSpeedY(((weaponChest) outerObject).getSetY());
                                ((weaponChest) outerObject).jump();
                                ((weaponChest) outerObject).setCurrentJumpHeight(((weaponChest) outerObject).getCurrentJumpHeight() + ((weaponChest) outerObject).getSetY());

                            } else {
                                ((weaponChest) outerObject).setSetY(((weaponChest) outerObject).getSpeedY() + weaponChest.getAccelerationY());
                                ((weaponChest) outerObject).setSpeedY(((weaponChest) outerObject).getSetY());
                                ((weaponChest) outerObject).jump();
                            }
                            if (((weaponChest) outerObject).isPushed()) {
                                if (((weaponChest) outerObject).getSpeedX() <= 0 || ((weaponChest) outerObject).getSpeedX() + weaponChest.getAccelerationX() <= 0) {
                                    ((weaponChest) outerObject).setPushed(false);
                                } else {
                                    ((weaponChest) outerObject).setSpeedX(((weaponChest) outerObject).getSpeedX() + weaponChest.getAccelerationX());
                                    ((weaponChest) outerObject).push();
                                }
                            }

                            if (innerObject instanceof smallPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((weaponChest) outerObject).setCurrentJumpHeight(0);
                                    ((weaponChest) outerObject).setSpeedY(-weaponChest.getJumpSlice());
                                }
                            } else if (innerObject instanceof mediumPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((weaponChest) outerObject).setCurrentJumpHeight(0);
                                    ((weaponChest) outerObject).setSpeedY(-weaponChest.getJumpSlice());

                                }
                            } else if (innerObject instanceof bigPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((weaponChest) outerObject).setCurrentJumpHeight(0);
                                    ((weaponChest) outerObject).setSpeedY(-weaponChest.getJumpSlice());
                                }
                            }

                            if (((weaponChest) outerObject).getWeaponChestPolygon().getLayoutY() > 780) {
                                ((weaponChest) outerObject).setSpeedY(0);  // Stop y axis motion for easy garbage collection
                            }
                        } else if ((outerObject instanceof TNT)) {
//                            // TNT chest movements
                            if (((TNT) outerObject).getCurrentJumpHeight() > ((TNT) outerObject).getJumpHeight()) {
                                ((TNT) outerObject).setSetY(((TNT) outerObject).getSpeedY() - TNT.getAccelerationY());
                                ((TNT) outerObject).setSpeedY(((TNT) outerObject).getSetY());
                                ((TNT) outerObject).jump();
                                ((TNT) outerObject).setCurrentJumpHeight(((TNT) outerObject).getCurrentJumpHeight() + ((TNT) outerObject).getSetY());

                            } else {
                                ((TNT) outerObject).setSetY(((TNT) outerObject).getSpeedY() + TNT.getAccelerationY());
                                ((TNT) outerObject).setSpeedY(((TNT) outerObject).getSetY());
                                ((TNT) outerObject).jump();
                            }
                            if (((TNT) outerObject).isPushed()) {
                                if (((TNT) outerObject).getSpeedX() <= 0 || ((TNT) outerObject).getSpeedX() + TNT.getAccelerationX() <= 0) {
                                    ((TNT) outerObject).setPushed(false);
                                } else {
                                    ((TNT) outerObject).setSpeedX(((TNT) outerObject).getSpeedX() + TNT.getAccelerationX());
                                    ((TNT) outerObject).push();
                                }
                            }
                            if (innerObject instanceof smallPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((TNT) outerObject).setCurrentJumpHeight(0);
                                    ((TNT) outerObject).setSpeedY(-TNT.getJumpSlice());
                                }
                            } else if (innerObject instanceof mediumPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((TNT) outerObject).setCurrentJumpHeight(0);
                                    ((TNT) outerObject).setSpeedY(-TNT.getJumpSlice());

                                }
                            } else if (innerObject instanceof bigPlatform) {
                                if (outerObject.collision_detected(innerObject)) {
                                    ((TNT) outerObject).setCurrentJumpHeight(0);
                                    ((TNT) outerObject).setSpeedY(-TNT.getJumpSlice());
                                }
                            }

                            if (((TNT) outerObject).getTntPolygon().getLayoutY() > 780) {
                                ((TNT) outerObject).setSpeedY(0);  // Stop y axis motion for easy garbage collection
                            }
                        } else if (outerObject instanceof Shuriken) {
                            if (((Shuriken) outerObject).isThrown()) {
                                ((Shuriken) outerObject).setSpeedX(((Shuriken) outerObject).getSpeedX() + Shuriken.getAccelerationX());
                                ((Shuriken) outerObject).throwShuriken();
                                ((Shuriken) outerObject).setTotalDistance(((Shuriken) outerObject).getTotalDistance() + ((Shuriken) outerObject).getSpeedX() + Shuriken.getAccelerationX());  // Calculate total distance travelled
                            }

                            if (innerObject instanceof greenOrc) {
                                if (outerObject.collision_detected(innerObject)) {
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShuriken());
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShurikenPolygon());
                                    iterator.remove();  // Remove shuriken from gameObjects after use
                                    player.getHero().removeShuriken();  // To reduce FPS fluctuation
                                    Glow glow1 = new Glow();
                                    Glow glow2 = new Glow();
                                    glow1.setLevel(0.5);
                                    glow2.setLevel(0);
                                    Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((greenOrc) innerObject).getGreenOrc().effectProperty(), glow1)));
                                    Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((greenOrc) innerObject).getGreenOrc().effectProperty(), glow2)));
                                    timeline1.setOnFinished(event -> timeline2.play());
                                    timeline1.play();
                                    ((greenOrc) innerObject).setHealth(((greenOrc) innerObject).getHealth() - (((Shuriken) outerObject)).getDamage());
                                    if (((greenOrc) innerObject).getHealth() <= 0 && !((greenOrc) innerObject).isKilled()) {
                                        ((greenOrc) innerObject).setKilled(true);
                                        ((greenOrc) innerObject).playDeathAnimation(1, player);
                                    }
                                }
                            } else if (innerObject instanceof redOrc) {
                                if (outerObject.collision_detected(innerObject)) {
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShuriken());
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShurikenPolygon());
                                    iterator.remove();  // Remove shuriken from gameObjects after use
                                    player.getHero().removeShuriken();  // To reduce FPS fluctuation
                                    Glow glow1 = new Glow();
                                    Glow glow2 = new Glow();
                                    glow1.setLevel(0.5);
                                    glow2.setLevel(0);
                                    Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((redOrc) innerObject).getRedOrc().effectProperty(), glow1)));
                                    Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((redOrc) innerObject).getRedOrc().effectProperty(), glow2)));
                                    timeline1.setOnFinished(event -> timeline2.play());
                                    timeline1.play();
                                    ((redOrc) innerObject).setHealth(((redOrc) innerObject).getHealth() - (((Shuriken) outerObject)).getDamage());
                                    if (((redOrc) innerObject).getHealth() <= 0 && !((redOrc) innerObject).isKilled()) {
                                        ((redOrc) innerObject).setKilled(true);
                                        ((redOrc) innerObject).playDeathAnimation(1, player);
                                    }
                                }
                            } else if (innerObject instanceof bossOrc) {
                                if (outerObject.collision_detected(innerObject)) {
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShuriken());
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShurikenPolygon());
                                    iterator.remove();  // Remove shuriken from gameObjects
                                    player.getHero().removeShuriken();  // To reduce FPS fluctuation
                                    Glow glow1 = new Glow();
                                    Glow glow2 = new Glow();
                                    glow1.setLevel(0.5);
                                    glow2.setLevel(0);
                                    Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((bossOrc) innerObject).getBossOrc().effectProperty(), glow1)));
                                    Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((bossOrc) innerObject).getBossOrc().effectProperty(), glow2)));
                                    timeline1.setOnFinished(event -> timeline2.play());
                                    timeline1.play();
                                    ((bossOrc) innerObject).setHealth(((bossOrc) innerObject).getHealth() - (((Shuriken) outerObject)).getDamage());  // Cause damage to boss
                                    if (((bossOrc) innerObject).getHealth() <= 0 && !((bossOrc) innerObject).isKilled()) {
                                        ((bossOrc) innerObject).setKilled(true);
                                        ((bossOrc) innerObject).playDeathAnimation(1, player);
                                        gameOver();  // match end
                                    }
                                }
                            }
                            else if (innerObject instanceof TNT) {
                                if (outerObject.collision_detected(innerObject)) {
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShuriken());
                                    gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShurikenPolygon());
                                    iterator.remove();  // Remove shuriken from gameObjects
                                    player.getHero().removeShuriken();  // To reduce FPS fluctuation
                                    if (!((TNT) innerObject).isActivated()) {
                                        ((TNT) innerObject).setActivated(true);
                                        ((TNT) innerObject).playTNTAnimation();  // Begin detonation
                                    }
                                }
                            }
                            if (((Shuriken) outerObject).getTotalDistance() >= 450) {
                                gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShuriken());
                                gameAnchorPane.getChildren().remove(((Shuriken) outerObject).getShurikenPolygon());
                                iterator.remove();  // Remove shuriken from gameObjects after 500 distance if no previous collision (for an ever so little better rendering)
                                player.getHero().removeShuriken();  // To reduce FPS fluctuation
                            }
                        }
                        else if (outerObject instanceof Sword) {
                            if (outerObject != player.getHero().getCurrentWeapon()) {
                                iterator.remove();
                            }
                            if (innerObject instanceof greenOrc) {
                                if (outerObject.collision_detected(innerObject) && ((Sword) outerObject).isUsed()) {
                                    ((Sword) outerObject).setUsed(false);
                                    Glow glow1 = new Glow();
                                    Glow glow2 = new Glow();
                                    glow1.setLevel(0.5);
                                    glow2.setLevel(0);
                                    Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((greenOrc) innerObject).getGreenOrc().effectProperty(), glow1)));
                                    Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((greenOrc) innerObject).getGreenOrc().effectProperty(), glow2)));
                                    timeline1.setOnFinished(event -> timeline2.play());
                                    timeline1.play();
                                    ((greenOrc) innerObject).setHealth(((greenOrc) innerObject).getHealth() - (((Sword) outerObject)).getDamage());
                                    if (((greenOrc) innerObject).getHealth() <= 0 && !((greenOrc) innerObject).isKilled()) {
                                        ((greenOrc) innerObject).setKilled(true);
                                        ((greenOrc) innerObject).playDeathAnimation(1, player);
                                    }
                                }
                            } else if (innerObject instanceof redOrc) {
                                if (outerObject.collision_detected(innerObject) && ((Sword) outerObject).isUsed()) {
                                    ((Sword) outerObject).setUsed(false);
                                    Glow glow1 = new Glow();
                                    Glow glow2 = new Glow();
                                    glow1.setLevel(0.5);
                                    glow2.setLevel(0);
                                    Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((redOrc) innerObject).getRedOrc().effectProperty(), glow1)));
                                    Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((redOrc) innerObject).getRedOrc().effectProperty(), glow2)));
                                    timeline1.setOnFinished(event -> timeline2.play());
                                    timeline1.play();
                                    ((redOrc) innerObject).setHealth(((redOrc) innerObject).getHealth() - (((Sword) outerObject)).getDamage());
                                    if (((redOrc) innerObject).getHealth() <= 0 && !((redOrc) innerObject).isKilled()) {
                                        ((redOrc) innerObject).setKilled(true);
                                        ((redOrc) innerObject).playDeathAnimation(1, player);
                                    }
                                }
                            } else if (innerObject instanceof bossOrc) {
                                if (outerObject.collision_detected(innerObject) && ((Sword) outerObject).isUsed()) {
                                    ((Sword) outerObject).setUsed(false);
                                    Glow glow1 = new Glow();
                                    Glow glow2 = new Glow();
                                    glow1.setLevel(0.5);
                                    glow2.setLevel(0);
                                    Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((bossOrc) innerObject).getBossOrc().effectProperty(), glow1)));
                                    Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(((bossOrc) innerObject).getBossOrc().effectProperty(), glow2)));
                                    timeline1.setOnFinished(event -> timeline2.play());
                                    timeline1.play();
                                    ((bossOrc) innerObject).setHealth(((bossOrc) innerObject).getHealth() - (((Sword) outerObject)).getDamage());  // Cause damage to boss
                                    if (((bossOrc) innerObject).getHealth() <= 0 && !((bossOrc) innerObject).isKilled()) {
                                        ((bossOrc) innerObject).setKilled(true);
                                        ((bossOrc) innerObject).playDeathAnimation(1, player);
                                        gameOver();  // match end
                                    }
                                }
                            }
                            else if (innerObject instanceof TNT) {
                                if (outerObject.collision_detected(innerObject) && ((Sword) outerObject).isUsed()) {
                                    if (!((TNT) innerObject).isActivated()) {
                                        ((TNT) innerObject).setActivated(true);
                                        ((TNT) innerObject).playTNTAnimation();  // Begin detonation
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (ConcurrentModificationException | IllegalStateException e) {
                System.out.println("Object destroyed");
            }
        }
    }
}
