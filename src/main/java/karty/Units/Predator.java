package karty.Units;

import karty.Utils.PathFinder;
import karty.map.Coordinates;
import karty.map.GameMap;

import java.util.List;
import java.util.Optional;

public class Predator extends Creature {
    private static final int SPEED = 1;
    private static final int HP = 14;
    private static final int DAMAGE = 2;

    private int damage;


    public Predator() {
        this.damage = DAMAGE;
        this.hp = HP;
        this.speed = SPEED;
        this.isAlive = true;
    }

    @Override
    public void makeMove(GameMap map, Coordinates current) {
        Optional<List<Coordinates>> pathOptional = PathFinder.findPath(map, current, entity -> entity instanceof Herbivore);
        if (pathOptional.isEmpty()) {
            return;
        }
        List<Coordinates> path = pathOptional.get();
        Coordinates moveTo = path.get(Math.min(speed, path.size() - 1));
        Entity entity = map.get(moveTo);

        if (entity instanceof Herbivore herbivore) {
            herbivore.loseHp(damage);
            this.hp++;
            if (!herbivore.isAlive()) {
                this.hp = hp + 4;
                map.remove(moveTo);
                map.move(current, moveTo);
            } else if (path.size() > 1) {
                map.move(current, path.get(path.size() - 2));
            }
            return;

        }
        map.move(current, moveTo);
    }


}
