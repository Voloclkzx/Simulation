package karty.map;

import karty.Units.Entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameMap {
    private int n;
    private int m;
    private Map<Coordinates, Entity> entities;
    private Map<Class<? extends Entity>, Integer> entitiesCount;

    public GameMap(int n, int m) {
        this.n = n;
        this.m = m;
        this.entities = new HashMap<>();
        this.entitiesCount = new HashMap<>();
    }

    public void put(Coordinates coordinates, Entity entity) {
        if (isInside(coordinates) && isEmpty(coordinates)) {
            entities.put(coordinates, entity);
            entitiesCount.merge(entity.getClass(), 1, Integer::sum);
        } else {
            throw new IllegalArgumentException("Эта клетка уже занята или находится за пределами карты");
        }
    }

    public boolean isEmpty(Coordinates coordinates) {
        return entities.get(coordinates) == null;
    }

    public boolean isInside(Coordinates coordinates) {
        return (coordinates.getX() >= 1 && coordinates.getX() <= n) && (coordinates.getY() >= 1 && coordinates.getY() <= m);
    }

    public Entity get(Coordinates coordinates) {
        return entities.get(coordinates);
    }

    public Entity remove(Coordinates coordinates) {
        if (!isInside(coordinates)) {
            throw new IllegalArgumentException("Некорректные координаты: вне области доски");
        }
        if (isEmpty(coordinates)) {
            throw new IllegalArgumentException("Клетка, из которой вы хотите удалить объект - пустая");
        }
        Entity entity = get(coordinates);
        entities.remove(coordinates);
        entitiesCount.compute(entity.getClass(), (type, count) -> count == 1 ? null : count - 1);
        return entity;
    }

    public void move(Coordinates from, Coordinates to) {
        if (!isInside(from) || !isInside(to)) {
            throw new IllegalArgumentException("Некорректные координаты: вне области доски");
        }
        if (isEmpty(from)) {
            throw new IllegalArgumentException("Клетка, из которой вы хотите переместить - пустая");
        }
        if (!isEmpty(to)) {
            throw new IllegalArgumentException("Клетка, в которую вы хотите переместить - занята");
        }

        Entity entity = remove(from);
        put(to, entity);
    }

    public List<EntityPosition> getPositions() {
        List<EntityPosition> entityPositions = new LinkedList<>();
        for (Map.Entry<Coordinates, Entity> entry : entities.entrySet()) {
            entityPositions.add(new EntityPosition(entry.getValue(), entry.getKey()));
        }
        return entityPositions;
    }

    public List<Coordinates> getEmptyCoordinates() {
        List<Coordinates> emptyCoordinates = new LinkedList<>();
        for (int x = 1; x <= n; x++) {
            for (int y = 1; y <= m; y++) {
                Coordinates coordinates = new Coordinates(x, y);
                if (this.isEmpty(coordinates)) {
                    emptyCoordinates.add(coordinates);
                }
            }
        }
        return emptyCoordinates;
    }

    public int countEntities(Class<? extends Entity> type) {
        return entitiesCount.entrySet().stream()
                .filter(e -> type.isAssignableFrom(e.getKey()))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }


    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

}
