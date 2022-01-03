package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.Random;

public class smallPlatform extends Platform implements Collidable {
    private transient ImageView sPlatform;
    private Polygon sPlatformPolygon;
    private AnchorPane gameAnchorPane;
    private double speedY;
    private double currentJumpHeight;
    private double setY;
    private int jumpHeight;
    private static final double jumpSlice = 0.01;
    private static final double accelerationY = 0.00003;
    Random random = new Random();

    public smallPlatform(double x, double y){
        super(x, y);
        speedY = 0.0001;
        currentJumpHeight = 0;
        jumpHeight = random.nextInt(4);
        sPlatform = new ImageView();
        sPlatformPolygon = new Polygon();
        sPlatform.setLayoutX(x);
        sPlatform.setLayoutY(y);
        sPlatform.setFitWidth(222.0);
        sPlatform.setFitHeight(339.0);
        sPlatform.setPreserveRatio(true);
        sPlatform.setImage(new Image("/Resources/smallPlatform.png", true));
        sPlatformPolygon.setLayoutX(x + 91);
        sPlatformPolygon.setLayoutY(y + 166);
        sPlatformPolygon.setFill(Color.TRANSPARENT);
        sPlatformPolygon.getPoints().setAll(
                102.583251953125, -54.000030517578125,
                102.58328247070312, -60.5,
                -66.19961547851562, -60.5,
                -66.19964599609375, -54.000030517578125);
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(sPlatform);
        gameAnchorPane.getChildren().add(sPlatformPolygon);
        sPlatform.toBack();
        sPlatformPolygon.toBack();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(sPlatform);
        gameAnchorPane.getChildren().remove(sPlatformPolygon);
    }

    public void jump() {
        sPlatform.setLayoutY(sPlatform.getLayoutY() + speedY);
        sPlatformPolygon.setLayoutY(sPlatformPolygon.getLayoutY() + speedY);
    }

    public ImageView getsPlatform() {
        return sPlatform;
    }
    public Polygon getsPlatformPolygon() {
        return sPlatformPolygon;
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
