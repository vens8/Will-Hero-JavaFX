package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Sword extends Weapon implements Collidable{
    private transient ImageView sword;
    private Polygon swordPolygon;
    private double speedX, speedY;

    public Sword(double x, double y) {
        super(x, y);
        speedX = 0;
        speedY = -4;
        sword = new ImageView();
        swordPolygon = new Polygon();
        sword.setLayoutX(x);
        sword.setLayoutY(y);
        sword.setFitWidth(98.0);
        sword.setFitHeight(45.0);
        sword.setPreserveRatio(true);
        sword.setImage(new Image("/Resources/sword.png", true));
        swordPolygon.setLayoutX(x + 56);
        swordPolygon.setLayoutY(y + 32);
        swordPolygon.setFill(Color.TRANSPARENT);
        swordPolygon.getPoints().setAll(
                -32.08332824707031, 42.0,
                -30.083328247070312, 43.5,
                -24.583328247070312, 38.16668701171875,
                15.083328247070312, 75.66668701171875,
                26.25, 79.16668701171875,
                23.416671752929688, 70.5,
                -18.916671752929688, 32.5,
                -14.416671752929688, 27.33331298828125,
                -16.416671752929688, 25.5,
                -22.583328247070312, 30.33331298828125,
                -37.41667175292969, 18.5,
                -39.41667175292969, 14.66668701171875,
                -41.416656494140625, 16.33331298828125,
                -43.75, 18.5,
                -39.41667175292969, 21.33331298828125,
                -26.25, 34.33331298828125);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(sword);
        anchorPane.getChildren().add(swordPolygon);
    }

    public ImageView getSword() {
        return sword;
    }
    public void setSword(ImageView sword) {
        this.sword = sword;
    }
    public Polygon getSwordPolygon() {
        return swordPolygon;
    }
    public void setSwordPolygon(Polygon swordPolygon) {
        this.swordPolygon = swordPolygon;
    }
    public double getSpeedX() {
        return speedX;
    }
    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }
    public double getSpeedY() {
        return speedY;
    }
    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
