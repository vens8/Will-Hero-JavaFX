package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class gameData {
    Random random = new Random();
    private final ImageView princess = new ImageView(new Image("/Resources/player.png", true));
    public gameData() {
        GlobalVariables.gameObjects.add(new mediumPlatform(-18, 208));
        GlobalVariables.gameObjects.add(new smallPlatform(287, 252));
        //GlobalVariables.gameObjects.add(new greenOrc(730, 275)); // Add at the end
        GlobalVariables.gameObjects.add(new weaponChest(700, 211, 0));  // test!
        GlobalVariables.gameObjects.add(new weaponChest(880, 211, 0));  // test!
        GlobalVariables.gameObjects.add(new Coin(930, 236));
        GlobalVariables.gameObjects.add(new Coin(960, 236));
        GlobalVariables.gameObjects.add(new Coin(990, 236));
        GlobalVariables.gameObjects.add(new bigPlatform(555, 224));
        GlobalVariables.gameObjects.add(new smallPlatform(1176, 182));
        GlobalVariables.gameObjects.add(new greenOrc(1628, 292));
        GlobalVariables.gameObjects.add(new mediumPlatform(1477, 211));
        GlobalVariables.gameObjects.add(new smallPlatform(1816, 204));
        GlobalVariables.gameObjects.add(new coinChest(2124, 239, random.nextInt(11) + 5));
        GlobalVariables.gameObjects.add(new smallPlatform(2096, 242));
        GlobalVariables.gameObjects.add(new greenOrc(2518, 276));
        GlobalVariables.gameObjects.add(new mediumPlatform(2402, 194));
        GlobalVariables.gameObjects.add(new greenOrc(2902, 384));
        GlobalVariables.gameObjects.add(new mediumPlatform(2748, 302));
        GlobalVariables.gameObjects.add(new smallPlatform(3115, 271));
        GlobalVariables.gameObjects.add(new redOrc(3567, 296));
        GlobalVariables.gameObjects.add(new smallPlatform(3431, 230));
        GlobalVariables.gameObjects.add(new weaponChest(3759, 207, random.nextInt(2)));
        GlobalVariables.gameObjects.add(new smallPlatform(3728, 209));
        GlobalVariables.gameObjects.add(new greenOrc(4221, 322));
        GlobalVariables.gameObjects.add(new greenOrc(4378, 322));
        GlobalVariables.gameObjects.add(new bigPlatform(4033, 267));
        GlobalVariables.gameObjects.add(new greenOrc(4823, 311));
        GlobalVariables.gameObjects.add(new mediumPlatform(4657, 230));
        GlobalVariables.gameObjects.add(new greenOrc(5124, 350));
        GlobalVariables.gameObjects.add(new smallPlatform(4983, 284));
        GlobalVariables.gameObjects.add(new greenOrc(5374, 309));
        GlobalVariables.gameObjects.add(new smallPlatform(5233, 244));
        GlobalVariables.gameObjects.add(new smallPlatform(5499, 206));
        GlobalVariables.gameObjects.add(new coinChest(5785, 348, random.nextInt(11) + 5));
        GlobalVariables.gameObjects.add(new smallPlatform(5767, 348));
        GlobalVariables.gameObjects.add(new greenOrc(6216, 364));
        GlobalVariables.gameObjects.add(new redOrc(6443, 364));
        GlobalVariables.gameObjects.add(new bigPlatform(6066, 310));
        GlobalVariables.gameObjects.add(new greenOrc(6723, 317));
        GlobalVariables.gameObjects.add(new weaponChest(6778, 247, random.nextInt(2)));
        GlobalVariables.gameObjects.add(new mediumPlatform(6666, 235));
        GlobalVariables.gameObjects.add(new redOrc(7154, 300));
        GlobalVariables.gameObjects.add(new mediumPlatform(6999, 218));
        GlobalVariables.gameObjects.add(new smallPlatform(7311, 316));
        GlobalVariables.gameObjects.add(new redOrc(7628, 355));
        GlobalVariables.gameObjects.add(new smallPlatform(7544, 289));
        GlobalVariables.gameObjects.add(new redOrc(7974, 331));
        GlobalVariables.gameObjects.add(new mediumPlatform(7808, 249));
        GlobalVariables.gameObjects.add(new TNT(8271, 314));
        GlobalVariables.gameObjects.add(new redOrc(8441, 324));
        GlobalVariables.gameObjects.add(new coinChest(8443, 257, random.nextInt(11) + 5));
        GlobalVariables.gameObjects.add(new redOrc(8576, 324));
        GlobalVariables.gameObjects.add(new bigPlatform(8148, 269));
        GlobalVariables.gameObjects.add(new smallPlatform(8723, 217));
        GlobalVariables.gameObjects.add(new smallPlatform(8957, 284));
        GlobalVariables.gameObjects.add(new mediumPlatform(9377, 272));
        GlobalVariables.gameObjects.add(new redOrc(9831, 314));
        GlobalVariables.gameObjects.add(new smallPlatform(9682, 248));
        GlobalVariables.gameObjects.add(new redOrc(10077, 283));
        GlobalVariables.gameObjects.add(new smallPlatform(9930, 216));
        GlobalVariables.gameObjects.add(new weaponChest(10230, 183, random.nextInt(2)));
        GlobalVariables.gameObjects.add(new smallPlatform(10202, 184));
        GlobalVariables.gameObjects.add(new TNT(10501, 357));
        GlobalVariables.gameObjects.add(new redOrc(10834, 370));
        GlobalVariables.gameObjects.add(new TNT(10926, 357));
        GlobalVariables.gameObjects.add(new bigPlatform(10495, 314));
        double scenechanger = 10495;
        GlobalVariables.gameObjects.add(new greenOrc(scenechanger + 696, 346));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 552, 280));
        GlobalVariables.gameObjects.add(new greenOrc(scenechanger + 936, 327));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 782, 263));
        GlobalVariables.gameObjects.add(new coinChest(scenechanger + 1036, 232, random.nextInt(11) + 5));
        GlobalVariables.gameObjects.add(new greenOrc(scenechanger + 1173, 294));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 1028, 230));
        GlobalVariables.gameObjects.add(new greenOrc(scenechanger + 1421, 271));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 1279, 204));
        GlobalVariables.gameObjects.add(new greenOrc(scenechanger + 1673, 238));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 1525, 172));
        GlobalVariables.gameObjects.add(new greenOrc(scenechanger + 1939, 206));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 1795, 141));
        GlobalVariables.gameObjects.add(new weaponChest(scenechanger + 1981, 394, random.nextInt(2)));
        GlobalVariables.gameObjects.add(new redOrc(scenechanger + 2196, 461));
        GlobalVariables.gameObjects.add(new mediumPlatform(scenechanger + 2000, 380));
        GlobalVariables.gameObjects.add(new redOrc(scenechanger + 2528, 422));
        GlobalVariables.gameObjects.add(new mediumPlatform(scenechanger + 2328, 341));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 2626, 321));
        GlobalVariables.gameObjects.add(new redOrc(scenechanger + 3037, 355));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 2882, 290));
        GlobalVariables.gameObjects.add(new TNT(scenechanger + 3215, 396));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 3135, 343));
        GlobalVariables.gameObjects.add(new redOrc(scenechanger + 3449, 359));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 3374, 293));
        GlobalVariables.gameObjects.add(new redOrc(scenechanger + 3803, 325));
        GlobalVariables.gameObjects.add(new redOrc(scenechanger + 3904, 325));
        GlobalVariables.gameObjects.add(new redOrc(scenechanger + 4014, 325));
        GlobalVariables.gameObjects.add(new TNT(scenechanger + 4102, 311));
        GlobalVariables.gameObjects.add(new bigPlatform(scenechanger + 3650, 269));
        GlobalVariables.gameObjects.add(new weaponChest(scenechanger + 4262, 298, random.nextInt(2)));
        GlobalVariables.gameObjects.add(new mediumPlatform(scenechanger + 4220, 282));
        GlobalVariables.gameObjects.add(new coinChest(scenechanger + 4575, 271, random.nextInt(11) + 5));
        GlobalVariables.gameObjects.add(new mediumPlatform(scenechanger + 4530, 256));
        GlobalVariables.gameObjects.add(new smallPlatform(scenechanger + 4925, 266));
        //boss lvl
        GlobalVariables.gameObjects.add(new bossOrc(scenechanger + 5498, 223));
        GlobalVariables.gameObjects.add(new bigPlatform(scenechanger + 5231, 275));
        GlobalVariables.gameObjects.add(new mediumPlatform(scenechanger + 5693, 249));
        //GlobalVariables.gameObjects.add(new princess(scenechanger + 6239, 324)); //fits x=50, y=50, height = width = 48  // Add princess image because no class
        GlobalVariables.gameObjects.add(new bigPlatform(scenechanger + 5876, 275));
        //GlobalVariables.gameObjects.add(new smallPlatform());
    }
}
