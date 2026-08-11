package karty;

import karty.actions.Action;
import karty.actions.InitAction;
import karty.map.GameMap;
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
