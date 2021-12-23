package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class mainHero extends GameObject implements Collidable {
    private transient ImageView hero;
    private Polygon heroPolygon;
    private double speedX, speedY, jumpHeight, leapLength, health;
    public static final double HEIGHT = 74.925048828125, WIDTH = 76.4000015258789, WEIGHT = 20;  // Final values, can be accessed from anywhere
    private int weapon;
    private GameObjectTemplate<GameObject> unlockedWeapons;  // Stores all the weapons unlocked by the player

    public mainHero(double x, double y) {
        super(new Position(x, y));
        speedX = 0;
        speedY = -4;  // Negative Y value means player moves up on the canvas
        jumpHeight = -40;
        leapLength = 200;
        hero = new ImageView();
        heroPolygon = new Polygon();
        hero.setLayoutX(x);
        hero.setLayoutY(y);
        hero.setFitWidth(45);
        hero.setFitHeight(42);
        hero.setPreserveRatio(true);
        hero.setImage(new Image("/Resources/player.png", true));
        heroPolygon.setLayoutX(73);
        heroPolygon.setLayoutY(290);
        heroPolygon.setFill(Color.TRANSPARENT);
        heroPolygon.setStroke(Color.RED);  // remove
        heroPolygon.setStrokeWidth(2);  // remove
        heroPolygon.getPoints().setAll(
                -62.55001449584961, 58.92502975463867,
                13.849987030029297, 58.92502975463867,
                13.849989891052246, 51.394344329833984,
                9.25, 42.925018310546875,
                11.449996948242188, 31.125,
                9.25, 31.125,
                9.25, -10.074981689453125,
                11.449996948242188, -16.0,
                2.9227311611175537, -16.000015258789062,
                6.1954569816589355, -10.075000762939453,
                -11.986353874206543, 5.525030612945557,
                -18.35000228881836, 5.5250244140625,
                -30.549999237060547, -10.074981689453125,
                -27.62272834777832, -16.000009536743164,
                -35.35000228881836, -16.0,
                -32.35000228881836, -10.074981689453125,
                -45.7499885559082, 9.21249771118164,
                -45.749996185302734, 48.524993896484375,
                -62.550010681152344, 48.524993896484375
        );
        heroPolygon.setScaleX(0.55);
        heroPolygon.setScaleY(0.55);
    }

    public ImageView getHero() {
        return hero;
    }

    public Polygon getHeroPolygon() {
        return heroPolygon;
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(hero);
        anchorPane.getChildren().add(heroPolygon);
    }

    public void manualMove(double x, double y) {
        hero.setLayoutY(hero.getLayoutY() + y);
        heroPolygon.setLayoutY(heroPolygon.getLayoutY() + y);
        hero.setLayoutX(hero.getLayoutX() + x);
        heroPolygon.setLayoutX(heroPolygon.getLayoutX() + x);
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getWeight() {
        return WEIGHT;
    }

    public void jump() {
        hero.setLayoutY(hero.getLayoutY() + speedY);
        heroPolygon.setLayoutY(heroPolygon.getLayoutY() + speedY);
    }

    public void leap() {
        hero.setLayoutX(hero.getLayoutX() + speedX);
        heroPolygon.setLayoutX(heroPolygon.getLayoutX() + speedX);
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public double getLeapLength() {
        return leapLength;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
