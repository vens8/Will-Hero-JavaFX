package Game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class weaponChest extends Chest implements Collidable{
    private Sword sword;
    private Shuriken shuriken;
    private int weaponType;
    private transient ImageView weaponChestImageView;
    private Image image1 = new Image("/Resources/wchest1.png", true);
    private Image image2 = new Image("/Resources/wchest2.png", true);
    private Image image3 = new Image("/Resources/wchest3.png", true);
    private Image image4 = new Image("/Resources/wchest4.png", true);
    private Image image5 = new Image("/Resources/wchest5.png", true);
    private Image image6 = new Image("/Resources/wchest6.png", true);
    private Polygon weaponChestPolygon;
    private double resize = 0;

    public weaponChest(double x, double y, int weaponType) {
        super(x, y);
        weaponChestImageView = new ImageView();
        weaponChestImageView.setImage(image1);
        weaponChestPolygon = new Polygon();
        weaponChestImageView.setLayoutX(x);
        weaponChestImageView.setLayoutY(y);
        weaponChestImageView.setFitWidth(200.0);
        weaponChestImageView.setFitHeight(175.0);
        weaponChestImageView.setPreserveRatio(true);
        weaponChestPolygon.setLayoutX(x + 64);
        weaponChestPolygon.setLayoutY(y + 68);
        weaponChestPolygon.setFill(Color.TRANSPARENT);
        weaponChestPolygon.getPoints().setAll(
                -27.95001220703125, 51.219512939453125,
                79.25, 51.219512939453125,
                79.25, -12.274993896484375,
                -18.54998779296875, -12.274993896484375,
                -27.95001220703125, -2.875);
        this.weaponType = weaponType;
        if (this.weaponType == 1) {
            sword = new Sword(x, y);
            shuriken = null;
            sword.getSwordPolygon().setDisable(true);  // Initially should be invisible and non-interactive
            sword.getSwordPolygon().setVisible(false);
            sword.getSword().setDisable(true); // Initially should be invisible and non-interactive
            sword.getSword().setVisible(false);
        }
        else {
            shuriken = new Shuriken(x, y);
            sword = null;
            shuriken.getShuriken().setDisable(true);  // Initially should be invisible and non-interactive
            shuriken.getShuriken().setVisible(false);
            shuriken.getShurikenPolygon().setDisable(true); // Initially should be invisible and non-interactive
            shuriken.getShurikenPolygon().setVisible(false);
        }
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(weaponChestImageView);
        anchorPane.getChildren().add(weaponChestPolygon);
    }

    public void playChestAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(weaponChestImageView.imageProperty(), image1)),
                new KeyFrame(Duration.millis(100), new KeyValue(weaponChestImageView.imageProperty(), image2)),
                new KeyFrame(Duration.millis(200), new KeyValue(weaponChestImageView.imageProperty(), image3)),
                new KeyFrame(Duration.millis(300), new KeyValue(weaponChestImageView.imageProperty(), image4)),
                new KeyFrame(Duration.millis(400), new KeyValue(weaponChestImageView.imageProperty(), image5)),
                new KeyFrame(Duration.millis(500), new KeyValue(weaponChestImageView.imageProperty(), image6))
        );
        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> playWeaponAnimation());  // On collecting the sword chest, the sword is displayed
        timeline.play();
    }

    public void playResize() {
        if (weaponType == 1) {
            sword.getSwordPolygon().setScaleX(1 + resize);
            sword.getSwordPolygon().setScaleY(1 + resize);
            sword.getSword().setScaleX(1 + resize);
            sword.getSword().setScaleY(1 + resize);
        }
        else {
            shuriken.getShurikenPolygon().setScaleX(1 + resize);
            shuriken.getShurikenPolygon().setScaleY(1 + resize);
            shuriken.getShuriken().setScaleX(1 + resize);
            shuriken.getShuriken().setScaleY(1 + resize);
        }
        resize += 0.05;
    }

    public void playWeaponAnimation() {
        if (weaponType == 1) {
            sword.getSword().setDisable(false);
            sword.getSword().setVisible(true);
            sword.getSwordPolygon().setDisable(false);
            sword.getSwordPolygon().setVisible(true);
            Animations.translateTransition(sword.getSword(), 0, -20, 500, 1, false).play();  // Lift weapon in the air
            Animations.translateTransition(sword.getSwordPolygon(), 0, -20, 500, 1, false).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), event -> playResize()));
            timeline.setCycleCount(1);
            timeline.play();
            Animations.translateTransition(sword.getSword(), 500, -100, 500, 1, false).play();  // Make sword go to the screen corner
            Animations.translateTransition(sword.getSwordPolygon(), 500, -100, 500, 1, false).play();
            sword.getSwordPolygon().setDisable(true);
            sword.getSwordPolygon().setVisible(false);
            sword.getSword().setDisable(true);
            sword.getSwordPolygon().setVisible(false);
        }
        else {
            shuriken.getShuriken().setDisable(false);
            shuriken.getShuriken().setVisible(true);
            shuriken.getShurikenPolygon().setDisable(false);
            shuriken.getShurikenPolygon().setVisible(true);
            Animations.translateTransition(shuriken.getShuriken(), 0, -20, 500, 1, false).play();  // Lift weapon in the air
            Animations.translateTransition(sword.getSwordPolygon(), 0, -20, 500, 1, false).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), event -> playResize()));
            timeline.setCycleCount(1);
            timeline.setOnFinished(event -> resize = 0);
            timeline.play();
            Animations.translateTransition(shuriken.getShuriken(), 500, -100, 500, 1, false).play();  // Make sword go to the screen corner
            Animations.translateTransition(sword.getSwordPolygon(), 500, -100, 500, 1, false).play();
            shuriken.getShuriken().setDisable(true);
            shuriken.getShuriken().setVisible(false);
            shuriken.getShurikenPolygon().setDisable(true);
            shuriken.getShurikenPolygon().setVisible(false);
        }

    }

    public Sword getSword() {
        return sword;
    }
    public void setSword(Sword sword) {
        this.sword = sword;
    }
    public Shuriken getShuriken() {
        return shuriken;
    }
    public void setShuriken(Shuriken shuriken) {
        this.shuriken = shuriken;
    }
    public int getWeaponType() {
        return weaponType;
    }
    public void setWeaponType(int weaponType) {
        this.weaponType = weaponType;
    }
    public ImageView getWeaponChestImageView() {
        return weaponChestImageView;
    }
    public void setWeaponChestImageView(ImageView weaponChestImageView) {
        this.weaponChestImageView = weaponChestImageView;
    }
    public Polygon getWeaponChestPolygon() {
        return weaponChestPolygon;
    }
    public void setWeaponChestPolygon(Polygon weaponChestPolygon) {
        this.weaponChestPolygon = weaponChestPolygon;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}

