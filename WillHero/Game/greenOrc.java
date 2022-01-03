package Game;

import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class greenOrc extends Orc implements Collidable{
    private final transient ImageView greenOrc;
    private final Rectangle leftRectangle, topRectangle, rightRectangle, bottomRectangle;
    private double speedY, speedX, jumpHeight, health, damage, currentJumpHeight, setY;
    private static final double jumpSlice = 0.0166666666666667;
    private static final double accelerationX = -0.0083333333333333; // match with that of mainHero
    private static final double accelerationY = 0.0016666666666667;
    private static final double weight = 5;
    private boolean pushed, killed;
    private AnchorPane gameAnchorPane;

    public greenOrc(double x, double y) {
        super(x, y);
        speedY = -0.0166666666666667;
        jumpHeight = -40;
        currentJumpHeight = 0;
        pushed = false;
        killed = false;
        health = 25;
        greenOrc = new ImageView();
        greenOrc.setLayoutX(x);
        greenOrc.setLayoutY(y);
        greenOrc.setFitHeight(51);
        greenOrc.setFitWidth(51);
        greenOrc.setPreserveRatio(true);
        greenOrc.setImage(new Image("/Resources/greenOrc.png", true));
        greenOrc.setScaleX(0.9);
        greenOrc.setScaleY(0.9);

        leftRectangle = new Rectangle();
        leftRectangle.setHeight(28);
        leftRectangle.setWidth(4);
        leftRectangle.setArcHeight(5);
        leftRectangle.setArcWidth(5);
        leftRectangle.setLayoutX(x + 4);
        leftRectangle.setLayoutY(y + 8);
        leftRectangle.setFill(Color.TRANSPARENT);

        topRectangle = new Rectangle();
        topRectangle.setHeight(4);
        topRectangle.setWidth(43);
        topRectangle.setArcHeight(5);
        topRectangle.setArcWidth(5);
        topRectangle.setLayoutX(x + 3);
        topRectangle.setLayoutY(y);
        topRectangle.setFill(Color.TRANSPARENT);

        rightRectangle = new Rectangle();
        rightRectangle.setHeight(28);
        rightRectangle.setWidth(4);
        rightRectangle.setArcHeight(5);
        rightRectangle.setArcWidth(5);
        rightRectangle.setLayoutX(x + 41);
        rightRectangle.setLayoutY(y + 8);
        rightRectangle.setFill(Color.TRANSPARENT);

        bottomRectangle = new Rectangle();
        bottomRectangle.setHeight(4);
        bottomRectangle.setWidth(41);
        bottomRectangle.setArcHeight(5);
        bottomRectangle.setArcWidth(5);
        bottomRectangle.setLayoutX(x + 5);
        bottomRectangle.setLayoutY(y + 41);
        bottomRectangle.setFill(Color.TRANSPARENT);
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(greenOrc);
        gameAnchorPane.getChildren().add(leftRectangle);
        gameAnchorPane.getChildren().add(topRectangle);
        gameAnchorPane.getChildren().add(rightRectangle);
        gameAnchorPane.getChildren().add(bottomRectangle);
        greenOrc.toFront();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(greenOrc);
        gameAnchorPane.getChildren().remove(leftRectangle);
        gameAnchorPane.getChildren().remove(topRectangle);
        gameAnchorPane.getChildren().remove(rightRectangle);
        gameAnchorPane.getChildren().remove(bottomRectangle);
    }

    public void push() {
        greenOrc.setLayoutX(greenOrc.getLayoutX() + speedX);
        leftRectangle.setLayoutX(leftRectangle.getLayoutX() + speedX);
        topRectangle.setLayoutX(topRectangle.getLayoutX() + speedX);
        rightRectangle.setLayoutX(rightRectangle.getLayoutX() + speedX);
        bottomRectangle.setLayoutX(bottomRectangle.getLayoutX() + speedX);
    }

    public void jump() {
        greenOrc.setLayoutY(greenOrc.getLayoutY() + speedY);
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
                player.increaseCoins(2);
            });
            timeline.play();
        }
        else if (deathType == 1) {
            Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(100), event -> {
                setSpeedY(-0.5);
                Animations.rotateTransition(greenOrc, 360, 500, 5, false).play();
                Animations.rotateTransition(leftRectangle, 360, 500, 5, false).play();
                Animations.rotateTransition(topRectangle, 360, 500, 5, false).play();
                Animations.rotateTransition(rightRectangle, 360, 500, 5, false).play();
                Animations.rotateTransition(bottomRectangle, 360, 500, 5, false).play();
                setKilled(true);
            }));
            Timeline timeline2 = new Timeline(new KeyFrame(Duration.ZERO, event -> {
                if (GlobalVariables.sound) {
                    GlobalVariables.orcDeathSound.stop();
                    GlobalVariables.orcDeathSound.play();
                }
            }),
                    new KeyFrame(Duration.millis(500), event -> {})
            );
            timeline2.setOnFinished(event -> {
                player.increaseCoins(2);
            });
            SequentialTransition sequentialTransition = new SequentialTransition(timeline1, timeline2);
            sequentialTransition.play();
        }
    }

    public ImageView getGreenOrc() {
        return greenOrc;
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
    public boolean isPushed() {
        return pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
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

    public static double getJumpSlice() {
        return jumpSlice;
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

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }
}
