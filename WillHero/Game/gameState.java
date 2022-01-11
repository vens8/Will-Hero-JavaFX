package Game;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class gameState implements Serializable {
    private String date;
    private Main game;
    private double currentLocationX, currentLocationY;

    // For serialization
    @Serial
    private static final long serialVersionUID = 42L;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Main getGame() {
        return game;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    public double getCurrentLocationX() {
        return currentLocationX;
    }

    public void setCurrentLocationX(double currentLocationX) {
        this.currentLocationX = currentLocationX;
    }

    public double getCurrentLocationY() {
        return currentLocationY;
    }

    public void setCurrentLocationY(double currentLocationY) {
        this.currentLocationY = currentLocationY;
    }
}
