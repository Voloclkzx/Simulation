package karty.app;

import karty.actions.Action;
import karty.actions.InitAction;
import karty.actions.RestoreGrassAction;
import karty.actions.TurnAction;
import karty.map.GameMap;
import karty.renderer.ConsoleRenderer;
import karty.renderer.Renderer;

import java.util.LinkedList;
import java.util.List;

public class Simulation {
    private GameMap map;
    private Renderer renderer;
    private int moveCount;
    private List<Action> initActions;
    private List<Action> turnActions;

    public Simulation(GameMap map, Renderer renderer, int moveCount) {
        this.map = map;
        this.renderer = renderer;
        this.moveCount = moveCount;
        this.initActions = new LinkedList<>();
        this.turnActions = new LinkedList<>();
    }

    public void nextTurn() {
        for (Action turnAction : turnActions) {
            turnAction.execute();
        }
    }

    public void startSimulation() {
        initActions.add(new InitAction(map));
        turnActions.add(new TurnAction(map));
        turnActions.add(new RestoreGrassAction(map));
        for (Action initAction : initActions) {
            initAction.execute();
        }
        renderer.print(map);
        int count = 0;
        while (count++ < moveCount) {
            nextTurn();
            renderer.print(map);
            System.out.println("Ход: " + count);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



    }

    public void pauseSimulation() {

    }
}
