package Game;

public abstract class Weapon extends GameObject {
    private float damage;
    private int level, weaponType;

    Weapon(double x, double y) {
        super(new Position(x, y));
    }

    public float getDamage () {
        return damage;
    }
    public int getLevel () {
        return level;
    }
    public int getWeaponType () {
        return weaponType;
    }
    public void setDamage ( float damage){
        this.damage = damage;
    }
    public void setLevel ( int level){
        this.level = level;
    }
    public void setWeaponType ( int weaponType){
        this.weaponType = weaponType;
    }

    @Override
    public boolean collision_detected (GameObject gameObject){
        return false; /* Dummy*/
    }
}

