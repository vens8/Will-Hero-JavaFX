package Game;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class redOrc extends Orc implements Collidable{
    private final transient ImageView redOrc;
    private final Rectangle leftRectangle, topRectangle, rightRectangle, bottomRectangle;
    private double speedY, speedX, jumpHeight, health, damage, currentJumpHeight, setX, setY;
    private static final double jumpSlice = 0.0166666666666667;
    private static final double accelerationX = -0.0066666666666667; // acceleration is -0.4 for smooth deceleration
    private static final double accelerationY = 0.0033333333333333;
    private static final double weight = 10;
    private boolean pushed, killed;
    private AnchorPane gameAnchorPane;

    public redOrc(double x, double y) {
        super(x, y);
        speedY = -0.0166666666666667;
        jumpHeight = -60;  // -60
        currentJumpHeight = 0;
        pushed = false;
        killed = false;
        health = 65;  // 3 shots with level 1 weapon, 2 shots with level 2
        redOrc = new ImageView();
        redOrc.setLayoutX(x);
        redOrc.setLayoutY(y);
        redOrc.setFitWidth(47);
        redOrc.setFitHeight(47);
        redOrc.setPreserveRatio(true);
        redOrc.setImage(new Image("/Resources/redOrc.png", true));

        leftRectangle = new Rectangle();
        leftRectangle.setHeight(34);
        leftRectangle.setWidth(4);
        leftRectangle.setArcHeight(5);
        leftRectangle.setArcWidth(5);
        leftRectangle.setLayoutX(x + 2);
        leftRectangle.setLayoutY(y + 9);
        leftRectangle.setFill(Color.TRANSPARENT);

        topRectangle = new Rectangle();
        topRectangle.setHeight(4);
        topRectangle.setWidth(43);
        topRectangle.setArcHeight(5);
        topRectangle.setArcWidth(5);
        topRectangle.setLayoutX(x + 2);
        topRectangle.setLayoutY(y);
        topRectangle.setFill(Color.TRANSPARENT);

        rightRectangle = new Rectangle();
        rightRectangle.setHeight(34);
        rightRectangle.setWidth(4);
        rightRectangle.setArcHeight(5);
        rightRectangle.setArcWidth(5);
        rightRectangle.setLayoutX(x + 42);
        rightRectangle.setLayoutY(y + 9);
        rightRectangle.setFill(Color.TRANSPARENT);

        bottomRectangle = new Rectangle();
        bottomRectangle.setHeight(4);
        bottomRectangle.setWidth(42);
        bottomRectangle.setArcHeight(5);
        bottomRectangle.setArcWidth(5);
        bottomRectangle.setLayoutX(x + 3);
        bottomRectangle.setLayoutY(y + 42);
        bottomRectangle.setFill(Color.TRANSPARENT);
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(redOrc);
        gameAnchorPane.getChildren().add(leftRectangle);
        gameAnchorPane.getChildren().add(topRectangle);
        gameAnchorPane.getChildren().add(rightRectangle);
        gameAnchorPane.getChildren().add(bottomRectangle);
        redOrc.toFront();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(redOrc);
        gameAnchorPane.getChildren().remove(leftRectangle);
        gameAnchorPane.getChildren().remove(topRectangle);
        gameAnchorPane.getChildren().remove(rightRectangle);
        gameAnchorPane.getChildren().remove(bottomRectangle);
    }

    public void push() {
        redOrc.setLayoutX(redOrc.getLayoutX() + speedX);
        leftRectangle.setLayoutX(leftRectangle.getLayoutX() + speedX);
        topRectangle.setLayoutX(topRectangle.getLayoutX() + speedX);
        rightRectangle.setLayoutX(rightRectangle.getLayoutX() + speedX);
        bottomRectangle.setLayoutX(bottomRectangle.getLayoutX() + speedX);
    }

    public void jump() {
        redOrc.setLayoutY(redOrc.getLayoutY() + speedY);
        leftRectangle.setLayoutY(leftRectangle.getLayoutY() + speedY);
        topRectangle.setLayoutY(topRectangle.getLayoutY() + speedY);
        rightRectangle.setLayoutY(rightRectangle.getLayoutY() + speedY);
        bottomRectangle.setLayoutY(bottomRectangle.getLayoutY() + speedY);
    }

    public void playDeathAnimation(int deathType, Player player) {  // 0 for fall death, 1 for all other deaths
        if (deathType == 0) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> {
                if (GlobalVariables.sound) {
                    GlobalVariables.orcDeathSound.stop();
                    GlobalVariables.orcDeathSound.play();
                }
                setKilled(true);
            }),
                    new KeyFrame(Duration.millis(500), event -> {})
            );
            timeline.setOnFinished(event -> {
                player.increaseCoins(4);
            });
            timeline.play();
        }
        else if (deathType == 1) {
            Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), event -> {
                setSpeedY(-0.5);
                Animations.rotateTransition(redOrc, 360, 500, 5, false).play();
                Animations.rotateTransition(leftRectangle, 360, 500, 5, false).play();
                Animations.rotateTransition(topRectangle, 360, 500, 5, false).play();
                Animations.rotateTransition(rightRectangle, 360, 500, 5, false).play();
                Animations.rotateTransition(bottomRectangle, 360, 500, 5, false).play();
                setKilled(true);
            }));
            Timeline timeline2 = new Timeline(new KeyFrame(Duration.ZERO, event -> {
                GlobalVariables.orcDeathSound.stop();
                GlobalVariables.orcDeathSound.play();
            }),
                    new KeyFrame(Duration.millis(500), event -> {})
            );
            timeline2.setOnFinished(event -> {
                player.increaseCoins(4);
            });
            SequentialTransition sequentialTransition = new SequentialTransition(timeline1, timeline2);
            sequentialTransition.play();
        }
    }

    public ImageView getRedOrc() {
        return redOrc;
    }

    public double getSpeedY() {
        return speedY;
    }
    public double getSpeedX() {
        return speedX;
    }
    public double getJumpHeight() {
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
    public void setJumpHeight(double jumpHeight) {
        this.jumpHeight = jumpHeight;
    }
    public void setHealth(double health) {
        this.health = health;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }

    public Rectangle getLeftRectangle() {
        return leftRectangle;
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


    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof mainHero) {
            return ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());        }
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
                    ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());        }
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
        else if (gameObject instanceof weaponChest) {
            return ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(leftRectangle.getBoundsInParent()) ||
                    ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(topRectangle.getBoundsInParent()) ||
                    ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(rightRectangle.getBoundsInParent()) ||
                    ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(bottomRectangle.getBoundsInParent());
        }
        return false;
    }

    public double getCurrentJumpHeight() {
        return currentJumpHeight;
    }

    public void setCurrentJumpHeight(double currentJumpHeight) {
        this.currentJumpHeight = currentJumpHeight;
    }

    public static double getWeight() {
        return weight;
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

    public static double getJumpSlice() {
        return jumpSlice;
    }

    public static double getAccelerationX() {
        return accelerationX;
    }

    public static double getAccelerationY() {
        return accelerationY;
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
}
