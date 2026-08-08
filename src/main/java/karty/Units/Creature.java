package karty.Units;

import karty.Utils.PathFinder;
import karty.map.Coordinates;
import karty.map.GameMap;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Creature extends Entity {
    private static final int[][] NEIGHBOR_SHIFTS = {
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1},
//            {1, 1},
//            {-1, -1},
//            {1, -1},
//            {-1, 1},
    };
    protected int speed;
    protected int hp;
    protected int maxHp;
    protected int hpRestore;
    protected int energy;
    protected int maxEnergy;
    protected int energyRestore;
    protected int reproductionThreshold;
    protected int reproductionCost;
    protected boolean isAlive;

    public abstract void makeMove(GameMap map, Coordinates coordinates);

    public void loseHp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Количество наносимого урона не должно быть отрицательно");
        }
        hp -= amount;
        if (hp <= 0) {
            isAlive = false;
        }
    }

    public void loseEnergy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Количество уменьшения энергии не должно быть отрицательно");
        }
        energy -= amount;
        if (energy < 0) {
            loseHp(Math.abs(energy));
            energy = 0;
        }
        if (hp <= 0) {
            isAlive = false;
        }
    }

    public void makeMove(GameMap map, Coordinates current, Predicate<Entity> foodTarget, Predicate<Entity> reproductionTarget, Supplier<? extends Creature> creatureFactory) {
        if (canReproduce()) {
            startReproduction(map, current, reproductionTarget, foodTarget, creatureFactory);
        } else {
            eat(map, current, foodTarget);
        }

    }

    private boolean canReproduce() {
        return energy >= reproductionThreshold;
    }

    private void eat(GameMap map, Coordinates current, Predicate<Entity> foodTarget) {
        Optional<List<Coordinates>> pathOptional = PathFinder.findPath(map, current, foodTarget);
        if (pathOptional.isEmpty()) {
            return;
        }
        List<Coordinates> path = pathOptional.get();
        int steps = Math.min(speed, path.size());
        Coordinates destination = path.get(steps - 1);

        Entity target = map.get(destination);

        if (target != null && foodTarget.test(target)) {
            boolean destinationIsFree = consumeFood(map, destination, target);
            if (destinationIsFree) {
                map.move(current, destination);
            }
            return;
        }
        map.move(current, destination);

    }

    protected boolean consumeFood(GameMap map, Coordinates coordinates, Entity target) {
        map.remove(coordinates);
        restoreHp();
        restoreEnergy();
        return true;
    }

    protected void restoreEnergy() {
        energy = Math.min(maxEnergy, energy + energyRestore);
    }
    protected void restoreEnergy(int amount) {
        energy = Math.min(maxEnergy, energy + amount);
    }

    protected void restoreHp() {
        hp = Math.min(maxHp, hp + hpRestore);
    }
    protected void restoreHp(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    private void startReproduction(GameMap map, Coordinates current, Predicate<Entity> mateTarget, Predicate<Entity> foodTarget, Supplier<? extends Creature> creatureFactory) {
        Optional<List<Coordinates>> pathOptional = PathFinder.findPath(map, current, mateTarget);
        if (pathOptional.isEmpty()) {
            eat(map, current, foodTarget);
            return;
        }
        List<Coordinates> path = pathOptional.get();

        Coordinates mateCoordinates = path.getLast();
        Entity entity = map.get(mateCoordinates);

        if (!(entity instanceof Creature mate)
                || !mateTarget.test(mate)
                || !mate.canReproduce()) {
            eat(map, current, foodTarget);
            return;
        }
        if (path.size() == 1) {
            reproduce(map, current, mate, creatureFactory);
            return;
        }
        int steps = Math.min(speed, path.size() - 1);
        Coordinates destination = path.get(steps - 1);

        map.move(current, destination);
        if (steps == path.size() - 1) {
            reproduce(map, destination, mate, creatureFactory);
        }
    }

    private void reproduce(GameMap map, Coordinates parentCoordinates, Creature mate, Supplier<? extends Creature> creatureFactory) {
        for (int[] shift : NEIGHBOR_SHIFTS) {
            Coordinates childCoordinates = new Coordinates(
                    parentCoordinates.getX() + shift[0],
                    parentCoordinates.getY() + shift[1]
            );

            if (map.isInside(childCoordinates) && map.isEmpty(childCoordinates)) {
                map.put(childCoordinates, creatureFactory.get());

                loseEnergy(reproductionCost);
                mate.loseEnergy(mate.reproductionCost);
                return;
            }
        }
    }

    public boolean isAlive() {
        return isAlive;
    }
}
