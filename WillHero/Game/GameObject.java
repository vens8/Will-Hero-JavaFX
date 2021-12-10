package Game;

public class GameObject implements Collidable {
    private Integer ID;
    private Position P;

    public Integer getID() {
        return ID;
    }

    public void setP(Position p) {
        P = p;
    }

    public Position getP() {
        return P;
    }

    public boolean collision_detected(GameObject gameObject) {

        return false; // Dummy
    }
}
