package Game;

import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.sql.Time;

public class coinChest extends Chest implements Collidable{
    private Coin coin;
    private transient ImageView coinChestImageView;
    private final Image image1 = new Image("/Resources/cchest1.png", true);
    private final Image image2 = new Image("/Resources/cchest2.png", true);
    private final Image image3 = new Image("/Resources/cchest3.png", true);
    private final Image image4 = new Image("/Resources/cchest4.png", true);
    private final Image image5 = new Image("/Resources/cchest5.png", true);
    private final Image image6 = new Image("/Resources/cchest6.png", true);
    private Polygon coinChestPolygon;
    private boolean activated;

    public coinChest(double x, double y, int coins) {
        super(x, y);
        activated = false;
        coinChestImageView = new ImageView();
        coinChestImageView.setImage(image1);
        coinChestPolygon = new Polygon();
        coinChestImageView.setLayoutX(x);
        coinChestImageView.setLayoutY(y);
        coinChestImageView.setFitWidth(200.0);
        coinChestImageView.setFitHeight(175.0);
        coinChestImageView.setPreserveRatio(true);
        coinChestPolygon.setLayoutX(x + 64.0);
        coinChestPolygon.setLayoutY(y + 68);
        coinChestPolygon.setFill(Color.TRANSPARENT);
        coinChestPolygon.getPoints().setAll(
                -27.95001220703125, 51.219512939453125,
                79.25, 51.219512939453125,
                79.25, -12.274993896484375,
                -18.54998779296875, -12.274993896484375,
                -27.95001220703125, -2.875);
        coin = new Coin(x + 70, y + 50);  // Place coin at the center of the chest
        coin.addToScreen(GlobalVariables.gameAnchorPane);
        coin.getCoinImage().setDisable(true);  // Initially should be invisible and non-interactive
        coin.getCoinImage().setVisible(false);
        coin.getCoinPolygon().setDisable(true); // Initially should be invisible and non-interactive
        coin.getCoinPolygon().setVisible(false);
        coin.setCoinValue(coins);  // Set the number of coins the player gets
    }

    public void playChestAnimation(Player player) {
        GlobalVariables.coinChestOpenSound.stop();
        GlobalVariables.coinChestOpenSound.play();
        Timeline timeline1 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(coinChestImageView.imageProperty(), image1)),
                new KeyFrame(Duration.millis(100), new KeyValue(coinChestImageView.imageProperty(), image2)),
                new KeyFrame(Duration.millis(200), new KeyValue(coinChestImageView.imageProperty(), image3)),
                new KeyFrame(Duration.millis(300), new KeyValue(coinChestImageView.imageProperty(), image4)),
                new KeyFrame(Duration.millis(400), new KeyValue(coinChestImageView.imageProperty(), image5)),
                new KeyFrame(Duration.millis(500), new KeyValue(coinChestImageView.imageProperty(), image6)),
                new KeyFrame(Duration.millis(600), event -> {
                    coin.getCoinImage().setDisable(false);
                    coin.getCoinImage().setVisible(true);
                    coin.getCoinPolygon().setDisable(false);
                    coin.getCoinPolygon().setVisible(true);
                })
        );
        timeline1.setCycleCount(1);
        Animation animation1 = Animations.translateTransition(coin.getCoinImage(), 0, -50, 500, 1, false);
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(coin.getCoinImage().scaleXProperty(), 0f)),
                new KeyFrame(Duration.millis(250), new KeyValue(coin.getCoinImage().fitHeightProperty(), 65)),
                new KeyFrame(Duration.millis(250), new KeyValue(coin.getCoinImage().fitWidthProperty(), 42)));
        timeline2.setCycleCount(1);
        Animation animation2 = Animations.translateTransition(coin.getCoinImage(), 0, -75, 500, 1, false);  // Make coin go up in the air
        Timeline timeline3 = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            coin.getCoinImage().setDisable(true);
            coin.getCoinImage().setVisible(false);
            coin.getCoinPolygon().setDisable(true);
            coin.getCoinPolygon().setVisible(false);
            GlobalVariables.gameAnchorPane.getChildren().removeAll(coin.getCoinImage(), coin.getCoinPolygon());
            // Also remove from gameObjects list
        }));
        timeline3.setCycleCount(1);
        SequentialTransition sequentialTransition = new SequentialTransition (timeline1, animation1, timeline2, animation2, timeline3);
        sequentialTransition.setCycleCount(1);
        sequentialTransition.play();
        sequentialTransition.setOnFinished(event -> player.increaseCoins(coin.getCoinValue()));
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(coinChestImageView);
        anchorPane.getChildren().add(coinChestPolygon);
    }

    public Coin getCoin(){
        return coin;
    }
    public ImageView getCoinChestImageView() {
        return coinChestImageView;
    }
    public Polygon getCoinChestPolygon() {
        return coinChestPolygon;
    }
    public void setCoin(Coin coin) {
        this.coin = coin;
    }
    public void setCoinChestImageView(ImageView coinChestImageView) {
        this.coinChestImageView = coinChestImageView;
    }
    public void setCoinChestPolygon(Polygon coinChestPolygon) {
        this.coinChestPolygon = coinChestPolygon;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

}
