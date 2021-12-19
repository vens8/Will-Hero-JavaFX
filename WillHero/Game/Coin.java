package Game;

import javafx.scene.image.ImageView;

public class Coin extends GameObject {
    private int coinValue;
    private transient ImageView coinImage;

    Coin(double x, double y) {
        super(new Position(x, y));
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
    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }

}
