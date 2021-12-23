package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class bigPlatform extends Platform{
    private transient ImageView BPlatform;
    private Polygon BplatformPolygon;

    bigPlatform(double x, double y){
        super(x, y);
        BPlatform = new ImageView();
        BplatformPolygon = new Polygon();
        BPlatform.setLayoutX(x);
        BPlatform.setLayoutY(y);
        BPlatform.setFitWidth(521.0);
        BPlatform.setFitHeight(265.0);
        BPlatform.setPreserveRatio(true);
        BPlatform.setImage(new Image("/Resources/bigPlatform.png", true));
        BplatformPolygon.setLayoutX(623.0);
        BplatformPolygon.setLayoutY(415.0);
        BplatformPolygon.setFill(Color.TRANSPARENT);
        BplatformPolygon.getPoints().setAll(
                -40.75, -41.0,
                -38.75, -37.5,
                -10.91668701171875, -37.5,
                -8.25, -24.833343505859375,
                231.91668701171875, -28.0,
                237.91668701171875, -62.166656494140625,
                243.75, -62.166656494140625,
                247.25, -22.666656494140625,
                347.91668701171875, -17.833343505859375,
                347.91668701171875, -12.333343505859375,
                400.75, -13.833343505859375,
                421.0833740234375, -13.833343505859375,
                427.25, -69.5,
                440.9166259765625, -69.5,
                440.9166259765625, -88.33334350585938,
                437.25, -91.5,
                -52.75, -91.5,
                -52.75, -67.83334350585938,
                -45.25, -67.83334350585938);
    }
}
