package karty.Units;

import karty.Utils.PathFinder;
import karty.map.Coordinates;
import karty.map.GameMap;

import java.util.List;
import java.util.Optional;

public class Predator extends Creature {
    private static final int SPEED = 1;
    private static final int HP = 12;
    private static final int MAX_HP = 18;
    private static final int HP_RESTORE = 3;
    private static final int ENERGY = 8;
    private static final int ENERGY_RESTORE = 5;
    private static final int MAX_ENERGY = 20;
    private static final int REPRODUCTION_THRESHOLD = 18;
    private static final int REPRODUCTION_COST = 14;
    private static final int DAMAGE = 2;

    private int damage;

    public Predator() {
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
        this.damage = DAMAGE;
    }

    @Override
    public void makeMove(GameMap map, Coordinates coordinates) {
        makeMove(map, coordinates, entity -> entity instanceof Herbivore, entity -> entity instanceof Predator, Predator::new);
    }


}
