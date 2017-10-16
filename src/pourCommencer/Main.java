package pourCommencer;

import pourCommencer.Agent.Robot;
import pourCommencer.Controler.Case;
import pourCommencer.Controler.Controler;
import pourCommencer.Vue.VueTexte;

public class Main {
    public static void main(String[] args) {
        Controler controler = new Controler(10, new Case(5,5));
        VueTexte vueTexte = new VueTexte(controler.getEnvironnement());
        Robot robot = new Robot(controler, new Case(5,5));

        controler.addObserver(vueTexte);

        System.out.println("Initialisation");
        (new Thread(controler)).start();
        System.out.println("Controler lancé");
        (new Thread(vueTexte)).start();
        System.out.println("Vue lancée");
        (new Thread(robot)).start();
        System.out.println("Robot lancée");

    }
}
