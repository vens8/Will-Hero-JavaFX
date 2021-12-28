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
    private Polygon mediumPolygon;
    @FXML
    private Polygon smallPolygon;
    @FXML
    private Polygon bigPolygon;
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

    private Camera camera;
    private Main game;
    private Player player;
    private coinChest chest;
    private weaponChest chest2;
    private TNT tnt;
    private Coin coin1, coin2, coin3;
    private redOrc redOrc;
    private greenOrc greenOrc;
    private boolean gameStarted;
    private ArrayList<GameObject> gameObjects;
    private GameObjectList<Platform> platforms;
    private GameObjectList<Chest> chests;
    private GameObjectList<Orc> orcs;
    private GameObjectList<TNT> TNTs;
    private GameObjectList<Coin> coins;

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
        //player = new Player(new mainHero(27, 290));  // Instead of creating an instance of a hero, pass the hero from gameObjects list.
        chest = new coinChest(347, 249, 10);  // test
        chest.addToScreen(gameAnchorPane);

        chest2 = new weaponChest(800, 211, 1);  // test
        chest2.addToScreen(gameAnchorPane);
        tnt = new TNT(559, 265);
        tnt.addToScreen(gameAnchorPane);
        coin1 = new Coin(930, 236);
        coin2 = new Coin(960, 236);
        coin3 = new Coin(990, 236);
        coin1.addToScreen(gameAnchorPane);
        coin2.addToScreen(gameAnchorPane);
        coin3.addToScreen(gameAnchorPane);
        redOrc = new redOrc(125, 288);  // 288
        redOrc.addToScreen(gameAnchorPane);
        greenOrc = new greenOrc(850, 288);  // 288
        greenOrc.addToScreen(gameAnchorPane);

        gameStarted = true;  // local to the game

        GlobalVariables.timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> run()));  // Run a frame every 10 milliseconds (100 fps)
        GlobalVariables.timeline.setCycleCount(Timeline.INDEFINITE);

        // Add this for the final stage after beating boss
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
        game.getPlayer().setScore(0);
        game.getPlayer().setHero(new mainHero(27, 290));
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
        //System.out.println("Hero max X: " + player.getHero().getHeroPolygon().getLayoutBounds().getMaxX());
        GlobalVariables.scene.setOnMousePressed(mouseEvent -> {
            if (!mouseEvent.isControlDown()) {
                if(mouseEvent.getPickResult().getIntersectedNode().getId() == null || !mouseEvent.getPickResult().getIntersectedNode().getId().equals("settingsButton")) {
                    if (GlobalVariables.sound) {
                        GlobalVariables.playerLeapSound.stop();
                        GlobalVariables.playerLeapSound.play();
                    }
                    System.out.println("Hero in activate: " + player.getHero());
                    player.getHero().setSpeedX(Game.mainHero.getJumpSlice());
                    player.getHero().setLeaped(true);
                    //System.out.println("Hero max X: " + player.getHero().getHeroPolygon().getLayoutBounds().getMaxX() + player.getHero().getHeroPolygon().getLayoutX());
                    //System.out.println("Red min X: " + redOrc.getRedOrcPolygon().getLayoutBounds().getMinX() + redOrc.getRedOrcPolygon().getLayoutX());
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

    // Add objects to the screen as the player moves
    public void generateObjects() {

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
        camera.update(player.getHero(), gameAnchorPane, bgAnchorPane);  // Follow the player
        if (gameStarted) {
            // Orc movements
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
                redOrc.setSpeedX(redOrc.getSpeedX() + Game.redOrc.getAccelerationX());
                redOrc.push();
                //player.getHero().setCurrentLeapLength(player.getHero().getCurrentLeapLength() + player.getHero().getSetX());
                if (redOrc.getSpeedX() <= 0) {
                    //redOrc.setSpeedX(0);
                    redOrc.setPushed(false);
                }
            }
            if (redOrc.getBottomRectangle().getBoundsInParent().intersects(mediumPolygon.getBoundsInParent()) || redOrc.getBottomRectangle().getBoundsInParent().intersects(smallPolygon.getBoundsInParent()) || redOrc.getBottomRectangle().getBoundsInParent().intersects(bigPolygon.getBoundsInParent())) {
                if (!redOrc.isKilled()) {
                    System.out.println("collided");
                    redOrc.setCurrentJumpHeight(0);
                    redOrc.setSpeedY(-Game.redOrc.getJumpSlice());
                }
            }
            // Orc movements
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
                System.out.println("Orc speed: " + greenOrc.getSpeedX());
                greenOrc.setSpeedX(greenOrc.getSpeedX() + Game.greenOrc.getAccelerationX());
                greenOrc.push();
                //player.getHero().setCurrentLeapLength(player.getHero().getCurrentLeapLength() + player.getHero().getSetX());
                if (greenOrc.getSpeedX() <= 0) {
                    //redOrc.setSpeedX(0);
                    greenOrc.setPushed(false);
                }
            }
            if (greenOrc.getGreenOrcPolygon().getBoundsInParent().intersects(mediumPolygon.getBoundsInParent()) || greenOrc.getGreenOrcPolygon().getBoundsInParent().intersects(smallPolygon.getBoundsInParent()) || greenOrc.getGreenOrcPolygon().getBoundsInParent().intersects(bigPolygon.getBoundsInParent())) {
                //System.out.println("collided");
                greenOrc.setCurrentJumpHeight(0);
                greenOrc.setSpeedY(-Game.greenOrc.getJumpSlice());
            }
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
            if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(mediumPolygon.getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(smallPolygon.getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(bigPolygon.getBoundsInParent())) {
                //System.out.println("collided");
                GlobalVariables.playerJumpSound.stop();
                GlobalVariables.playerJumpSound.play();
                player.getHero().setCurrentJumpHeight(0);
                player.getHero().setSpeedY(-Game.mainHero.getJumpSlice());
            }
            if (player.getHero().collision_detected(redOrc)) {
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getLeftRectangle().getBoundsInParent())) {  // hero left of rOrc
                    if (!redOrc.isPushed()) {
                        System.out.println("Player speed: " + player.getHero().getSpeedX());
                        redOrc.setSpeedX(2 * Game.mainHero.getWeight() * player.getHero().getSpeedX() / (Game.mainHero.getWeight() + Game.redOrc.getWeight()));
                        System.out.println("First Orc speed: " + redOrc.getSpeedX());
                        redOrc.setPushed(true);
                        player.getHero().setSpeedX((Game.mainHero.getWeight() - Game.redOrc.getWeight()) * player.getHero().getSpeedX() / (Game.mainHero.getWeight() + Game.redOrc.getWeight()));
                        player.getHero().leap();
                        //player.getHero().setCurrentLeapLength(0);
                    }
                }
                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getTopRectangle().getBoundsInParent()) && !(player.getHero().getSpeedX() > 0) && !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getLeftRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getTopRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getRightRectangle().getBoundsInParent()))) {
                    System.out.println("stopped");
                    GlobalVariables.playerJumpSound.stop();
                    GlobalVariables.playerJumpSound.play();
                    player.getHero().setCurrentJumpHeight(0);
                    player.getHero().setSpeedY(-Game.mainHero.getJumpSlice());
                }

                if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getBottomRectangle().getBoundsInParent()) && !(player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getLeftRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getTopRectangle().getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(redOrc.getRightRectangle().getBoundsInParent()))) {
                    playerDeath(1);
                }
            }
            if (player.getHero().collision_detected(greenOrc)) {
                if (player.getHero().getHeroPolygon().getLayoutY() > greenOrc.getGreenOrcPolygon().getLayoutY() && (player.getHero().getHeroPolygon().getLayoutBounds().getMaxX() + player.getHero().getHeroPolygon().getLayoutX()) > (greenOrc.getGreenOrcPolygon().getLayoutBounds().getMinX() + greenOrc.getGreenOrcPolygon().getLayoutX())) {
                    playerDeath(1);
                }
                if (player.getHero().getHeroPolygon().getBoundsInParent().getMaxY() <= greenOrc.getGreenOrcPolygon().getBoundsInParent().getMinY() && (player.getHero().getHeroPolygon().getLayoutBounds().getMaxX() + player.getHero().getHeroPolygon().getLayoutX()) > (greenOrc.getGreenOrcPolygon().getLayoutBounds().getMinX() + greenOrc.getGreenOrcPolygon().getLayoutX())) {
                    GlobalVariables.playerJumpSound.stop();
                    GlobalVariables.playerJumpSound.play();
                    player.getHero().setCurrentJumpHeight(0);
                    player.getHero().setSpeedY(-Game.mainHero.getJumpSlice());
                }
                if (player.getHero().getHeroPolygon().getLayoutX() < greenOrc.getGreenOrcPolygon().getLayoutX()) {  // hero left of rOrc
                    if (!greenOrc.isPushed()) {
                        System.out.println("Player speed: " + player.getHero().getSpeedX());
                        greenOrc.setSpeedX(2 * Game.mainHero.getWeight() * player.getHero().getSpeedX() / (Game.mainHero.getWeight() + Game.greenOrc.getWeight()));
                        System.out.println("First Orc speed: " + greenOrc.getSpeedX());
                        greenOrc.setPushed(true);
                        player.getHero().setSpeedX((Game.mainHero.getWeight() - Game.greenOrc.getWeight()) * player.getHero().getSpeedX() / (Game.mainHero.getWeight() + Game.greenOrc.getWeight()));
                        player.getHero().leap();
                        //player.getHero().setCurrentLeapLength(0);
                    }
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
