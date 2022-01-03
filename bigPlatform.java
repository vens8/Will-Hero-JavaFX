package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.Random;

public class bigPlatform extends Platform implements Collidable{
    private transient ImageView bPlatform;
    private Polygon bPlatformPolygon;
    private AnchorPane gameAnchorPane;
    private double speedY;
    private double currentJumpHeight;
    private double setY;
    private int jumpHeight;
    private static final double jumpSlice = 0.01;
    private static final double accelerationY = 0.00003;
    Random random = new Random();

    bigPlatform(double x, double y){
        super(x, y);
        speedY = 0.0001;
        currentJumpHeight = 0;
        jumpHeight = random.nextInt(2);
        bPlatform = new ImageView();
        bPlatformPolygon = new Polygon();
        bPlatform.setLayoutX(x);
        bPlatform.setLayoutY(y);
        bPlatform.setFitWidth(521.0);
        bPlatform.setFitHeight(265.0);
        bPlatform.setPreserveRatio(true);
        bPlatform.setImage(new Image("/Resources/bigPlatform.png", true));
        bPlatformPolygon.setLayoutX(x + 64);
        bPlatformPolygon.setLayoutY(y + 189);
        bPlatformPolygon.setFill(Color.TRANSPARENT);
        bPlatformPolygon.getPoints().setAll(
                440.9166259765625, -84.80001831054688,
                440.9166259765625, -91.5,
                -52.75, -91.5,
                -52.75, -84.80001831054688);
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(bPlatform);
        gameAnchorPane.getChildren().add(bPlatformPolygon);
        bPlatform.toBack();
        bPlatformPolygon.toBack();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(bPlatform);
        gameAnchorPane.getChildren().remove(bPlatformPolygon);
    }

    public void jump() {
        bPlatform.setLayoutY(bPlatform.getLayoutY() + speedY);
        bPlatformPolygon.setLayoutY(bPlatformPolygon.getLayoutY() + speedY);
    }

    public Polygon getbPlatformPolygon() {
        return bPlatformPolygon;
    }

    public ImageView getbPlatform() {
        return bPlatform;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getCurrentJumpHeight() {
        return currentJumpHeight;
    }

    public void setCurrentJumpHeight(double currentJumpHeight) {
        this.currentJumpHeight = currentJumpHeight;
    }

    public double getSetY() {
        return setY;
    }

    public void setSetY(double setY) {
        this.setY = setY;
    }

    public int getJumpHeight() {
        return jumpHeight;
    }

    public void setJumpHeight(int jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public static double getJumpSlice() {
        return jumpSlice;
    }

    public static double getAccelerationY() {
        return accelerationY;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
