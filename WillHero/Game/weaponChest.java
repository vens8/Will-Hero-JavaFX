package Game;

import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class weaponChest extends Chest implements Cloneable {  // Clones the current weapon each time the player leaps
    private Sword sword;
    private Shuriken shuriken;
    private int weaponType;  // 0 for shuriken and 1 for sword
    private transient ImageView weaponChestImageView;
    private Image image1 = new Image("/Resources/wchest1.png", true);
    private Image image2 = new Image("/Resources/wchest2.png", true);
    private Image image3 = new Image("/Resources/wchest3.png", true);
    private Image image4 = new Image("/Resources/wchest4.png", true);
    private Image image5 = new Image("/Resources/wchest5.png", true);
    private Image image6 = new Image("/Resources/wchest6.png", true);
    private transient Polygon weaponChestPolygon;
    private boolean activated, pushed;
    private AnchorPane gameAnchorPane;
    private double speedX;
    private double speedY;
    private double currentJumpHeight;
    private double setY;
    private double jumpHeight;
    private static final double jumpSlice = 0.0001;
    private static final double accelerationY = 0.0003;
    private static final double WEIGHT = 5, accelerationX = -0.0125;  // Acceleration of weaponChest is 1.5 times acceleration of greenOrc

    public weaponChest(double x, double y, int weaponType) {
        super(x, y);
        speedX = 0;
        speedY = -0.0001;
        pushed = false;
        currentJumpHeight = 0;
        jumpHeight = -3;
        weaponChestImageView = new ImageView();
        weaponChestImageView.setImage(image1);
        weaponChestPolygon = new Polygon();
        weaponChestImageView.setLayoutX(x);
        weaponChestImageView.setLayoutY(y);
        weaponChestImageView.setFitWidth(200.0);
        weaponChestImageView.setFitHeight(175.0);
        weaponChestImageView.setPreserveRatio(true);
        weaponChestPolygon.setLayoutX(x + 64);
        weaponChestPolygon.setLayoutY(y + 68);
        weaponChestPolygon.setFill(Color.TRANSPARENT);
        weaponChestPolygon.getPoints().setAll(
                -27.95001220703125, 51.219512939453125,
                79.25, 51.219512939453125,
                79.25, -12.274993896484375,
                -18.54998779296875, -12.274993896484375,
                -27.95001220703125, -2.875);

        weaponChestImageView.setScaleX(0.75);
        weaponChestImageView.setScaleY(0.75);
        weaponChestPolygon.setScaleX(0.75);
        weaponChestPolygon.setScaleY(0.75);
        this.weaponType = weaponType;
        if (this.weaponType == 1) {
            sword = new Sword(x + 70, y + 50);
            shuriken = null;
            sword.getSwordPolygon().setDisable(true);  // Initially should be invisible and non-interactive
            sword.getSwordPolygon().setVisible(false);
            sword.getSword().setDisable(true); // Initially should be invisible and non-interactive
            sword.getSword().setVisible(false);
        }
        else {
            shuriken = new Shuriken(x + 70, y + 50, 1);
            sword = null;
            shuriken.getShuriken().setDisable(true);  // Initially should be invisible and non-interactive
            shuriken.getShuriken().setVisible(false);
            shuriken.getShurikenPolygon().setDisable(true); // Initially should be invisible and non-interactive
            shuriken.getShurikenPolygon().setVisible(false);
        }
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(weaponChestImageView);
        gameAnchorPane.getChildren().add(weaponChestPolygon);
        weaponChestImageView.toFront();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(weaponChestImageView);
        gameAnchorPane.getChildren().remove(weaponChestPolygon);
    }

    public void playChestAnimation(Player player) {
        if (GlobalVariables.sound) {
            GlobalVariables.weaponChestOpenSound.stop();
            GlobalVariables.weaponChestOpenSound.play();
        }
        Timeline timeline1 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(weaponChestImageView.imageProperty(), image1)),
                new KeyFrame(Duration.millis(100), new KeyValue(weaponChestImageView.imageProperty(), image2)),
                new KeyFrame(Duration.millis(200), new KeyValue(weaponChestImageView.imageProperty(), image3)),
                new KeyFrame(Duration.millis(300), new KeyValue(weaponChestImageView.imageProperty(), image4)),
                new KeyFrame(Duration.millis(400), new KeyValue(weaponChestImageView.imageProperty(), image5)),
                new KeyFrame(Duration.millis(500), new KeyValue(weaponChestImageView.imageProperty(), image6)),
                new KeyFrame(Duration.millis(600), event -> {
                    if (this.weaponType == 1) {
                        sword.addToScreen(gameAnchorPane);
                        sword.getSwordPolygon().setDisable(false);  // Initially should be invisible and non-interactive
                        sword.getSwordPolygon().setVisible(true);
                        sword.getSword().setDisable(false); // Initially should be invisible and non-interactive
                        sword.getSword().setVisible(true);
                    }
                    else {
                        shuriken.addToScreen(gameAnchorPane);
                        shuriken.getShuriken().setDisable(false);  // Initially should be invisible and non-interactive
                        shuriken.getShuriken().setVisible(true);
                        shuriken.getShurikenPolygon().setDisable(false); // Initially should be invisible and non-interactive
                        shuriken.getShurikenPolygon().setVisible(true);
                    }
                })
        );
        timeline1.setCycleCount(1);
        Animation animation1;
        Timeline timeline2, timeline3;
        if (weaponType == 1) {
            // Animation
            animation1 = Animations.translateTransition(sword.getSword(), 0, -75, 500, 1, false);
            timeline2 = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(sword.getSword().scaleXProperty(), 1)),
                    new KeyFrame(Duration.millis(250), new KeyValue(sword.getSword().scaleYProperty(), 1)));
            timeline2.setCycleCount(1);
            timeline3 = new Timeline(new KeyFrame(Duration.millis(10), event -> {
                sword.getSword().setDisable(true);
                sword.getSword().setVisible(false);
                sword.getSwordPolygon().setDisable(true);
                sword.getSwordPolygon().setVisible(false);
                this.gameAnchorPane.getChildren().removeAll(sword.getSword(), sword.getSwordPolygon());
            }));
        }
        else {
            animation1 = Animations.translateTransition(shuriken.getShuriken(), 0, -75, 500, 1, false);
            timeline2 = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(shuriken.getShuriken().scaleXProperty(), 1)),
                    new KeyFrame(Duration.millis(250), new KeyValue(shuriken.getShuriken().scaleYProperty(), 1)));
            timeline2.setCycleCount(1);
            timeline3 = new Timeline(new KeyFrame(Duration.millis(10), event -> {
                shuriken.getShuriken().setDisable(true);
                shuriken.getShuriken().setVisible(false);
                shuriken.getShurikenPolygon().setDisable(true);
                shuriken.getShurikenPolygon().setVisible(false);
                this.gameAnchorPane.getChildren().removeAll(shuriken.getShuriken(), shuriken.getShurikenPolygon());
            }));
        }
        timeline3.setCycleCount(1);
        SequentialTransition sequentialTransition = new SequentialTransition (timeline1, timeline2, animation1, timeline3);
        sequentialTransition.setCycleCount(1);
        sequentialTransition.play();
    }

    public Sword getSword() {
        return sword;
    }
    public void setSword(Sword sword) {
        this.sword = sword;
    }
    public Shuriken getShuriken() {
        return shuriken;
    }
    public void setShuriken(Shuriken shuriken) {
        this.shuriken = shuriken;
    }
    public int getWeaponType() {
        return weaponType;
    }
    public void setWeaponType(int weaponType) {
        this.weaponType = weaponType;
    }
    public ImageView getWeaponChestImageView() {
        return weaponChestImageView;
    }
    public void setWeaponChestImageView(ImageView weaponChestImageView) {
        this.weaponChestImageView = weaponChestImageView;
    }

    public boolean isPushed() {
        return pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
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

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public static double getWeight() {
        return WEIGHT;
    }

    public static double getAccelerationX() {
        return accelerationX;
    }

    public static double getAccelerationY() {
        return accelerationY;
    }

    public static double getJumpSlice() {
        return jumpSlice;
    }

    public void push() {  // move chest
        weaponChestImageView.setLayoutX(weaponChestImageView.getLayoutX() + speedX);
        weaponChestPolygon.setLayoutX(weaponChestPolygon.getLayoutX() + speedX);
    }

    public void jump() {
        weaponChestImageView.setLayoutY(weaponChestImageView.getLayoutY() + speedY);
        weaponChestPolygon.setLayoutY(weaponChestPolygon.getLayoutY() + speedY);
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Polygon getWeaponChestPolygon() {
        return weaponChestPolygon;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(weaponChestPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(weaponChestPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bigPlatform) {
            return ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(weaponChestPolygon.getBoundsInParent());
        }
        return false;
    }

    public double getJumpHeight() {
        return jumpHeight;
    }
}

