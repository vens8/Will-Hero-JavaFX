package Game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.sql.Time;

public class Sword extends Weapon {
    private transient ImageView sword;
    private Polygon swordPolygon;
    private double speedX, speedY;
    private boolean used;  // Check if the sword is used

    public Sword(double x, double y) {
        super(x, y);
        speedX = 0;
        speedY = -4;
        setLevel(1);
        setDamage(30);
        used = false;
        sword = new ImageView();
        swordPolygon = new Polygon();
        sword.setLayoutX(x);
        sword.setLayoutY(y);
        sword.setFitWidth(98.0);
        sword.setFitHeight(45.0);
        sword.setPreserveRatio(true);
        sword.setImage(new Image("/Resources/sword.png", true));
        swordPolygon.setLayoutX(x + 55);
        swordPolygon.setLayoutY(y - 31);
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
        sword.setScaleX(0.85);
        sword.setScaleY(0.85);
        sword.setRotate(-180);  // Initial rotation
        swordPolygon.setRotate(-224);
        swordPolygon.setScaleX(0.85);
        swordPolygon.setScaleY(0.85);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(sword);
        anchorPane.getChildren().add(swordPolygon);
    }

    public void useSword() {
        if (GlobalVariables.sound) {
            GlobalVariables.swordKillSound.stop();
            GlobalVariables.swordKillSound.play();
        }
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(sword.rotateProperty(), 45)),
                new KeyFrame(Duration.millis(150), new KeyValue(swordPolygon.rotateProperty(), 0)));
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            sword.setRotate(-180);  // reset to initial rotation position
            swordPolygon.setRotate(-224);
            used = false;  // After the sword is used
        }));
        timeline1.setOnFinished(event -> timeline2.play());
        timeline1.play();
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public ImageView getSword() {
        return sword;
    }
    public Polygon getSwordPolygon() {
        return swordPolygon;
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
        if (gameObject instanceof greenOrc) {
            return ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof redOrc) {
            return ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bossOrc) {
            return ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(swordPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof TNT) {
            return ((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(swordPolygon.getBoundsInParent());
        }
        return false; // Dummy
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
