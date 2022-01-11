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
    private transient Polygon coinChestPolygon;
    private boolean activated, pushed;
    private AnchorPane gameAnchorPane;
    private double speedX;
    private double speedY;
    private double currentJumpHeight;
    private double setY;
    private double jumpHeight;
    private static final double jumpSlice = 0.0001;
    private static final double WEIGHT = 5;
    private static final double accelerationX = -0.0125; // Acceleration of coin chest is 1.5 times acceleration of greenOrc
    private static final double accelerationY = 0.0003;

    public coinChest(double x, double y, int coins) {
        super(x, y);
        speedX = 0;
        speedY = -0.0001;
        currentJumpHeight = 0;
        jumpHeight = -3;
        pushed = false;
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
        coinChestImageView.setScaleX(0.75);
        coinChestImageView.setScaleY(0.75);
        coinChestPolygon.setScaleX(0.75);
        coinChestPolygon.setScaleY(0.75);
        coin = new Coin(x + 70, y + 50);  // Place coin at the center of the chest
        coin.getCoinImage().setDisable(true);  // Initially should be invisible and non-interactive
        coin.getCoinImage().setVisible(false);
        coin.getCoinPolygon().setDisable(true); // Initially should be invisible and non-interactive
        coin.getCoinPolygon().setVisible(false);
        coin.setCoinValue(coins);  // Set the number of coins the player gets
    }

    public void playChestAnimation(Player player) {
        coin.addToScreen(gameAnchorPane);
        if (GlobalVariables.sound) {
            GlobalVariables.coinChestOpenSound.stop();
            GlobalVariables.coinChestOpenSound.play();
        }
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

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(coinChestImageView);
        gameAnchorPane.getChildren().add(coinChestPolygon);
        coinChestImageView.toFront();
    }

    public void removeFromScreen() {
        gameAnchorPane.getChildren().remove(coinChestImageView);
        gameAnchorPane.getChildren().remove(coinChestPolygon);
    }


    public boolean isPushed() {
        return pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public static double getWeight() {
        return WEIGHT;
    }

    public static double getAccelerationX() {
        return accelerationX;
    }

    public void push() {  // move chest
        coinChestImageView.setLayoutX(coinChestImageView.getLayoutX() + speedX);
        coinChestPolygon.setLayoutX(coinChestPolygon.getLayoutX() + speedX);
    }

    public void jump() {
        coinChestImageView.setLayoutY(coinChestImageView.getLayoutY() + speedY);
        coinChestPolygon.setLayoutY(coinChestPolygon.getLayoutY() + speedY);
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getCurrentJumpHeight() {
        return currentJumpHeight;
    }

    public void setCurrentJumpHeight(double currentJumpHeight) {
        this.currentJumpHeight = currentJumpHeight;
    }

    public double getSetY() {
        return setY;
    }

    public void setSetY(double setY) {
        this.setY = setY;
    }
    public double getJumpHeight() {
        return jumpHeight;
    }

    public static double getAccelerationY() {
        return accelerationY;
    }

    public static double getJumpSlice() {
        return jumpSlice;
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
        if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(coinChestPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(coinChestPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bigPlatform) {
            return ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(coinChestPolygon.getBoundsInParent());
        }
        return false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

}
