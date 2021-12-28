package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class smallPlatform extends Platform implements Collidable {
    private transient ImageView sPlatform;
    private Polygon sPlatformPolygon;

    public smallPlatform(double x, double y){
        super(x, y);
        sPlatform = new ImageView();
        sPlatformPolygon = new Polygon();
        sPlatform.setLayoutX(x);
        sPlatform.setLayoutY(y);
        sPlatform.setFitWidth(222.0);
        sPlatform.setFitHeight(339.0);
        sPlatform.setPreserveRatio(true);
        sPlatform.setImage(new Image("/Resources/smallPlatform.png", true));
        sPlatformPolygon.setLayoutX(x + 91);
        sPlatformPolygon.setLayoutY(y + 161);
        sPlatformPolygon.setFill(Color.TRANSPARENT);
        sPlatformPolygon.getPoints().setAll(
                102.583251953125, -54.000030517578125,
                102.58328247070312, -60.5,
                -66.19961547851562, -60.5,
                -66.19964599609375, -54.000030517578125);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(sPlatform);
        anchorPane.getChildren().add(sPlatformPolygon);
    }

    public ImageView getsPlatform() {
        return sPlatform;
    }
    public Polygon getsPlatformPolygon() {
        return sPlatformPolygon;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
