package Game;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.image.Image;

public class Coin extends GameObject implements Collidable{
    private int coinValue;
    private transient ImageView coinImage;
    private Polygon coinPolygon;
    private boolean collected;

    Coin(double x, double y) {
        super(new Position(x, y));
        coinImage = new ImageView();
        coinPolygon = new Polygon();
        coinValue = 1;
        collected = false;
        coinImage.setLayoutX(x);
        coinImage.setLayoutY(y);
        coinImage.setFitWidth(25.0);
        coinImage.setFitHeight(24.0);
        coinImage.setPreserveRatio(true);
        coinImage.setImage(new Image("/Resources/coin.png", true));
        coinPolygon.setLayoutX(x + 13);
        coinPolygon.setLayoutY(y + 42);
        coinPolygon.setFill(Color.TRANSPARENT);
        coinPolygon.getPoints().setAll(
                -12.91668701171875, -30.0,
                -9.58331298828125, -21.67498779296875,
                -1.0, -18.508331298828125,
                7.75, -21.67498779296875,
                11.0, -30.0,
                7.75, -38.508331298828125,
                -1.0, -42.0,
                -9.58331298828125, -38.508331298828125);
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(coinImage);
        anchorPane.getChildren().add(coinPolygon);
    }

    public void playCoinAnimation() {
        collected = true;
        GlobalVariables.coinCollectSound.stop();
        GlobalVariables.coinCollectSound.play();
        GlobalVariables.gameAnchorPane.getChildren().removeAll(coinImage, coinPolygon);
    }

    public ImageView getCoinImage() {
        return coinImage;
    }

    public void setCoinValue(int coinValue) {
        this.coinValue = coinValue;
    }

    public int getCoinValue() {
        return coinValue;
    }

    public Polygon getCoinPolygon() {
        return coinPolygon;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {  // Coin can only collide with hero
        return ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(coinPolygon.getBoundsInParent());
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
