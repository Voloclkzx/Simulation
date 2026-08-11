package karty.app;

import karty.actions.Action;
import karty.actions.InitAction;
import karty.actions.RestoreGrassAction;
import karty.actions.TurnAction;
import karty.map.GameMap;
import karty.renderer.Renderer;

import java.util.LinkedList;
import java.util.List;

public class Simulation {
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private GameMap map;
    private Renderer renderer;
    private int moveCount;
    private int delay;
    private List<Action> initActions;
    private List<Action> turnActions;

    public Simulation(GameMap map, Renderer renderer, int moveCount, int delay) {
        this.map = map;
        this.renderer = renderer;
        this.moveCount = moveCount;
        this.initActions = new LinkedList<>();
        this.turnActions = new LinkedList<>();
        this.delay = delay;
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
        Thread watcher = new Thread(new SimulationControl(this));
        watcher.setDaemon(true);
        watcher.start();
        for (Action initAction : initActions) {
            initAction.execute();
        }
        renderer.print(map);
        int count = 0;
        while (running && count < moveCount) {
            if (paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }
            nextTurn();
            renderer.print(map);
            System.out.println("Ход: " + (++count));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Симуляция завершена. Ходов выполнено: " + count);

    }

    public void pauseSimulation() {
        this.paused = true;
    }

    public void resumeSimulation() {
        this.paused = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void stopSimulation() {
        this.running = false;
    }

    public void setSpeedDelay(int amount) {
        this.delay = amount;
    }
}