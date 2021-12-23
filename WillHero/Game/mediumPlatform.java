package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class mediumPlatform extends Platform{
    private transient ImageView mPlatform;
    private Polygon mplatformPolygon;

    mediumPlatform(double x, double y){
        super(x, y);
        mPlatform = new ImageView();
        mplatformPolygon = new Polygon();
        mPlatform.setLayoutX(x);
        mPlatform.setLayoutY(y);
        mPlatform.setFitWidth(265.0);
        mPlatform.setFitHeight(302.0);
        mPlatform.setPreserveRatio(true);
        mPlatform.setImage(new Image("/Resources/mediumPlatform.png", true));
        mplatformPolygon.setLayoutX(128.0);
        mplatformPolygon.setLayoutY(246.0);
        mplatformPolygon.setFill(Color.TRANSPARENT);
        mplatformPolygon.getPoints().setAll(
                -24.74999237060547, 138.66665649414062,
                -122.08332824707031, 135.5,
                -128.0, 131.83334350585938,
                -128.0, 85.33334350585938,
                102.91665649414062, 85.33334350585938,
                102.91665649414062, 111.83334350585938,
                93.25, 111.83334350585938,
                90.08332824707031, 128.5,
                -24.74999237060547, 131.83334350585938);
    }
}
