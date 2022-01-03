package Game;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class Camera {
    private double offsetX, offsetY;
    public Camera(double x, double y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public void update(mainHero hero, AnchorPane gameAnchorPane, AnchorPane bgAnchorPane) {
        offsetX = ((hero.getHeroPolygon().getLayoutX() - 73) - 200);
        offsetY = (hero.getHeroPolygon().getLayoutY() - 290) * 0.1; // 0.05

        gameAnchorPane.setTranslateX(-offsetX); // Move the gameAnchorPane by player movement.
        gameAnchorPane.setLayoutY(-offsetY);

        bgAnchorPane.setTranslateX(-offsetX * 0.25); // Move the background by a factor by player movement.
        bgAnchorPane.setLayoutY(-offsetY);

    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }
}
