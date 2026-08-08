package karty;

import karty.app.Simulation;
import karty.map.GameMap;
import karty.renderer.ConsoleRenderer;
import karty.renderer.Renderer;

public class Main {
    public static void main(String[] args) {
        //TODO
        /*
        Добавить damage в Predator
         */
        GameMap map = new GameMap(80, 25);
        Renderer renderer = new ConsoleRenderer();
        Simulation simulation = new Simulation(map, renderer, 100000);
        simulation.startSimulation();
    }

}
