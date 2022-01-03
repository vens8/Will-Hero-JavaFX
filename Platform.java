package Game;

public abstract class Platform extends GameObject {
    public Platform(double x, double y){
        super(new Position(x, y));
    }

    @Override
    public boolean collision_detected (GameObject gameObject) {
        return false;
    }
}
