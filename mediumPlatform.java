package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.Random;

public class mediumPlatform extends Platform implements Collidable{
    private final transient ImageView mPlatform;
    private final Polygon mPlatformPolygon;
    private AnchorPane gameAnchorPane;
    private double speedY;
    private double currentJumpHeight;
    private double setY;
    private int jumpHeight;
    private static final double jumpSlice = 0.01;
    private static final double accelerationY = 0.00003;
    Random random = new Random();

    mediumPlatform(double x, double y){
        super(x, y);
        speedY = 0.0001;
        currentJumpHeight = 0;
        jumpHeight = random.nextInt(3);
        mPlatform = new ImageView();
        mPlatformPolygon = new Polygon();
        mPlatform.setLayoutX(x);
        mPlatform.setLayoutY(y);
        mPlatform.setFitWidth(265.0);
        mPlatform.setFitHeight(302.0);
        mPlatform.setPreserveRatio(true);
        mPlatform.setImage(new Image("/Resources/mediumPlatform.png", true));
        mPlatformPolygon.setLayoutX(x + 146);
        mPlatformPolygon.setLayoutY(y + 38);
        mPlatformPolygon.setFill(Color.TRANSPARENT);
        mPlatformPolygon.getPoints().setAll(
                -134.6003875732422, 92.99996948242188,
                -134.6003875732422, 85.33334350585938,
                102.91665649414062, 85.33334350585938,
                102.91664123535156, 92.99996948242188);
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(mPlatform);
        gameAnchorPane.getChildren().add(mPlatformPolygon);
        mPlatform.toBack();
        mPlatformPolygon.toBack();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(mPlatform);
        gameAnchorPane.getChildren().remove(mPlatformPolygon);
    }

    public void jump() {
        mPlatform.setLayoutY(mPlatform.getLayoutY() + speedY);
        mPlatformPolygon.setLayoutY(mPlatformPolygon.getLayoutY() + speedY);
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

    public ImageView getmPlatform() {
        return mPlatform;
    }

    public Polygon getmPlatformPolygon() {
        return mPlatformPolygon;
    }


    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false;
    }
}
