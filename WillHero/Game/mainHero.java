package Game;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;

public class mainHero extends GameObject implements Collidable {
    private transient ImageView hero;
    private Rectangle heroRectangle;

    public mainHero(double x, double y) {
        super(new Position(x, y));
        hero = new ImageView(new Image("/Resources/player.png", true));
        hero.setX(x);
        hero.setY(y);
        hero.setFitWidth(200);
        hero.setPreserveRatio(true);
        //heroRectangle.setBounds();
    }

    public ImageView getHero() {
        return hero;
    }

    public Rectangle getHeroRectangle() {
        return heroRectangle;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
