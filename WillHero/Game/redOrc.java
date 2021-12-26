package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class redOrc extends Orc implements Collidable{
    private transient ImageView redOrc;
    private Polygon redOrcPolygon;
    private double speedY, speedX, jumpHeight, health, damage, weight;
    private int weapon, orcType; //why

    public redOrc(double x, double y) {
        super(x, y);
        speedY = -4;
        jumpHeight = -50;
        weight = 10;
        redOrc = new ImageView();
        redOrcPolygon = new Polygon();
        redOrc.setLayoutX(x);
        redOrc.setLayoutY(y);
        redOrc.setFitWidth(47);
        redOrc.setFitHeight(47);
        redOrc.setPreserveRatio(true);
        redOrc.setImage(new Image("/Resources/redOrc.png", true));
        redOrcPolygon.setLayoutX(x + 25);
        redOrcPolygon.setLayoutY(y - 1);
        redOrcPolygon.setStrokeWidth(2);
        redOrcPolygon.setStroke(Color.YELLOW);
        redOrcPolygon.setFill(Color.TRANSPARENT);
        redOrcPolygon.getPoints().setAll(
                -29.0, 51.32501220703125,
                25.04998779296875, 51.32501220703125,
                25.04998779296875, -0.4749755859375,
                -26.150009155273438, -0.4749755859375,
                -26.150009155273438, 31.32501220703125,
                -26.150009155273438, 45.125);
        //redOrc.setScaleX(0.55);
        //redOrc.setScaleY(0.55);
        redOrcPolygon.setScaleX(0.85);
        redOrcPolygon.setScaleY(0.85);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(redOrc);
        anchorPane.getChildren().add(redOrcPolygon);
    }

    public void jump() {
        redOrc.setLayoutY(redOrc.getLayoutY() + speedY);
        redOrcPolygon.setLayoutY(redOrcPolygon.getLayoutY() + speedY);
    }

    public ImageView getRedOrc() {
        return redOrc;
    }
    public Polygon getRedOrcPolygon() {
        return redOrcPolygon;
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
        if (gameObject instanceof mainHero) {
            return ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bigPlatform) {
            return ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof coinChest) {
            return ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof TNT) {
            if (((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent())) {
                return true;
            }
            // Replace get with the explosion polygon
            else return ((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof weaponChest) {
            return ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(redOrcPolygon.getBoundsInParent());
        }
        return false;
    }
}
