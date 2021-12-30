package Game;

import java.util.ArrayList;
import java.util.Date;

public class gameState {
    private Date date;
    private Main game;
    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Main getGame() {
        return game;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void setGameObjects(ArrayList<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }
}
