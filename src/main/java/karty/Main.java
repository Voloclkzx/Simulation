package karty;

import karty.app.Simulation;
import karty.map.GameMap;
import karty.renderer.ConsoleRenderer;
import karty.renderer.Renderer;

public class Main {
    public static void main(String[] args) {
        GameMap map = new GameMap(50, 20);
        Renderer renderer = new ConsoleRenderer();
        Simulation simulation = new Simulation(map, renderer, 1000, 500);
        simulation.startSimulation();
    }

}
