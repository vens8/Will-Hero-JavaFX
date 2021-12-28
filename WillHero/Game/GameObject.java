package Game;

public abstract class GameObject implements Collidable {
    private Position P;

    public GameObject(Position P) {
        setP(P);
    }

    public void setP(Position p) {
        P = p;
    }

    public Position getP() {
        return P;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
