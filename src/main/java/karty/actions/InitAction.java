package karty.actions;

import karty.units.*;
import karty.map.Coordinates;
import karty.map.GameMap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class InitAction implements Action {
    private static final int GRASS_PERCENT = 10;
    private static final int ROCK_PERCENT = 5;
    private static final int TREE_PERCENT = 5;
    private static final int HERBIVORE_PERCENT = 7;
    private static final int PREDATOR_PERCENT = 5;

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
        putEntity(coordinates, Grass::new, coordinates.size()* GRASS_PERCENT /100);
        putEntity(coordinates, Rock::new, coordinates.size()* ROCK_PERCENT /100);
        putEntity(coordinates, Tree::new, coordinates.size()* TREE_PERCENT /100);
        putEntity(coordinates, Herbivore::new, coordinates.size()* HERBIVORE_PERCENT /100);
        putEntity(coordinates, Predator::new, coordinates.size()* PREDATOR_PERCENT /100);

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
