package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class mediumPlatform extends Platform implements Collidable{
    private final transient ImageView mPlatform;
    private final Polygon mPlatformPolygon;

    mediumPlatform(double x, double y){
        super(x, y);
        mPlatform = new ImageView();
        mPlatformPolygon = new Polygon();
        mPlatform.setLayoutX(x);
        mPlatform.setLayoutY(y);
        mPlatform.setFitWidth(265.0);
        mPlatform.setFitHeight(302.0);
        mPlatform.setPreserveRatio(true);
        mPlatform.setImage(new Image("/Resources/mediumPlatform.png", true));
        mPlatformPolygon.setLayoutX(x + 146);
        mPlatformPolygon.setLayoutY(y + 38);
        mPlatformPolygon.setFill(Color.TRANSPARENT);
        mPlatformPolygon.getPoints().setAll(
                -134.6003875732422, 105.80001831054688,
                -134.6003875732422, 85.33334350585938,
                102.91665649414062, 85.33334350585938,
                102.91665649414062, 105.80001831054688);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(mPlatform);
        anchorPane.getChildren().add(mPlatformPolygon);
    }

    public ImageView getmPlatform() {
        return mPlatform;
    }

    public Polygon getmPlatformPolygon() {
        return mPlatformPolygon;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false;
    }
}
