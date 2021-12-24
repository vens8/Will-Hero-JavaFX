package Game;

public abstract class Orc extends GameObject implements Collidable {

    public Orc(double x, double y) {
        super(new Position(x, y));
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
