package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Shuriken extends Weapon {
    private transient ImageView shuriken;
    private Polygon shurikenPolygon;
    private double speedX;

    Shuriken(double x, double y){
        super(x,y);
        speedX = 0;
        shuriken = new ImageView();
        shurikenPolygon = new Polygon();
        shuriken.setLayoutX(x);
        shuriken.setLayoutY(y);
        shuriken.setFitWidth(70.0);
        shuriken.setFitHeight(56.0);
        shuriken.setPreserveRatio(true);
        shuriken.setImage(new Image("/Resources/shuriken.png", true));
        shurikenPolygon.setLayoutX(59.0);
        shurikenPolygon.setLayoutY(646.0);
        shurikenPolygon.setFill(Color.TRANSPARENT);
        shurikenPolygon.getPoints().setAll(
                -19.583328247070312, -49.166-17.583328247070312,
                -34.67498779296875, -12.583328247070312,
                -28.00830078125, -21.25,
                -28.00830078125, -36.0,
                -23.0, -21.25,
                -16.50830078125, -12.583328247070312,
                -18.00830078125, -17.583328247070312,
                -11.5, -19.583328247070312,
                5.0, -6.75,
                -4.50830078125, -3.75,
                -11.5, -1.0833282470703125,
                -4.50830078125, 12.25,
                5.0, 10.583328247070312,
                -11.5, 6.25,
                -18.00830078125, 13.75,
                -16.50830078125, 27.878326416015625,
                -23.0, 13.75, -29.67498779296875,
                6.25, -29.67498779296875,
                10.583328247070312, -34.67498779296875,
                12.25, -49.16668701171875,
                -1.0833282470703125, -40.8416748046875,
                -3.75, -34.67498779296875,
                -6.75, -40.84165);
    }
}
