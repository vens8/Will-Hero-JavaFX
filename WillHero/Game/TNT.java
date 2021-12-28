package Game;

import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class TNT extends GameObject implements Collidable{
    private transient ImageView tntImage;
    private transient ImageView explosionImage;
    private Polygon tntPolygon;
    private Polygon explosionPolygon;
    private boolean activated, explosionActivated;
    private AnchorPane anchorPane;


    TNT(double x, double y) {
        super(new Position(x, y));
        activated = false;
        explosionActivated = false;
        tntImage = new ImageView();
        explosionImage = new ImageView();
        tntPolygon = new Polygon();
        explosionPolygon = new Polygon();
        tntImage.setLayoutX(x);
        tntImage.setLayoutY(y);
        tntImage.setFitWidth(67.0);
        tntImage.setFitHeight(68.0);
        tntImage.setPreserveRatio(true);
        tntImage.setImage(new Image("/Resources/tnt.png", true));
        tntPolygon.setLayoutX(x + 28);
        tntPolygon.setLayoutY(y + 21);
        tntPolygon.setFill(Color.TRANSPARENT);
        tntPolygon.getPoints().setAll(
                -22.91668701171875, 40.82501220703125,
                31.58331298828125, 40.82501220703125,
                31.58331298828125, 34.32501220703125,
                33.91668701171875, 34.32501220703125,
                33.91668701171875, 25.32501220703125,
                31.58331298828125, 25.32501220703125,
                31.58331298828125, -1.841644287109375,
                33.91668701171875, -1.841644287109375,
                33.91668701171875, -10.17498779296875,
                31.58331298828125, -10.17498779296875,
                31.58331298828125, -16.508331298828125,
                -22.91668701171875, -16.508331298828125,
                -22.91668701171875, -10.17498779296875,
                -25.25, -10.17498779296875,
                -25.25, -2.341644287109375,
                -22.91668701171875, -2.341644287109375,
                -22.91668701171875, 27.158355712890625,
                -25.25, 27.158355712890625,
                -25.25, 35.82501220703125,
                -22.91668701171875, 35.82501220703125);

        explosionImage.setLayoutX(x - 83);
        explosionImage.setLayoutY(y - 71);
        explosionImage.setFitWidth(242);
        explosionImage.setFitHeight(231);
        explosionImage.setPreserveRatio(true);
        explosionImage.setImage(new Image("/Resources/explosion.png", true));
        explosionPolygon.setLayoutX(x + 32);
        explosionPolygon.setLayoutY(y + 24);
        explosionPolygon.setFill(Color.TRANSPARENT);
        explosionPolygon.getPoints().setAll(-84.00042724609375, 33.79998779296875,
                -61.60040283203125, 55.39996337890625,
                -29.600425720214844, 75.99996948242188,
                -20.00042724609375, 67.39996337890625,
                -4.2724609375E-4, 67.39996337890625,
                20.79962158203125, 67.39996337890625,
                39.199615478515625, 67.39996337890625,
                50.0, 55.39996337890625,
                63.199615478515625, 45.5999755859375,
                73.59957885742188, 45.5999755859375,
                80.79959106445312, 33.79998779296875,
                89.59957885742188, 7.999977111816406,
                80.79962158203125, -3.0,
                80.79962158203125, -23.0,
                73.599609375, -39.00001525878906,
                50.0, -67.00001525878906,
                30.39959716796875, -74.20002746582031,
                -4.2724609375E-4, -87.80003356933594,
                -29.60040283203125, -74.20002746582031,
                -42.400390625, -56.60002136230469,
                -53.60040283203125, -39.00001525878906,
                -69.60040283203125, -23.0,
                -76.8004150390625, -3.0,
                -76.8004150390625, 16.199981689453125);
        tntPolygon.setScaleX(0.75);
        tntPolygon.setScaleY(0.75);
        tntImage.setScaleX(0.75);
        tntImage.setScaleY(0.75);
        explosionPolygon.setDisable(true);  // Initially should be invisible and non-interactive
        explosionPolygon.setVisible(false);
        explosionImage.setDisable(true); // Initially should be invisible and non-interactive
        explosionImage.setVisible(false);
        explosionImage.setScaleX(0);
        explosionImage.setScaleY(0);
        explosionPolygon.setScaleX(0);
        explosionPolygon.setScaleY(0);
        //tntImage.setVisible(false);
    }

    public void addToScreen(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        anchorPane.getChildren().add(tntImage);
        anchorPane.getChildren().add(tntPolygon);
    }

    public void playTNTAnimation() {
        GlobalVariables.gameAnchorPane.getChildren().removeAll(tntPolygon);  // Remove tnt polygon on contact
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.ZERO, event -> {
            Animations.translateTransition(tntImage, 2, 15, 150, 10, true).play();
            Animations.translateTransition(tntPolygon, 2, 15, 150, 10, true).play();
            Animations.fadeTransition(tntImage, 1, 0.5, 150, 10, true).play();
        }),
                new KeyFrame(Duration.millis(1500), event -> {})
        );
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.ZERO, event -> {
            explosionPolygon.setDisable(false);
            explosionPolygon.setVisible(true);
            explosionImage.setDisable(false);
            explosionImage.setVisible(true);
            GlobalVariables.tntExplosionSound.stop();
            GlobalVariables.tntExplosionSound.play();
            GlobalVariables.gameAnchorPane.getChildren().add(explosionImage);
            GlobalVariables.gameAnchorPane.getChildren().add(explosionPolygon);
            explosionActivated = true;
        }),
                new KeyFrame(Duration.millis(250), new KeyValue(explosionImage.scaleXProperty(), 1)),
                new KeyFrame(Duration.millis(250), new KeyValue(explosionImage.scaleYProperty(),1)),
                new KeyFrame(Duration.millis(250), new KeyValue(explosionPolygon.scaleXProperty(), 1)),
                new KeyFrame(Duration.millis(250), new KeyValue(explosionPolygon.scaleYProperty(), 1))
        );
        Timeline timeline3 = new Timeline(new KeyFrame(Duration.millis(3000), event -> {}));
        Timeline timeline4 = new Timeline(
                new KeyFrame(Duration.millis(1000), new KeyValue(explosionImage.scaleXProperty(), 0)),
                new KeyFrame(Duration.millis(1000), new KeyValue(explosionImage.scaleYProperty(),0)),
                new KeyFrame(Duration.millis(1000), new KeyValue(explosionPolygon.scaleXProperty(), 0)),
                new KeyFrame(Duration.millis(1000), new KeyValue(explosionPolygon.scaleYProperty(), 0)),
                new KeyFrame(Duration.millis(1000), event -> {
                    GlobalVariables.gameAnchorPane.getChildren().removeAll(explosionImage, explosionPolygon, tntImage, tntPolygon);
                    explosionActivated = false;
                })
        );
        SequentialTransition sequentialTransition = new SequentialTransition(timeline1, timeline2, timeline3, timeline4);
        sequentialTransition.play();
    }

    public ImageView getTntImage() {
        return tntImage;
    }
    public void setTntImage(ImageView tntImage) {
        this.tntImage = tntImage;
    }
    public Polygon getTntPolygon() {
        return tntPolygon;
    }
    public void setTntPolygon(Polygon tntPolygon) {
        this.tntPolygon = tntPolygon;
    }

    public ImageView getExplosionImage() {
        return explosionImage;
    }

    public void setExplosionImage(ImageView explosionImage) {
        this.explosionImage = explosionImage;
    }

    public Polygon getExplosionPolygon() {
        return explosionPolygon;
    }

    public void setExplosionPolygon(Polygon explosionPolygon) {
        this.explosionPolygon = explosionPolygon;
    }


    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof mainHero) {
            if (activated) {
                return ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent());
            }
            else {
                return ((mainHero) gameObject).getHeroPolygon().getBoundsInParent().intersects(tntPolygon.getBoundsInParent());
            }

        }
        else if (gameObject instanceof redOrc) {
            if (activated) {
                return ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent());
            }
            else {
                return ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent());
            }
        }
        else if (gameObject instanceof greenOrc) {
            if (activated) {
                return ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent());
            }
            else {
                return ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent());
            }
        }
        else if (gameObject instanceof bossOrc) {
            if (activated) {
                return ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent()) ||
                        ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent());
            }
            else {
                return ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent()) ||
                        ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(tntPolygon.getBoundsInParent());
            }
        }
        else if (gameObject instanceof Sword) {
            if (activated) {
                return ((Sword) gameObject).getSwordPolygon().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent());
            }
            else {
                return ((Sword) gameObject).getSwordPolygon().getBoundsInParent().intersects(tntPolygon.getBoundsInParent());
            }
        }
        else if (gameObject instanceof Shuriken) {
            if (activated) {
                return ((Shuriken) gameObject).getShurikenPolygon().getBoundsInParent().intersects(explosionPolygon.getBoundsInParent());
            }
            else {
                return ((Shuriken) gameObject).getShurikenPolygon().getBoundsInParent().intersects(tntPolygon.getBoundsInParent());
            }
        }
        return false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isExplosionActivated() {
        return explosionActivated;
    }

    public void setExplosionActivated(boolean explosionActivated) {
        this.explosionActivated = explosionActivated;
    }
}