package karty.utils;

import karty.units.Entity;
import karty.map.Coordinates;
import karty.map.GameMap;

import java.util.*;
import java.util.function.Predicate;

public class PathFinder {

    private static int[][] SHIFT = {
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1},
//            {1, 1},
//            {-1, -1},
//            {1, -1},
//            {-1, 1},
    };

    public static Optional<List<Coordinates>> findPath(GameMap map, Coordinates start, Predicate<Entity> targetCondition) {
        Queue<Coordinates> queue = new ArrayDeque<>();
        Set<Coordinates> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        Map<Coordinates, Coordinates> pathTable = new HashMap<>();
        while (!queue.isEmpty()) {
            Coordinates current = queue.poll();
            for (int i = 0; i < SHIFT.length; i++) {
                Coordinates coordinates = new Coordinates(current.x() + SHIFT[i][0], current.y() + SHIFT[i][1]);
                if (map.isInside(coordinates) && !visited.contains(coordinates)) {
                    Entity entity = map.get(coordinates);
                    if (entity != null && targetCondition.test(entity)) {
                        pathTable.put(coordinates, current);

                        List<Coordinates> path = restorePath(start, coordinates, pathTable);
                        return Optional.of(path);
                    } else if (entity == null) {
                        pathTable.put(coordinates, current);
                        queue.add(coordinates);
                        visited.add(coordinates);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static List<Coordinates> restorePath(Coordinates start, Coordinates coordinates, Map<Coordinates, Coordinates> pathTable) {
        List<Coordinates> path = new LinkedList<>();
        Coordinates currentInPath = coordinates;
        while (!currentInPath.equals(start)) {
            path.add(currentInPath);
            currentInPath = pathTable.get(currentInPath);
        }
        Collections.reverse(path);
        return path;
    }
}

