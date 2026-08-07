package karty.Units;

import karty.Utils.PathFinder;
import karty.map.Coordinates;
import karty.map.GameMap;

import java.util.List;
import java.util.Optional;

public class Herbivore extends Creature {
    private static final int SPEED = 2;
    private static final int HP = 10;




    public Herbivore() {
        this.hp = HP;
        this.speed = SPEED;
        this.isAlive = true;
    }

    @Override
    public void makeMove(GameMap map, Coordinates current) {
        Optional<List<Coordinates>> pathOptional = PathFinder.findPath(map, current, entity -> entity instanceof Grass);
        if (pathOptional.isEmpty()) {
            //TODO добавить метод, убежать от хищника
            return;
        }
        List<Coordinates> path = pathOptional.get();
        Coordinates moveTo;
        if (path.size() >= 1 * speed) {
            moveTo = path.get(1*speed - 1);
        } else {
            moveTo = path.getLast();
        }
        if (map.get(moveTo) instanceof Grass) {
            this.hp = hp + 4;
            map.remove(moveTo);
        }
        map.move(current, moveTo);

    }

}
