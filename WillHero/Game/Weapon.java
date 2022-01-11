package Game;

import java.io.Serializable;

public abstract class Weapon extends GameObject implements Collidable, Cloneable, Serializable {
    private double damage;
    private int level, weaponType;

    Weapon(double x, double y) {
        super(new Position(x, y));
        weaponType = -1;
    }

    public double getDamage () {
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

    public void upgrade(){
        ++level;
        damage *= 2;
    }

    @Override
    public boolean collision_detected (GameObject gameObject){
        return false; /* Dummy*/
    }
}

