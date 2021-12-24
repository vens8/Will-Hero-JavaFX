package Game;

public abstract class Chest extends GameObject implements Collidable{
    private int chestType;

    public Chest(double x, double y){
        super(new Position(x, y));
    }

    public int getChestType() {
        return chestType;
    }
    public void setChestType(int chestType) {
        this.chestType = chestType;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        return false; // Dummy
    }
}
