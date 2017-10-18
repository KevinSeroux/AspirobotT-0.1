package pourCommencer;

import pourCommencer.Agent.AgentExploStupide;
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
        /*Environment env = new Manor(perfCounter, manorSize, new Position(0, 0));
        env.getState().getCase(new Position(9,9)).addEnvObject(EnvObject.JEWELRY);
        env.getState().getCase(new Position(3,3)).addEnvObject(EnvObject.DUST);*/
        EnvSimulator envSimulator = new EnvSimulator(env);
        Robot robot = new AgentExploStupide(env);
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
