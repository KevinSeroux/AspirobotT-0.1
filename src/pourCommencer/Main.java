package pourCommencer;

import pourCommencer.Agent.Robot;
import pourCommencer.Environment.*;
import pourCommencer.Vue.VueTexte;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initialisation");

        int manorSize = 10;
        // Object that allows to compute performance measures
        PerformanceCounter perfCounter = new PerfCounterImpl(manorSize);
        // The environment
        Environment env = new Manor(perfCounter, manorSize, new Position(5, 5));
        // The simulator places random dust and jewel in the environment
        EnvSimulator envSimulator = new EnvSimulator(env);
        Robot robot = new Robot(env);
        // The console GUI
        VueTexte vueTexte = new VueTexte(env, robot);

        // The GUI is notified when the environment changes
        env.addObserver(vueTexte);

        (new Thread(envSimulator)).start();
        System.out.println("Simulateur d'environnement lancé");

        (new Thread(vueTexte)).start();
        System.out.println("Vue lancée");

        (new Thread(robot)).start();
        System.out.println("Robot lancé");
    }
}
