package karty;

import karty.app.Simulation;
import karty.map.GameMap;
import karty.renderer.ConsoleRenderer;
import karty.renderer.Renderer;

public class Main {
    public static void main(String[] args) {
        //TODO
        /*
        Если цели нет, то объекты стоят на месте
        Нет спавна травы
        Добавить на карту камни и деревья
        Добавить систему голода
         */
        GameMap map = new GameMap(20, 20);
        Renderer renderer = new ConsoleRenderer();
        Simulation simulation = new Simulation(map, renderer, 300);
        simulation.startSimulation();
    }

}
