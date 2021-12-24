package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class bossOrc extends Orc implements Collidable{
    private transient ImageView bossOrc;
    private Polygon bossOrcPolygon;
    private double speedY, speedX, jumpHeight, health, damage;
    private int weapon, orcType; //why

    bossOrc(double x, double y){
        super(x, y);
        speedY = -4;
        jumpHeight = -80;
        bossOrc = new ImageView();
        bossOrcPolygon = new Polygon();
        bossOrc.setLayoutX(x);
        bossOrc.setLayoutY(y);
        bossOrc.setPreserveRatio(true);
        bossOrc.setImage(new Image("/Resources/bossOrc.png", true));
        bossOrcPolygon.setLayoutX(x + 52);
        bossOrcPolygon.setLayoutY(y + 110);
        bossOrcPolygon.setFill(Color.TRANSPARENT);
        bossOrcPolygon.getPoints().setAll(
                -49.08331298828125, 39.32501220703125,
                88.58331298828125, 39.32501220703125,
                88.58331298828125, -104.00831604003906,
                -46.75, -104.00831604003906,
                -46.75, -4.841644287109375,
                -44.91668701171875, -4.841644287109375,
                -44.91668701171875, 11.991668701171875,
                -49.08331298828125, 11.991668701171875);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(bossOrc);
        anchorPane.getChildren().add(bossOrcPolygon);
    }

    public void jump() {
        bossOrc.setLayoutY(bossOrc.getLayoutY() + speedY);
        bossOrcPolygon.setLayoutY(bossOrcPolygon.getLayoutY() + speedY);
    }

    public ImageView getBossOrc() {
        return bossOrc;
    }
    public Polygon getBossOrcPolygon() {
        return bossOrcPolygon;
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
    public void setHealth(double health) {
        this.health = health;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bigPlatform) {
            return ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof redOrc) {
            return ((redOrc) gameObject).getRedOrcPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof greenOrc) {
            return ((greenOrc) gameObject).getGreenOrcPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bossOrc) {
            return ((bossOrc) gameObject).getBossOrcPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof coinChest) {
            return ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof weaponChest) {
            return ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof TNT) {
            //make explosion polygon
            if (((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(bossOrcPolygon.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }
}
