package karty.renderer;

import karty.units.*;
import karty.map.Coordinates;
import karty.map.GameMap;

public class ConsoleRenderer implements Renderer {
    public final static String EMPTY_SPRITE = "⬛";
    public final static String ROCK_SPRITE = "\uD83E\uDEA8";
    public final static String TREE_SPRITE = "\uD83C\uDF33";
    public final static String GRASS_SPRITE = "\uD83C\uDF3F";
    public final static String HERBIVORE_SPRITE = "\uD83D\uDC07";
    public final static String PREDATOR_SPRITE = "\uD83D\uDC3A";

    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    @Override
    public void print(GameMap map) {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
        int grassCount = map.countEntities(Grass.class);
        int herbivoreCount = map.countEntities(Herbivore.class);
        int predatorCount = map.countEntities(Predator.class);
        System.out.println("Трава: " + grassCount);
        System.out.println("Травоядные: " + herbivoreCount);
        System.out.println("Хищники: " + predatorCount);
        printMap(map);
    }

    @Override
    public void printMap(GameMap map) {
        for (int y = 1; y <= map.getM(); y++) {
            for (int x = 1; x <= map.getN(); x++) {
                Entity entity = map.get(new Coordinates(x, y));
                String sprite = spriteFor(entity);
                System.out.print(sprite);
            }
            System.out.println();
        }
        System.out.println();
    }

    private String spriteFor(Entity entity) {
        return switch (entity) {
            case null -> EMPTY_SPRITE;
            case Tree ignored -> TREE_SPRITE;
            case Grass ignored -> GRASS_SPRITE;
            case Herbivore ignored -> HERBIVORE_SPRITE;
            case Predator ignored -> PREDATOR_SPRITE;
            case Rock ignored -> ROCK_SPRITE;
            default -> throw new IllegalArgumentException("Неизвестный объект");
        };
    }


}
