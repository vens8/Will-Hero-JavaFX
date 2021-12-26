package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class greenOrc extends Orc implements Collidable{
    private transient ImageView greenOrc;
    private Polygon greenOrcPolygon;
    private double speedY, speedX, jumpHeight, health, damage;
    private int weapon, orcType; //why

    public greenOrc(double x, double y) {
        super(x, y);
        speedY = -4;
        jumpHeight = -80;
        greenOrc = new ImageView();
        greenOrcPolygon = new Polygon();
        greenOrc.setLayoutX(x);
        greenOrc.setLayoutY(y);
        greenOrc.setFitHeight(49);
        greenOrc.setFitWidth(49);
        greenOrc.setPreserveRatio(true);
        greenOrc.setImage(new Image("/Resources/greenOrc.png", true));
        greenOrcPolygon.setLayoutX(x + 25);
        greenOrcPolygon.setLayoutY(y - 5);
        greenOrcPolygon.setStroke(Color.RED);
        greenOrcPolygon.setStrokeWidth(2);
        greenOrcPolygon.setFill(Color.TRANSPARENT);
        greenOrcPolygon.getPoints().setAll(
                -27.95001220703125, 51.219512939453125,
                27.0, 51.219512939453125,
                27.0, 31.32501220703125,
                23.25, 31.32501220703125,
                23.250003814697266, 2.567394495010376,
                -26.150020599365234, 2.6253042221069336,
                -26.150009155273438, 31.32501220703125,
                -26.1500244140625, 45.925018310546875);

        greenOrcPolygon.setScaleX(0.9);
        greenOrcPolygon.setScaleY(0.9);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(greenOrc);
        anchorPane.getChildren().add(greenOrcPolygon);
    }

    public void jump() {
        greenOrc.setLayoutY(greenOrc.getLayoutY() + speedY);
        greenOrcPolygon.setLayoutY(greenOrcPolygon.getLayoutY() + speedY);
    }

    public ImageView getGreenOrc() {
        return greenOrc;
    }
    public Polygon getGreenOrcPolygon() {
        return greenOrcPolygon;
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

    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bigPlatform) {
            return ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof redOrc) {
            return ((redOrc) gameObject).getRedOrcPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof greenOrc) {
            return ((greenOrc) gameObject).getGreenOrcPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bossOrc) {
            return ((bossOrc) gameObject).getBossOrcPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof coinChest) {
            return ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof weaponChest) {
            return ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof TNT) {
            if (((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent())) {
                return true;
            }
            // Replace get with the explosion polygon
            else return ((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(greenOrcPolygon.getBoundsInParent());
        }
        return false;
    }
}
