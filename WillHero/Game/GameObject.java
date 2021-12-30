package Game;

import java.io.Serializable;

public abstract class GameObject implements Collidable, Serializable {
    private Position P;
    private boolean added;  // Check if the game object has been added to the list (screen)

    public GameObject(Position P) {
        setP(P);
        added = false;
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

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }
}
