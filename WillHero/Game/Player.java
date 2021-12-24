package Game;

public class Player {
    private int score;
    private Main game;
    private mainHero hero;
    private long coins;
    private boolean resurrected;  // Check if the player restarted the game

    public Player(mainHero hero) {
        this.hero = hero;
        score = 0;
    }

    public void resurrect() {

    }

    public void useCoins() {

    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void increaseScore() {
        this.score += 1;
    }
    public long getCoins() {
        return coins;
    }
    public void increaseCoins(long coins){
        this.coins += coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public mainHero getHero() {
        return hero;
    }
}
