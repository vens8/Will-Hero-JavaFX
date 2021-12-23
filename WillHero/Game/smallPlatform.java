package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class smallPlatform extends Platform{
    private transient ImageView sPlatform;
    private Polygon splatformPolygon;

    public smallPlatform(double x, double y){
        super(x, y);
        sPlatform = new ImageView();
        splatformPolygon = new Polygon();
        sPlatform.setLayoutX(x);
        sPlatform.setLayoutY(y);
        sPlatform.setFitWidth(222.0);
        sPlatform.setFitHeight(339.0);
        sPlatform.setPreserveRatio(true);
        sPlatform.setImage(new Image("/Resources/smallPlatform.png", true));
        splatformPolygon.setLayoutX(378.0);
        splatformPolygon.setLayoutY(421.0);
        splatformPolygon.setFill(Color.TRANSPARENT);
        splatformPolygon.getPoints().setAll(
                -54.583343505859375, 0.5,
                -27.583343505859375, 2.666656494140625,
                -27.583343505859375, 14.0,
                94.91668701171875, 10.5,
                96.75, -20.5,
                102.58331298828125, -20.5,
                102.58331298828125, -55.0,
                98.91668701171875, -60.5,
                -59.583343505859375, -62.166656494140625,
                -62.916656494140625, -57.166656494140625,
                -62.916656494140625, -34.166656494140625,
                -59.583343505859375, -30.666656494140625);
    }
}
