package Game;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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
    private Label fpsLabel;
    private mainHero hero;
    private Camera camera;
    private boolean gameStarted, leaped;
    private double jumpHeight;  // cumulative jump height of the player with every call of run
    private double leapLength;  // cumulative leap length of the player with every call of run
    private static final double jumpSlice = 4, leapSlice = 4, accelerationX = 0.5, accelerationY = 0.5;
    private double setX, setY;
    private Timeline timeline;
    private ArrayList<GameObject> gameObjects;

    // FPS computation
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false;
    long now;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make another transition method by polymorphism that takes in other arguments if needed
//        hero = new mainHero(27, 290);
        hero = new mainHero(27, 290);
        hero.addToScreen(gameAnchorPane);
        camera = new Camera(0, 0);
        //hero.playVerticalAnimation(true);
        jumpHeight = 0;
        leapLength = 0;
        gameStarted = true;
        leaped = false;
        System.out.println("Hero: " + hero.getHeroPolygon().getLayoutBounds());
        System.out.println("Red Orc: " + redPolygon.getLayoutBounds());

        timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> run()));  // Run a frame every 10 milliseconds (100 fps)
        timeline.setCycleCount(Timeline.INDEFINITE);

        Animations.translateTransition(redOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(redPolygon, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(greenOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(greenPolygon, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(chest1, 0, -5, 250, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(princess, 0, -100, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(bossOrc1, 0, -30, 1000, TranslateTransition.INDEFINITE, true).play();
        timeline.play();

//        AnimationTimer frameRateMeter = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                displayFPS();
//            }
//        };
//        frameRateMeter.start();
    }

    public void settingsButtonClicked() throws IOException {
        stage = new Stage();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("pauseMenu.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(settingsButton.getScene().getWindow());
        stage.showAndWait();
    }

    public void settingsMouseEntered() {
        glow.setLevel(0.5f);
        settingsButton.setEffect(glow);
    }

    public void settingsMouseExited() {
        settingsButton.setEffect(null);
    }

    public void activateKeyListener() {
        GlobalVariables.scene.setOnMousePressed(mouseEvent -> {
            if (!mouseEvent.isControlDown()) {
                if(mouseEvent.getPickResult().getIntersectedNode().getId() == null || !mouseEvent.getPickResult().getIntersectedNode().getId().equals("settingsButton")) {
                    GlobalVariables.playerLeapSound.stop();
                    GlobalVariables.playerLeapSound.play();
                    hero.setSpeedX(leapSlice);
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

    public void run() {
        displayFPS();
        camera.update(hero, gameAnchorPane, bgAnchorPane);
        System.out.println("hero X: " + hero.getHeroPolygon().getLayoutX());
        System.out.println("hero Y: " + hero.getHeroPolygon().getLayoutY());
        System.out.println("Camera X: " + gameAnchorPane.getLayoutX());
        System.out.println("Camera Y: " + gameAnchorPane.getLayoutY());
        if (hero.getHeroPolygon().getLayoutX() + gameAnchorPane.getLayoutX() != 73) {
            //System.exit(10);
        }
        if (gameStarted) {
            if (!leaped) {  // Stop Y axis motion when player leaps
                if (jumpHeight > hero.getJumpHeight()) {
                    if (hero.getHeroPolygon().getBoundsInParent().intersects(redPolygon.getBoundsInParent())) {
                    } else {
                        //System.out.println("non collide");
                        setY = hero.getSpeedY() - accelerationY;
                        hero.setSpeedY(setY);
                        hero.jump();
                        jumpHeight += setY;
                    }
                } else {
                    //System.out.println("Main Else");
                    setY = hero.getSpeedY() + accelerationY;
                    hero.setSpeedY(setY);
                    hero.jump();
                    //jumpHeight += setY;
                }
            }
            if (leaped) {
                hero.setSpeedY(0);
                if (leapLength < hero.getLeapLength()) {
                    if (hero.getHeroPolygon().getBoundsInParent().intersects(redPolygon.getBoundsInParent()) || hero.getHeroPolygon().getBoundsInParent().intersects(greenPolygon.getBoundsInParent())) {
                        System.out.println("Collided with orc!");
//                    double velocity = hero.getWeight() * (hero.getSpeedX()) / 30;
//                    System.out.println(velocity);
//                    redPolygon.setLayoutX(redPolygon.getLayoutX() + velocity);
                        //hero.setSpeedX(0);
                        leaped = false;
                        leapLength = 0;
                    } else {
                        System.out.println();
                        System.out.println("| | | | |\tLEAPED\t| | | | |");
                        System.out.println();
                        setX = hero.getSpeedX() + accelerationX;
                        hero.setSpeedX(setX);
                        hero.leap();
                        leapLength += setX;
                    }
                } else {
                    hero.setSpeedY(setY);  // At the end of the leap, set Y speed back to before and X to 0.
                    hero.setSpeedX(0);
                    leapLength = 0;
                    leaped = false;
                }
            }
            if (hero.getHeroPolygon().getBoundsInParent().intersects(mediumPolygon.getBoundsInParent()) || hero.getHeroPolygon().getBoundsInParent().intersects(smallPolygon.getBoundsInParent()) || hero.getHeroPolygon().getBoundsInParent().intersects(bigPolygon.getBoundsInParent())) {
                System.out.println("collided");
                GlobalVariables.playerJumpSound.stop();
                GlobalVariables.playerJumpSound.play();
                jumpHeight = 0;
                hero.setSpeedY(-jumpSlice);
            }

            if (hero.getHeroPolygon().getLayoutY() > 800) {  // Death fall detection
                GlobalVariables.playerFallSound.play();
                System.out.println("Game over!");  // Make game over scene
                //timeline.pause();
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                // Play player falling translate transition animation and then timeline.stop()
                timeline.stop();
                gameStarted = false;
            }
        }
    }
}
