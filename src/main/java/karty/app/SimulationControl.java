package karty.app;

import java.util.Scanner;

public class SimulationControl implements Runnable {
    private Simulation simulation;

    public SimulationControl(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (simulation.isRunning()) {
            String command = scanner.next();
            switch (command) {
                case "p" -> {
                    simulation.pauseSimulation();
                    System.out.println("Можете выбрать скорость: \"1\", \"2\" или \"3\"");
                }
                case "r" -> simulation.resumeSimulation();
                case "s" -> simulation.stopSimulation();
                case "1" -> simulation.setSpeedDelay(1000);
                case "2" -> simulation.setSpeedDelay(500);
                case "3" -> simulation.setSpeedDelay(200);
                default -> System.out.println("Неизвестная комманда");
            }
        }

    }
}
