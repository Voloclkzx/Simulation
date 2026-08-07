package karty.placement;

import karty.Units.Entity;
import karty.map.Coordinates;
import karty.map.GameMap;

import java.util.Random;

public class EntityPlacer {
    private Random random;

    public EntityPlacer(GameMap map) {
        this.random = new Random();
    }
    //TODO подумать, как рандомно расставлять
    public void putRandom(Entity entity, GameMap map) {
        int xBound = map.getN();
        int yBound = map.getM();
        int xRandom = random.nextInt(xBound) + 1;
        int yRandom = random.nextInt(yBound) + 1;
        while (true) {
            try {
                map.put(new Coordinates(xRandom, yRandom), entity);
                return;
            } catch (IllegalArgumentException e) {
                xRandom = random.nextInt(xBound) + 1;
                yRandom = random.nextInt(yBound) + 1;
            }
        }



    }
}
