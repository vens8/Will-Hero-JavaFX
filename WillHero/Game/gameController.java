package Game;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Camera;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private Polygon mediumPolygon;
    @FXML
    private ImageView settingsButton;
    @FXML
    private AnchorPane gameAnchorPane;
    private mainHero hero;
    private Camera camera;
    private boolean gameStarted;
    private double jumpHeight;  // cumulative jump height of the player with every call of run
    private double leapLength;  // cumulative leap length of the player with every call of run
    private static final double jumpSlice = 4, leapSlice = 4, accelerationX = 10, accelerationY = 5;
    private ArrayList<GameObject> gameObjects;

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make another transition method by polymorphism that takes in other arguments if needed
        hero = new mainHero(27, 290);
        hero.addToScreen(gameAnchorPane);
        //hero.playVerticalAnimation(true);
        jumpHeight = 0;
        leapLength = 0;
        gameStarted = true;
        System.out.println("Hero: " + hero.getHeroPolygon().getLayoutBounds());
        System.out.println("Red Orc: " + redPolygon.getLayoutBounds());
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> run()));
        tl.setCycleCount(Timeline.INDEFINITE);

        Animations.translateTransition(redOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(redPolygon, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(greenOrc1, 0, -160, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(chest1, 0, -5, 250, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(princess, 0, -100, 500, TranslateTransition.INDEFINITE, true).play();
        Animations.translateTransition(bossOrc1, 0, -30, 1000, TranslateTransition.INDEFINITE, true).play();
        tl.play();
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
                }
            }
        });
    }

    public void accelerate() {
        
    }

    public void run() {
        if (gameStarted) {
            if (jumpHeight > hero.getJumpHeight()) {
                if (hero.getHeroPolygon().getBoundsInParent().intersects(redPolygon.getBoundsInParent())) {
                    //hero.setSpeedY(hero.getSpeedY() + jumpSlice * Math.signum(hero.getSpeedY()));
                    hero.setSpeedY(jumpSlice);
                }
                else {
                    hero.jump();
                    jumpHeight -= jumpSlice;
                }

                if(hero.getHeroPolygon().getBoundsInParent().intersects(mediumPolygon.getBoundsInParent())) {
                    GlobalVariables.playerJumpSound.stop();
                    GlobalVariables.playerJumpSound.play();
                    jumpHeight = 0;
                    hero.setSpeedY(-jumpSlice);
                }
            }
            else {
                hero.setSpeedY(4);
                jumpHeight += jumpSlice;
            }
            if (leapLength < hero.getLeapLength()) {
                if (hero.getHeroPolygon().getBoundsInParent().intersects(redPolygon.getBoundsInParent())) {
                    //hero.setSpeedX(hero.getSpeedX() + 1 * Math.signum(hero.getSpeedX()));
//                    if (!(hero.getHero().getLayoutY() <= redPolygon.getLayoutY()))
//                        hero.manualMove(-2, 0);
                    hero.setSpeedX(0);
                }
                else {
                    hero.leap();
                    leapLength += leapSlice;
                }
            }
            else {
                hero.setSpeedY(-jumpSlice);
                hero.setSpeedX(0);
                leapLength = 0;
            }
        }
    }
}
