package karty;

import karty.Units.*;
import karty.actions.Action;
import karty.actions.InitAction;
import karty.map.Coordinates;
import karty.map.GameMap;
import karty.placement.EntityPlacer;
import karty.renderer.ConsoleRenderer;
import karty.renderer.Renderer;

public class TestMain {
    public static void main(String[] args) {
        GameMap map = new GameMap(10,10);
        Renderer renderer = new ConsoleRenderer();
        Action initAction = new InitAction(map);
        initAction.execute();
        renderer.printMap(map);

    }
}
