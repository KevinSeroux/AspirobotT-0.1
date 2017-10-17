package pourCommencer.Vue;

import pourCommencer.Environment.Environment;
import pourCommencer.Environment.Position;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;

public class VueTexte implements _Vue, Runnable, Observer {

    private Semaphore semaphore;
    private Environment env;

    public VueTexte(Environment env) {
        this.env = env;
        this.semaphore = new Semaphore(1);
    }

    @Override
    public void run() {
        while(true) {
            try {
                semaphore.acquire();

                System.out.println(this);
                System.out.println("Performance : " + env.getPerfCounter().getPerformance());

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
