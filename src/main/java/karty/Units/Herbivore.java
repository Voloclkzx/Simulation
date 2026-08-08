package karty.Units;

import karty.map.Coordinates;
import karty.map.GameMap;

public class Herbivore extends Creature {
    private static final int SPEED = 2;
    private static final int HP = 12;
    private static final int MAX_HP = 18;
    private static final int HP_RESTORE = 3;
    private static final int ENERGY = 8;
    private static final int ENERGY_RESTORE = 5;
    private static final int MAX_ENERGY = 20;
    private static final int REPRODUCTION_THRESHOLD = 18;
    private static final int REPRODUCTION_COST = 12;


    public Herbivore() {
        this.speed = SPEED;
        this.hp = HP;
        this.maxHp = MAX_HP;
        this.hpRestore = HP_RESTORE;
        this.energy = ENERGY;
        this.maxEnergy = MAX_ENERGY;
        this.energyRestore = ENERGY_RESTORE;
        this.reproductionThreshold = REPRODUCTION_THRESHOLD;
        this.reproductionCost = REPRODUCTION_COST;
        this.isAlive = true;
    }




    @Override
    public void makeMove(GameMap map, Coordinates coordinates) {
        makeMove(map, coordinates, entity -> entity instanceof Grass, entity -> entity instanceof Herbivore, Herbivore::new);
    }
}
