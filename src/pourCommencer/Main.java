package pourCommencer;

import pourCommencer.Agent.Robot;
import pourCommencer.Environment.*;
import pourCommencer.Vue.VueTexte;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initialisation");

        int manorSize = 10;
        PerformanceCounter perfCounter = new PerfCounterImpl(manorSize);
        Environment env = new Manor(perfCounter, manorSize, new Position(5, 5));
        EnvSimulator envSimulator = new EnvSimulator(env);
        VueTexte vueTexte = new VueTexte(env);
        Robot robot = new Robot(env);

        env.addObserver(vueTexte);

        (new Thread(envSimulator)).start();
        System.out.println("Simulateur d'environnement lancé");

        (new Thread(vueTexte)).start();
        System.out.println("Vue lancée");

        (new Thread(robot)).start();
        System.out.println("Robot lancé");
    }
}
