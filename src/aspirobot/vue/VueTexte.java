package aspirobot.vue;

import aspirobot.agent.Robot;
import aspirobot.environnement.Environment;
import aspirobot.Event;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;

public class VueTexte implements _Vue, Runnable, Observer {
    /* The semaphore is inspired from Producer-Consumer pattern
     * except that the producer does not sleep. The consumer is
     * this class */

    private Semaphore semaphore;
    private Environment env;
    private Robot robot;
    private Event event;

    public VueTexte(Environment env, Robot robot) {
        this.env = env;
        this.robot = robot;
        this.semaphore = new Semaphore(1);
        this.event = Event.STARTUP;
    }

    @Override
    public void run() {
        while(true) {
            try {
                semaphore.acquire();

                System.out.println("New event: " + event.toString());
                double perf = env.getPerfCounter().get();
                double exploFreq = robot.getExplorationFrequency().get();
                boolean agentIsTraining = robot.getExplorationFrequency().isTraining();

                System.out.println(env.getStateSnapshot());
                System.out.println("Performance: " + perf);

                if(agentIsTraining)
                    System.out.println("agent is training");

                System.out.println("agent exploration frequency: " + exploFreq);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Débloque le sémaphore pour que run() poursuit
    public void update(Observable o, Object arg) {
        event = (Event)arg;
        semaphore.release();
    }
}
