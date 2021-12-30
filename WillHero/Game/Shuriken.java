package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Shuriken extends Weapon implements Collidable, Cloneable {
    private transient ImageView shuriken;
    private Polygon shurikenPolygon;
    private double speedX, totalDistance;  // Distance across X axis
    private static final double throwSlice = 10;
    private static final double accelerationX = 0.1;
    private boolean thrown;

    Shuriken(double x, double y){
        super(x,y);
        speedX = 0;
        setLevel(1);
        setDamage(30);
        shuriken = new ImageView();
        shurikenPolygon = new Polygon();
        shuriken.setLayoutX(x);
        shuriken.setLayoutY(y);
        shuriken.setFitWidth(70.0);
        shuriken.setFitHeight(56.0);
        shuriken.setPreserveRatio(true);
        shuriken.setImage(new Image("/Resources/shuriken.png", true));
        shurikenPolygon.setLayoutX(x + 36);
        shurikenPolygon.setLayoutY(y + 51);
        shurikenPolygon.setFill(Color.TRANSPARENT);
        shurikenPolygon.getPoints().setAll(
                -19.583328247070312, -49.16668701171875, -17.583328247070312, -34.67498779296875, -12.583328247070312, -28.00830078125, -21.25, -28.00830078125, -36.0, -23.0, -21.25, -16.50830078125, -12.583328247070312, -18.00830078125, -17.583328247070312, -11.5, -19.583328247070312, 5.0, -6.75, -4.50830078125, -3.75, -11.5, -1.0833282470703125, -4.50830078125, 12.25, 5.0, 10.583328247070312, -11.5, 6.25, -18.00830078125, 13.75, -16.50830078125, 27.878326416015625, -23.0, 13.75, -29.67498779296875, 6.25, -29.67498779296875, 10.583328247070312, -34.67498779296875, 12.25, -49.16668701171875, -1.0833282470703125, -40.8416748046875, -3.75, -34.67498779296875, -6.75, -40.8416748046875
        );
        shuriken.setScaleX(0.4);
        shuriken.setScaleY(0.4);
        shurikenPolygon.setScaleX(0.4);
        shurikenPolygon.setScaleY(0.4);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(shuriken);
        anchorPane.getChildren().add(shurikenPolygon);
    }

    public void throwShuriken() {
        shuriken.setLayoutX(shuriken.getLayoutX() + speedX);
        shurikenPolygon.setLayoutX(shurikenPolygon.getLayoutX() + speedX);
    }

    public boolean isThrown() {
        return thrown;
    }

    public void setThrown(boolean thrown) {
        this.thrown = thrown;
    }

    public ImageView getShuriken() {
        return shuriken;
    }
    public void setShuriken(ImageView shuriken) {
        this.shuriken = shuriken;
    }
    public Polygon getShurikenPolygon() {
        return shurikenPolygon;
    }
    public void setShurikenPolygon(Polygon shurikenPolygon) {
        this.shurikenPolygon = shurikenPolygon;
    }
    public double getSpeedX() {
        return speedX;
    }
    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }
    public static double getAccelerationX() {
        return accelerationX;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof bossOrc) {
            return ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(shurikenPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(shurikenPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(shurikenPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(shurikenPolygon.getBoundsInParent());
        }
        return false;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static double getThrowSlice() {
        return throwSlice;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}
