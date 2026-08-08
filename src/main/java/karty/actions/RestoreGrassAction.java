package karty.actions;

import karty.Units.Entity;
import karty.Units.Grass;
import karty.map.Coordinates;
import karty.map.EntityPosition;
import karty.map.GameMap;
import karty.placement.EntityPlacer;

import java.util.Collections;
import java.util.List;

public class RestoreGrassAction implements Action {
    private static final int MIN_GRASS_PROCENT = 28;
    private static final int GRASS_GROWTH_PER_TURN_PROCENT = 2;
    private GameMap map;

    public RestoreGrassAction(GameMap map) {
        this.map = map;
    }

    @Override
    public void execute() {
        int grassCount = 0;
        for (EntityPosition entityPosition : map.getPositions()) {
            Entity entity = entityPosition.entity();
            if (entity instanceof Grass) {
                grassCount++;
            }
        }

        List<Coordinates> emptyCoordinates = map.getEmptyCoordinates();
        Collections.shuffle(emptyCoordinates);
        int positionsCount = map.getN() * map.getM();
        int targetGrassCount = MIN_GRASS_PROCENT * positionsCount / 100;

        if (grassCount < targetGrassCount) {
            int addGrassCount = targetGrassCount - grassCount;
            addGrassCount = Math.min(addGrassCount, positionsCount * GRASS_GROWTH_PER_TURN_PROCENT / 100);
            addGrassCount = Math.min(addGrassCount, emptyCoordinates.size());
            for (int i = 0; i < addGrassCount; i++) {
                map.put(emptyCoordinates.get(i), new Grass());
            }
        }
    }
}
