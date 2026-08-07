package karty.Units;

import karty.map.Coordinates;
import karty.map.GameMap;

public abstract class Creature extends Entity {
    protected int speed;
    protected int hp;
    protected boolean isAlive;

    public abstract void makeMove(GameMap map, Coordinates coordinates);

    public void loseHp(int amount) {
        hp -= amount;
        if (hp <= 0) {
            isAlive = false;
        }
    }
    public boolean isAlive() {
        return isAlive;
    }

}
