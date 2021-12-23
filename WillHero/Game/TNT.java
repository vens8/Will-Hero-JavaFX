package Game;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class TNT extends GameObject {
    private float explosionRadius;
    private transient ImageView tntImage;
    private Polygon tntPolygon;

    TNT(double x, double y) {
        super(new Position(x, y));
        tntImage = new ImageView();
        tntPolygon = new Polygon();
        tntImage.setLayoutX(x);
        tntImage.setLayoutY(y);
        tntImage.setFitWidth(67.0); //?
        tntImage.setFitHeight(68.0); //?
        tntImage.setPreserveRatio(true);
        tntImage.setImage(new Image("/Resources/tnt.png", true));
        tntPolygon.setLayoutX(587.0);
        tntPolygon.setLayoutY(286.0);
        tntPolygon.setFill(Color.TRANSPARENT);
        tntPolygon.getPoints().setAll(
                -22.91668701171875, 40.82501220703125,
                31.58331298828125, 40.82501220703125,
                31.58331298828125, 34.32501220703125,
                33.91668701171875, 34.32501220703125,
                33.91668701171875, 25.32501220703125,
                31.58331298828125, 25.32501220703125,
                31.58331298828125, -1.841644287109375,
                33.91668701171875, -1.841644287109375,
                33.91668701171875, -10.17498779296875,
                31.58331298828125, -10.17498779296875,
                31.58331298828125, -16.508331298828125,
                -22.91668701171875, -16.508331298828125,
                -22.91668701171875, -10.17498779296875,
                -25.25, -10.17498779296875,
                -25.25, -2.341644287109375,
                -22.91668701171875, -2.341644287109375,
                -22.91668701171875, 27.158355712890625,
                -25.25, 27.158355712890625,
                -25.25, 35.82501220703125,
                -22.91668701171875, 35.82501220703125);
        tntPolygon.setScaleX(0.55); //?
        tntPolygon.setScaleY(0.55); //?
    }

    public float getExplosionRadius() { return explosionRadius; }
    @Override
    public boolean collision_detected(GameObject gameObject) {        return false; /* Dummy*/    }
}