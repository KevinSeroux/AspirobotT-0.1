package pourCommencer;

import pourCommencer.Agent.AgentExploMonoObject;
import pourCommencer.Agent.MentalState;
import pourCommencer.Agent.Robot;
import pourCommencer.Environment.*;
import pourCommencer.Vue.VueTexte;

import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initialisation");

        int manorSize = 10;
        // Object that allows to compute performance measures
        PerformanceCounter perfCounter = new PerfCounterImpl(manorSize);
        // The environment
        Environment env = new Manor(perfCounter, manorSize, new Position(0, 0));
        // The simulator places random dust and jewel in the environment
        //Environment env = new Manor(perfCounter, manorSize, new Position(0, 0));

        EnvSimulator envSimulator = new EnvSimulator(env);
        env.placeJewelAt(new Position(9,9));
        env.placeDustAt(new Position(3,3));

        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = MentalState.class;
        Robot robot = null;
        try {
            Method m = AgentExploMonoObject.class.getMethod("explorationLargeur",parameterTypes);
            //robot = new AgentExploMonoObject(env,m);
            robot = new Robot(env);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
