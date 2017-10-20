package aspirobot;

import aspirobot.agent.AgentExploMonoObject;
import aspirobot.agent.AgentExploPathWithMultiObject;
import aspirobot.agent.MentalState;
import aspirobot.agent.Robot;
import aspirobot.environnement.*;
import aspirobot.vue.VueTexte;

import java.lang.reflect.Method;
import java.util.Scanner;

public class Menu {

    public static void main(String[] args) {
        //Variables determinées
        int manorSize = 10;
        // Object that allows to compute performance measures
        PerformanceCounter perfCounter = new PerfCounterImpl(manorSize);
        // The environment
        Environment env = new Manor(perfCounter, manorSize, new Position(5, 5));
        // The simulator places random dust and jewel in the environment
        //environnement env = new Manor(perfCounter, manorSize, new Position(0, 0));

        EnvSimulator envSimulator = new EnvSimulator(env);

        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = MentalState.class;
        Robot robot = null;

        String entete =     "*****************************************\n" +
                            "*        Bienvenu sur ASPIROBOT         *\n" +
                            "*****************************************\n";
        String menu = "Deux types d'explorations sont dispolibles, lequel souhaitez vous utiliser ?\n" +
                "1 - Exploration non-informée\n" +
                "2 - Exploration informée\n" +
                "Votre choix ? ";

        String menuNonInformeeType = "Deux manières d'explorer de manière non informée ont été réalisées.\n" +
                "1 - Exploration par sous-but simple, de type \"Chercher la poussiere la plus proche\" \nqui va d'un point A à un point B (le plus proche pour réaliser son but)\n" +
                "2 - Exploration dont le but est de maximiser sa mesure de performance,\n en cherchant la suite d'actions lui permettant de gagner le plus de points (via une exploration en largeur)\n" +
                "Votre choix ?";

        String menuNonInformeeOneObject = "Plusieurs algorithmes sont disponibles pour ce type d'exploration.\nLequel souhaitez-vous utiliser ?\n" +
                "1 - Exploration en largeur\n" +
                "2 - Exploration iterative en profondeur\n" +
                "Votre choix ?";


        Scanner sc = new Scanner(System.in);
        Method m = null;
        int choix;
        System.out.println(entete);
        try {
        while (true){
            System.out.println(menu);
            try {
                choix = sc.nextInt();
            }catch (Exception e) {
                sc = new Scanner(System.in);
                continue;
            }
            switch (choix){
                case 1:
                    while (true) {
                        System.out.println(menuNonInformeeType);
                        try {
                            choix = sc.nextInt();
                        }catch (Exception e) {
                            sc = new Scanner(System.in);
                            continue;
                        }
                        switch (choix) {
                            case 1 :
                                while (true) {
                                    System.out.println(menuNonInformeeOneObject);
                                    try {
                                        choix = sc.nextInt();
                                    }catch (Exception e) {
                                        sc = new Scanner(System.in);
                                        continue;
                                    }
                                    switch (choix) {
                                        case 1:
                                            m = AgentExploMonoObject.class.getMethod("explorationLargeur", parameterTypes);
                                            robot = new AgentExploMonoObject(env,m);
                                            break;
                                        case 2:
                                            m = AgentExploMonoObject.class.getMethod("explorationIterativeDeepening", parameterTypes);
                                            robot = new AgentExploMonoObject(env,m);
                                            break;
                                        default:
                                            continue;
                                    }
                                    break;
                                }break;
                            case 2:
                                robot = new AgentExploPathWithMultiObject(env);
                                break;
                            default:
                                continue;
                        }break;
                    }break;
                case 2:
                    robot = new Robot(env);
                    break;
                default:
                    continue;
            }break;
        }

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
        System.out.println("vue lancée");

        (new Thread(robot)).start();
        System.out.println("Robot lancé");
    }
}
