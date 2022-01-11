package Game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.ObjectOutputStream;

public class bossOrc extends Orc implements Collidable {
    private transient ImageView bossOrc;
    private final Rectangle leftRectangle;
    private final Rectangle topRectangle;
    private final Rectangle rightRectangle;
    private final Rectangle bottomRectangle;
    private double speedY, speedX, health, damage, currentJumpHeight, setX, setY;
    private static final double jumpHeight = -8;
    private static final double jumpSlice = 0.0033333333333333;
    private static double accelerationX = -0.0033333333333333; // smooth acceleration
    private static final double accelerationY = 0.00033333333333333;
    private static final double weight = 2000;  // Heavy and bulky and can push the player off
    private boolean pushed, killed, attacked;
    private AnchorPane gameAnchorPane;

    bossOrc(double x, double y) {
        super(x, y);
        speedY = -0.0033333333333333;
        currentJumpHeight = 0;
        pushed = false;
        killed = false;
        attacked = false;
        health = 800;  // 800
        bossOrc = new ImageView();
        bossOrc.setLayoutX(x);
        bossOrc.setLayoutY(y);
        bossOrc.setFitHeight(155);
        bossOrc.setFitWidth(157);
        bossOrc.setPreserveRatio(true);
        bossOrc.setImage(new Image("/Resources/bossOrc.png", true));

        leftRectangle = new Rectangle();
        leftRectangle.setHeight(130);
        leftRectangle.setWidth(40);
        leftRectangle.setArcHeight(5);
        leftRectangle.setArcWidth(5);
        leftRectangle.setLayoutX(x + 10);
        leftRectangle.setLayoutY(y + 14);
        leftRectangle.setStrokeWidth(2);
        leftRectangle.setStroke(Color.BLUE);
        leftRectangle.setFill(Color.TRANSPARENT);

        topRectangle = new Rectangle();
        topRectangle.setHeight(4);
        topRectangle.setWidth(132);
        topRectangle.setArcHeight(5);
        topRectangle.setArcWidth(5);
        topRectangle.setLayoutX(x + 10);
        topRectangle.setLayoutY(y + 6);
        topRectangle.setFill(Color.TRANSPARENT);

        rightRectangle = new Rectangle();
        rightRectangle.setHeight(129);
        rightRectangle.setWidth(4);
        rightRectangle.setArcHeight(5);
        rightRectangle.setArcWidth(5);
        rightRectangle.setLayoutX(x + 137);
        rightRectangle.setLayoutY(y + 15);
        rightRectangle.setFill(Color.TRANSPARENT);

        bottomRectangle = new Rectangle();
        bottomRectangle.setHeight(4);
        bottomRectangle.setWidth(132);
        bottomRectangle.setArcHeight(5);
        bottomRectangle.setArcWidth(5);
        bottomRectangle.setLayoutX(x + 10);
        bottomRectangle.setLayoutY(y + 146);
        bottomRectangle.setFill(Color.TRANSPARENT);
    }



    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(bossOrc);
        gameAnchorPane.getChildren().add(leftRectangle);
        gameAnchorPane.getChildren().add(topRectangle);
        gameAnchorPane.getChildren().add(rightRectangle);
        gameAnchorPane.getChildren().add(bottomRectangle);
        bossOrc.toFront();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(bossOrc);
        gameAnchorPane.getChildren().remove(leftRectangle);
        gameAnchorPane.getChildren().remove(topRectangle);
        gameAnchorPane.getChildren().remove(rightRectangle);
        gameAnchorPane.getChildren().remove(bottomRectangle);
    }

    public void push() {
        bossOrc.setLayoutX(bossOrc.getLayoutX() + speedX);
        leftRectangle.setLayoutX(leftRectangle.getLayoutX() + speedX);
        topRectangle.setLayoutX(topRectangle.getLayoutX() + speedX);
        rightRectangle.setLayoutX(rightRectangle.getLayoutX() + speedX);
        bottomRectangle.setLayoutX(bottomRectangle.getLayoutX() + speedX);
    }

    public void jump() {
        bossOrc.setLayoutY(bossOrc.getLayoutY() + speedY);
        leftRectangle.setLayoutY(leftRectangle.getLayoutY() + speedY);
        topRectangle.setLayoutY(topRectangle.getLayoutY() + speedY);
        rightRectangle.setLayoutY(rightRectangle.getLayoutY() + speedY);
        bottomRectangle.setLayoutY(bottomRectangle.getLayoutY() + speedY);
    }

    public void playDeathAnimation(int deathType, Player player) {  // 0 for fall death, 1 for all other deaths
        if (deathType == 0) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> {
                if (GlobalVariables.sound) {
                    GlobalVariables.bossOrcDeathSound.stop();
                    GlobalVariables.bossOrcDeathSound.play();
                }
                setKilled(true);
            }),
                    new KeyFrame(Duration.millis(500), event -> {})
            );
            timeline.setOnFinished(event -> {
                player.increaseCoins(100);
            });
            timeline.play();
        }
        else if (deathType == 1) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> {
                setSpeedY(-1);
                setSpeedX(0.1);  // Motion after death
                setKilled(true);
                if (GlobalVariables.sound) {
                    GlobalVariables.bossOrcDeathSound.stop();
                    GlobalVariables.bossOrcDeathSound.play();
                }
            }),
                    new KeyFrame(Duration.millis(500), event -> {})
            );
            timeline.setOnFinished(event -> {
                player.increaseCoins(100);
            });
            timeline.play();
        }
    }

    public ImageView getBossOrc() {
        return bossOrc;
    }

    public double getSpeedY() {
        return speedY;
    }
    public double getSpeedX() {
        return speedX;
    }
    public static double getJumpHeight() {
        return jumpHeight;
    }
    public double getHealth() {
        return health;
    }
    public double getDamage() {
        return damage;
    }
    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }
    public void setHealth(double health) {
        this.health = health;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }
    public Rectangle getTopRectangle() {
        return topRectangle;
    }

    public Rectangle getRightRectangle() {
        return rightRectangle;
    }

    public Rectangle getBottomRectangle() {
        return bottomRectangle;
    }

    public static double getJumpSlice() {
        return jumpSlice;
    }

    public static void setAccelerationX(double accelerationX) {
        Game.bossOrc.accelerationX = accelerationX;
    }

    public static double getAccelerationX() {
        return accelerationX;
    }

    public static double getAccelerationY() {
        return accelerationY;
    }

    public static double getWeight() {
        return weight;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof bigPlatform) {
            return ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof redOrc) {
            return ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof greenOrc) {
            return ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof bossOrc) {
            return ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof coinChest) {
            return ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof weaponChest) {
            return ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof TNT) {
            if (((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent())) {
                return true;
            }
            else return ((TNT) gameObject).getExplosionPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((TNT) gameObject).getExplosionPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((TNT) gameObject).getExplosionPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((TNT) gameObject).getExplosionPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        return false;
    }

    public boolean isPushed() {
        return pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public double getCurrentJumpHeight() {
        return currentJumpHeight;
    }

    public void setCurrentJumpHeight(double currentJumpHeight) {
        this.currentJumpHeight = currentJumpHeight;
    }

    public double getSetX() {
        return setX;
    }

    public void setSetX(double setX) {
        this.setX = setX;
    }

    public double getSetY() {
        return setY;
    }

    public void setSetY(double setY) {
        this.setY = setY;
    }

    public Rectangle getLeftRectangle() {
        return leftRectangle;
    }

    public boolean isAttacked() {
        return attacked;
    }

    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }
}
