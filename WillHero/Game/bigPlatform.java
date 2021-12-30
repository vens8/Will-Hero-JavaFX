package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class bigPlatform extends Platform implements Collidable{
    private transient ImageView bPlatform;
    private Polygon bPlatformPolygon;
    private AnchorPane gameAnchorPane;

    bigPlatform(double x, double y){
        super(x, y);
        bPlatform = new ImageView();
        bPlatformPolygon = new Polygon();
        bPlatform.setLayoutX(x);
        bPlatform.setLayoutY(y);
        bPlatform.setFitWidth(521.0);
        bPlatform.setFitHeight(265.0);
        bPlatform.setPreserveRatio(true);
        bPlatform.setImage(new Image("/Resources/bigPlatform.png", true));
        bPlatformPolygon.setLayoutX(x + 64);
        bPlatformPolygon.setLayoutY(y + 189);
        bPlatformPolygon.setFill(Color.TRANSPARENT);
        bPlatformPolygon.setStroke(Color.BLUE);
        bPlatformPolygon.setStrokeWidth(2);
        bPlatformPolygon.getPoints().setAll(
                440.9166259765625, -84.80001831054688,
                440.9166259765625, -91.5,
                -52.75, -91.5,
                -52.75, -84.80001831054688);
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(bPlatform);
        gameAnchorPane.getChildren().add(bPlatformPolygon);
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(bPlatform);
        gameAnchorPane.getChildren().remove(bPlatformPolygon);
    }

    public Polygon getbPlatformPolygon() {
        return bPlatformPolygon;
    }

    public ImageView getbPlatform() {
        return bPlatform;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
