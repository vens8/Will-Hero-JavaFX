package Game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private ImageView redOrc1;
    @FXML
    private ImageView greenOrc1;
    @FXML
    private ImageView chest1;
    @FXML
    private ImageView bossOrc1;
    @FXML
    private ImageView princess;
    @FXML
    private Polygon redPolygon;
    @FXML
    private Polygon greenPolygon;
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

    private Camera camera;
    private Player player;
    private boolean gameStarted, leaped;
    private double jumpHeight;  // cumulative jump height of the player with every call of run
    private double leapLength;  // cumulative leap length of the player with every call of run
    private static final double jumpSlice = 4, leapSlice = 4, accelerationX = 0.5, accelerationY = 0.5;
    private double setX, setY;
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

    Glow glow = new Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make another transition method by polymorphism that takes in other arguments if needed
        camera = new Camera(0, 0);
        player = new Player(new mainHero(27, 290));  // Instead of creating an instance of a hero, pass the hero from gameObjects list.
        player.getHero().addToScreen(gameAnchorPane);

        jumpHeight = 0;
        leapLength = 0;
        gameStarted = true;
        leaped = false;

        GlobalVariables.timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> run()));  // Run a frame every 10 milliseconds (100 fps)
        GlobalVariables.timeline.setCycleCount(Timeline.INDEFINITE);

        Animations.translateTransition(redOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(redPolygon, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(greenOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(greenPolygon, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(chest1, 0, -5, 250, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(princess, 0, -100, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(bossOrc1, 0, -30, 1000, TranslateTransition.INDEFINITE, true).play();
        GlobalVariables.timeline.play();
    }

    public void settingsButtonClicked() throws IOException {
        if (GlobalVariables.sound) {
            GlobalVariables.buttonClickSound.stop();
            GlobalVariables.buttonClickSound.play();
        }
        GlobalVariables.timeline.pause();  // Pause the game on clicking the settings button
        stage = new Stage();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("pauseMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(settingsButton.getScene().getWindow());
        pauseController.setStage(stage);  // Sending stage so it can be closed from pauseController after resuming
        stage.setOnCloseRequest(event -> GlobalVariables.timeline.play());  // Resume game when settings is closed
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

    public void activateKeyListener() {
        GlobalVariables.scene.setOnMousePressed(mouseEvent -> {
            if (!mouseEvent.isControlDown()) {
                if(mouseEvent.getPickResult().getIntersectedNode().getId() == null || !mouseEvent.getPickResult().getIntersectedNode().getId().equals("settingsButton")) {
                    if (GlobalVariables.sound) {
                        GlobalVariables.playerLeapSound.stop();
                        GlobalVariables.playerLeapSound.play();
                    }
                    player.getHero().setSpeedX(leapSlice);
                    leaped = true;
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

    // Add objects to the screen as the player moves
    public void generateObjects() {

    }

    public void run() {
        displayFPS();
        displayScore();
        camera.update(player.getHero(), gameAnchorPane, bgAnchorPane);  // Follow the player
//        System.out.println("player.getHero() X: " + player.getHero().getHeroPolygon().getLayoutX());
//        System.out.println("player.getHero() Y: " + player.getHero().getHeroPolygon().getLayoutY());
//        System.out.println("Camera X: " + gameAnchorPane.getLayoutX());
//        System.out.println("Camera Y: " + gameAnchorPane.getLayoutY());
        if (gameStarted) {
            if (!leaped) {  // Stop Y axis motion when player leaps
                if (jumpHeight > player.getHero().getJumpHeight()) {
                    if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redPolygon.getBoundsInParent())) {
                    } else {
                        setY = player.getHero().getSpeedY() - accelerationY;
                        player.getHero().setSpeedY(setY);
                        player.getHero().jump();
                        jumpHeight += setY;
                    }
                } else {
                    //System.out.println("Main Else");
                    setY = player.getHero().getSpeedY() + accelerationY;
                    player.getHero().setSpeedY(setY);
                    player.getHero().jump();
                    //jumpHeight += setY;
                }
            }
            if (leaped) {
                player.getHero().setSpeedY(0);
                if (leapLength < player.getHero().getLeapLength()) {
                    if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(redPolygon.getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(greenPolygon.getBoundsInParent())) {
                        //System.out.println("Collided with orc!");
//                        double velocity = player.getHero().getWeight() * (player.getHero().getSpeedX()) / 10;  Code for collision impact with orc. 10 is the weight of orc
//                        System.out.println(velocity);
//                        redPolygon.setLayoutX(redPolygon.getLayoutX() + velocity);
//                        redOrc1.setLayoutX(redOrc1.getLayoutX() + velocity);
                        leaped = false;
                        leapLength = 0;
                        if (player.getHero().getHeroPolygon().getLayoutY() < 300)
                            player.increaseScore();
                    } else {
                        setX = player.getHero().getSpeedX() + accelerationX;
                        player.getHero().setSpeedX(setX);
                        player.getHero().leap();
                        leapLength += setX;
                    }
                } else {
                    player.getHero().setSpeedY(setY);  // At the end of the leap, set Y speed back to before and X to 0.
                    player.getHero().setSpeedX(0);
                    leapLength = 0;
                    leaped = false;
                    if (player.getHero().getHeroPolygon().getLayoutY() < 300)  // To prevent user from gaining score from leaping mid fall
                        player.increaseScore();
                }
            }
            if (player.getHero().getHeroPolygon().getBoundsInParent().intersects(mediumPolygon.getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(smallPolygon.getBoundsInParent()) || player.getHero().getHeroPolygon().getBoundsInParent().intersects(bigPolygon.getBoundsInParent())) {
                //System.out.println("collided");
                GlobalVariables.playerJumpSound.stop();
                GlobalVariables.playerJumpSound.play();
                jumpHeight = 0;
                player.getHero().setSpeedY(-jumpSlice);
            }
            for (int i = 0; i < gameObjects.size(); i++) {
                if (gameObjects.get(i) instanceof mainHero) {
                    if (player.getHero().getHeroPolygon().getLayoutY() > 780) {  // Death fall detection
                        GlobalVariables.playerFallSound.stop();
                        GlobalVariables.playerFallSound.play();
                        System.out.println("Game over!");  // Make game over scene and fade transition (bring down opacity of the game scene)
                        //GlobalVariables.timeline.pause();
    //                try {
    //                    TimeUnit.SECONDS.sleep(2);
    //                } catch (InterruptedException e) {
    //                    e.printStackTrace();
    //                }
                        // Play player falling translate transition animation and then GlobalVariables.timeline.stop()
                        GlobalVariables.timeline.setRate(0.1);  // Slow down time on death
                        //GlobalVariables.timeline.stop();
                        gameStarted = false;
                    }
                }
                if (gameObjects.get(i) instanceof redOrc){
                    if (((redOrc) gameObjects.get(i)).getRedOrcPolygon().getLayoutY() > 780){
                        // Adding coins to player and remove from arraylist
                        player.increaseCoins(3);
                        //gameObjects.remove(i);
                    }
                }
                if (gameObjects.get(i) instanceof greenOrc){
                    if (((greenOrc) gameObjects.get(i)).getGreenOrcPolygon().getLayoutY() > 780){
                        // Adding coins to player and remove from arraylist
                        player.increaseCoins(1);
                        //gameObjects.remove(i);
                    }
                }
                if (gameObjects.get(i) instanceof coinChest) {
                    if (((coinChest) gameObjects.get(i)).getCoinChestPolygon().getLayoutY() > 780){
                        // remove from arraylist
                        //gameObjects.remove(i);
                    }
                }
                if (gameObjects.get(i) instanceof weaponChest) {
                    if (((weaponChest) gameObjects.get(i)).getWeaponChestPolygon().getLayoutY() > 780){
                        // remove from arraylist
                        //gameObjects.remove(i);
                    }
                }
            }

        }
    }
}
