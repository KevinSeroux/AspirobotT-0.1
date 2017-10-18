package pourCommencer.Vue;

import pourCommencer.Agent.Robot;
import pourCommencer.Environment.Environment;
import pourCommencer.Environment.Position;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;

public class VueTexte implements _Vue, Runnable, Observer {

    private Semaphore semaphore;
    private Environment env;
    private Robot robot;

    public VueTexte(Environment env, Robot robot) {
        this.env = env;
        this.robot = robot;
        this.semaphore = new Semaphore(1);
    }

    @Override
    public void run() {
        while(true) {
            try {
                semaphore.acquire();

                double perf = env.getPerfCounter().get();
                double exploFreq = robot.getExplorationFrequency().get();
                boolean isTraining = robot.getExplorationFrequency().isTraining();

                System.out.println(this);
                System.out.println("Performance: " + perf);
                System.out.println("Agent is training: " + isTraining);
                System.out.println("Agent exploration frequency: " + exploFreq);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        int size = env.getSize();

        String representation = "";
        for (int i = 0; i < size; i++) {
            representation += "-----";
        }
        representation += "\n";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Position pos = new Position(i, j);
                representation += env.getState().getCase(pos) + "|";
            }
            representation += "\n";
            for (int j = 0; j < size; j++) {
                representation += "-----";
            }
            representation += "\n";
        }
        return representation;
    }

    // Débloque le sémaphore pour que run() poursuit
    public void update(Observable o, Object arg) {
        semaphore.release();
    }
}
