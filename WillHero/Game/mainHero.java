package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

public class mainHero extends GameObject implements Collidable {
    private final transient ImageView hero;
    private final Polygon heroPolygon;
    private double speedX;
    private double speedY;
    private final double jumpHeight;
    private final double leapLength;
    private double health;
    private double currentJumpHeight;
    private double currentLeapLength;
    private double setX;
    private double setY;
    private static final double HEIGHT = 74.925048828125;
    private static final double WIDTH = 76.4000015258789;
    private static final double WEIGHT = 30;  // 30 Note: everything is divided by 60 so far
    private static final double jumpSlice = 0.025;
    private static final double leapSlice = 0.06675;
    private static final double accelerationX = 0.00825;
    private static final double accelerationY = 0.00325;  // Final values & can be accessed from anywhere
//    private static final double HEIGHT = 74.925048828125;
//    private static final double WIDTH = 76.4000015258789;
//    private static final double WEIGHT = 30;  // 30
//    private static final double jumpSlice = 1.5;
//    private static final double leapSlice = 4;
//    private static final double accelerationX = 0.5;
//    private static final double accelerationY = 0.2;  // Final values & can be accessed from anywhere
    private Weapon currentWeapon;
    private boolean leaped;
    private AnchorPane gameAnchorPane;
    private ArrayList<GameObject> unlockedWeapons;  // Stores all the weapons unlocked by the player
    private ArrayList<Shuriken> shurikens;
    private double maxSpeed;

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    private double minSpeed;

    public mainHero(double x, double y) {
        super(new Position(x, y));
        speedX = 0;
        speedY = -0.025;  // Negative Y value means player moves up on the canvas
        currentJumpHeight = 0;
        currentLeapLength = 0;
        jumpHeight = -50;  //-50
        leapLength = 140;  // 140 works best for the locations
        leaped = false;
        hero = new ImageView();
        heroPolygon = new Polygon();
        hero.setLayoutX(x);
        hero.setLayoutY(y);
        hero.setFitWidth(45);
        hero.setFitHeight(42);
        hero.setPreserveRatio(true);
        hero.setImage(new Image("/Resources/player.png", true));
        heroPolygon.setLayoutX(x + 46);
        heroPolygon.setLayoutY(y);
        heroPolygon.setFill(Color.TRANSPARENT);
        heroPolygon.setStroke(Color.RED);  // remove
        heroPolygon.setStrokeWidth(2);  // remove
        heroPolygon.getPoints().setAll(
                -62.55001449584961, 58.92502975463867,
                13.849987030029297, 58.92502975463867,
                13.849989891052246, 51.394344329833984,
                9.25, 42.925018310546875,
                11.449996948242188, 31.125,
                9.25, 31.125,
                9.25, -10.074981689453125,
                11.449996948242188, -16.0,
                2.9227311611175537, -16.000015258789062,
                6.1954569816589355, -10.075000762939453,
                -11.986353874206543, 5.525030612945557,
                -18.35000228881836, 5.5250244140625,
                -30.549999237060547, -10.074981689453125,
                -27.62272834777832, -16.000009536743164,
                -35.35000228881836, -16.0,
                -32.35000228881836, -10.074981689453125,
                -45.7499885559082, 9.21249771118164,
                -45.749996185302734, 48.524993896484375,
                -62.550010681152344, 48.524993896484375
        );
        heroPolygon.setScaleX(0.55);
        heroPolygon.setScaleY(0.55);
        unlockedWeapons = new ArrayList<>();
        shurikens = new ArrayList<>();
    }

    public ImageView getHero() {
        return hero;
    }

    public Polygon getHeroPolygon() {
        return heroPolygon;
    }

    public void addToScreen(AnchorPane gameAnchorPane) {
        this.gameAnchorPane = gameAnchorPane;
        gameAnchorPane.getChildren().add(hero);
        gameAnchorPane.getChildren().add(heroPolygon);
    }

    public void manualMove(double x, double y) {  // Can remove if not used
        hero.setLayoutY(hero.getLayoutY() + y);
        heroPolygon.setLayoutY(heroPolygon.getLayoutY() + y);
        hero.setLayoutX(hero.getLayoutX() + x);
        heroPolygon.setLayoutX(heroPolygon.getLayoutX() + x);
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public static double getWeight() {
        return WEIGHT;
    }

    public void jump() {
        hero.setLayoutY(hero.getLayoutY() + speedY);
        heroPolygon.setLayoutY(heroPolygon.getLayoutY() + speedY);
        if (currentWeapon != null) {
            if (currentWeapon instanceof Shuriken) {  // Shuriken
                ((Shuriken) currentWeapon).getShuriken().setLayoutY(((Shuriken) currentWeapon).getShuriken().getLayoutY() + speedY);
                ((Shuriken) currentWeapon).getShurikenPolygon().setLayoutY(((Shuriken) currentWeapon).getShurikenPolygon().getLayoutY() + speedY);
            }
            else {  // Sword
                ((Sword) currentWeapon).getSword().setLayoutY(((Sword) currentWeapon).getSword().getLayoutY() + speedY);
                ((Sword) currentWeapon).getSwordPolygon().setLayoutY(((Sword) currentWeapon).getSwordPolygon().getLayoutY() + speedY);
            }
        }
    }

    public void leap() {
        hero.setLayoutX(hero.getLayoutX() + speedX);
        heroPolygon.setLayoutX(heroPolygon.getLayoutX() + speedX);
        if (currentWeapon != null) {
            if (currentWeapon instanceof Shuriken) {  // Shuriken
                ((Shuriken) currentWeapon).getShuriken().setLayoutX(((Shuriken) currentWeapon).getShuriken().getLayoutX() + speedX);
                ((Shuriken) currentWeapon).getShurikenPolygon().setLayoutX(((Shuriken) currentWeapon).getShurikenPolygon().getLayoutX() + speedX);
            }
            else {  // Sword
                ((Sword) currentWeapon).getSword().setLayoutX(((Sword) currentWeapon).getSword().getLayoutX() + speedX);
                ((Sword) currentWeapon).getSwordPolygon().setLayoutX(((Sword) currentWeapon).getSwordPolygon().getLayoutX() + speedX);
            }
        }
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public double getLeapLength() {
        return leapLength;
    }

    @Override
    public boolean collision_detected(GameObject gameObject) {
        if (gameObject instanceof smallPlatform) {
            return ((smallPlatform) gameObject).getsPlatformPolygon().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof mediumPlatform) {
            return ((mediumPlatform) gameObject).getmPlatformPolygon().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bigPlatform) {
            return ((bigPlatform) gameObject).getbPlatformPolygon().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof redOrc) {
            return ((redOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((redOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((redOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((redOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof greenOrc) {
            return ((greenOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((greenOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof bossOrc) {
            return ((bossOrc) gameObject).getLeftRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getTopRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getRightRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent()) ||
                    ((bossOrc) gameObject).getBottomRectangle().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof coinChest) {
            return ((coinChest) gameObject).getCoinChestPolygon().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof weaponChest) {
            return ((weaponChest) gameObject).getWeaponChestPolygon().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof Coin) {
            return ((Coin) gameObject).getCoinPolygon().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }
        else if (gameObject instanceof TNT) {
            return ((TNT) gameObject).getTntPolygon().getBoundsInParent().intersects(heroPolygon.getBoundsInParent());
        }

        return false;
    }

    public double getCurrentLeapLength() {
        return currentLeapLength;
    }

    public void setCurrentLeapLength(double currentLeapLength) {
        this.currentLeapLength = currentLeapLength;
    }

    public double getCurrentJumpHeight() {
        return currentJumpHeight;
    }

    public void setCurrentJumpHeight(double currentJumpHeight) {
        this.currentJumpHeight = currentJumpHeight;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getSetX() {
        return setX;
    }

    public void setSetX(double setX) {
        this.setX = setX;
    }

    public double getSetY() {
        return setY;
    }

    public void setSetY(double setY) {
        this.setY = setY;
    }

    public static double getJumpSlice() {
        return jumpSlice;
    }

    public static double getLeapSlice() {
        return leapSlice;
    }

    public static double getAccelerationX() {
        return accelerationX;
    }

    public static double getAccelerationY() {
        return accelerationY;
    }

    public boolean isLeaped() {
        return leaped;
    }

    public void setLeaped(boolean leaped) {
        this.leaped = leaped;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public ArrayList<GameObject> getUnlockedWeapons() {
        return unlockedWeapons;
    }

    public void addWeapon(Weapon weapon) throws CloneNotSupportedException {
        if (weapon instanceof Shuriken) {
            currentWeapon = new Shuriken(hero.getLayoutX() - 10, hero.getLayoutY());
            if (unlockedWeapons.contains(currentWeapon)) {
                currentWeapon.upgrade();
            }
            ((Shuriken) currentWeapon).addToScreen(gameAnchorPane);
        }
        else {
            currentWeapon = new Sword(hero.getLayoutX(), hero.getLayoutY());
            if (unlockedWeapons.contains(currentWeapon)) {
                currentWeapon.upgrade();
            }
            ((Sword) currentWeapon).addToScreen(gameAnchorPane);
        }
        unlockedWeapons.add(currentWeapon);
    }

    public ArrayList<Shuriken> getShurikens() {
        return shurikens;
    }

    public void addShuriken(Shuriken shuriken) {
        shurikens.add(shuriken);
    }
}
