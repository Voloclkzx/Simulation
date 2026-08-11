package karty.actions;

import karty.units.Creature;
import karty.units.Entity;
import karty.map.Coordinates;
import karty.map.EntityPosition;
import karty.map.GameMap;

import java.util.Collections;
import java.util.List;

public class TurnAction implements Action {
    private GameMap map;

    public TurnAction(GameMap map) {
        this.map = map;
    }

    @Override
    public void execute() {
        List<EntityPosition> entityPositions = map.getPositions();
        Collections.shuffle(entityPositions);
        for (EntityPosition entityPosition : entityPositions) {
            Entity entity = entityPosition.entity();
            Coordinates coordinates = entityPosition.coordinates();
            if (map.get(coordinates) != entity) {
                continue;
            }
            if (entity instanceof Creature creature) {
                hunger(creature);
                if (!creature.isAlive() && !map.isEmpty(entityPosition.coordinates())) {
                    map.remove(entityPosition.coordinates());
                    continue;
                }
                creature.makeMove(map, entityPosition.coordinates());

            }
        }
    }

    private void hunger(Creature creature) {
        creature.loseEnergy(1);
    }
}
