package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public abstract class Orc extends GameObject {
    private transient ImageView orc;
    private Polygon orcPolygon;
    private double speedY, jumpHeight, health, damage;
    private int weapon, orcType;
    private GameObjectTemplate<GameObject> unlockedWeapons;  // Stores all the weapons unlocked by the player

    public Orc(double x, double y) {
        super(new Position(x, y));
        speedY = -4;
        jumpHeight = -80;
        orc = new ImageView();
        orcPolygon = new Polygon();
        orc.setLayoutX(x);
        orc.setLayoutY(y);
        orc.setFitWidth(45);
        orc.setFitHeight(42);
        orc.setPreserveRatio(true);
        orc.setImage(new Image("/Resources/redOrc.png", true));
        orcPolygon.setLayoutX(73.0);
        orcPolygon.setLayoutY(290.0);
        orcPolygon.setFill(Color.TRANSPARENT);
        //orcPolygon.setStroke(Color.RED);
        //orcPolygon.setStrokeWidth(1);
        orcPolygon.getPoints().setAll(
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
        orcPolygon.setScaleX(0.55);
        orcPolygon.setScaleY(0.55);
    }

    public ImageView getHero() {
        return orc;
    }

    public Polygon getHeroPolygon() {
        return orcPolygon;
    }

    public void addToScreen(AnchorPane anchorPane) {
        anchorPane.getChildren().add(orc);
        anchorPane.getChildren().add(orcPolygon);
    }

    public void manualMove(double x, double y) {
        orc.setLayoutY(orc.getLayoutY() + y);
        orcPolygon.setLayoutY(orcPolygon.getLayoutY() + y);
        orc.setLayoutX(orc.getLayoutX() + x);
        orcPolygon.setLayoutX(orcPolygon.getLayoutX() + x);
    }


    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void jump() {
        orc.setLayoutY(orc.getLayoutY() + speedY);
        orcPolygon.setLayoutY(orcPolygon.getLayoutY() + speedY);
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
