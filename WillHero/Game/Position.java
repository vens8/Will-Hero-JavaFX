package Game;

import java.io.Serializable;

public class Position implements Serializable {
    private Double x, y;

    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}

