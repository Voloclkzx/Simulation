package karty.actions;

import karty.Units.*;
import karty.map.Coordinates;
import karty.map.GameMap;
import karty.placement.EntityPlacer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class InitAction implements Action {
    private static final int GRASS_PROCENT = 35;
    private static final int ROCK_PROCENT = 4;
    private static final int TREE_PROCENT = 4;
    private static final int HERBIVORE_PROCENT = 8;
    private static final int PREDATOR_PROCENT = 1;

    private GameMap map;
    private int countPlacedEntities = 0;

    public InitAction(GameMap map) {
        this.map = map;
    }

    @Override
    public void execute() {
        List<Coordinates> coordinates = new LinkedList<>();
        for (int y = 1; y <= map.getM(); y++) {
            for (int x = 1; x <= map.getN(); x++) {
                coordinates.add(new Coordinates(x, y));
            }
        }

        Collections.shuffle(coordinates);
        putEntity(coordinates, Grass::new, coordinates.size()*GRASS_PROCENT/100);
        putEntity(coordinates, Rock::new, coordinates.size()*ROCK_PROCENT/100);
        putEntity(coordinates, Tree::new, coordinates.size()*TREE_PROCENT/100);
        putEntity(coordinates, Herbivore::new, coordinates.size()*HERBIVORE_PROCENT/100);
        putEntity(coordinates, Predator::new, coordinates.size()*PREDATOR_PROCENT/100);

    }

    private <T extends Entity> void putEntity(List<Coordinates> coordinates, Supplier<T> factory, int count) {
        int i;
        for (i = countPlacedEntities; i < count + countPlacedEntities; i++) {
            map.put(coordinates.get(i), factory.get());
        }
        countPlacedEntities = i;

    }

//    @Override
//    public void execute() {
//        int positions = map.getM() * map.getN();
//        putRandom(map, Grass::new, GRASS_PROCENT);
//        putRandom(map, Rock::new, ROCK_PROCENT);
//        putRandom(map, Tree::new, TREE_PROCENT);
//        putRandom(map, Herbivore::new, HERBIVORE_PROCENT);
//        putRandom(map, Predator::new, PREDATOR_PROCENT);
//    }
//
//    private <T extends Entity> void putRandom(GameMap map, Supplier<T> factory, int count) {
//        for (int i = 0; i < count; i++) {
//            entityPlacer.putRandom(factory.get(), map);
//        }
//    }





}
