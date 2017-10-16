package pourCommencer.Vue;

import pourCommencer.Controler.Case;
import pourCommencer.Controler._Controler;

import java.util.Observable;

public class VueTexte implements _Vue, Runnable {

    private Case environnement[][];
    private boolean change = false;
    private double performance = 100;

    public VueTexte(Case e[][]) {
        /*environnement = new  Case[e.length][e.length];
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e.length; j++) {
                environnement[i][j] = new Case(e[i][j]);
            }
        }*/
        environnement = e;
    }

    @Override
    public void run() {
        System.out.println("dmerage vue");
        while (true){
            synchronized (this) {
                if (change) {
                    System.out.println(this);
                    System.out.println("Performance : " + performance);
                    change = false;
                }
            }
        }
    }

    @Override
    public String toString() {
        String representation = "";
        for (int j = 0; j < environnement.length; j++) {
            representation+="-----";
        }
        representation+="\n";
        for (int i = 0; i < environnement.length; i++) {
            for (int j = 0; j < environnement.length; j++) {
                representation+=environnement[i][j] + "|";
            }
            representation+="\n";
            for (int j = 0; j < environnement.length; j++) {
                representation+="-----";
            }
            representation+="\n";
        }
        return representation;
    }

    //TODO peut être vaut il mieux passer l'env via update que de l'avoir en param.
    @Override
    public void update(Observable o, Object arg) {
        synchronized (this){
            change = true;
        }
        /*synchronized (o){
            Case[][] e = ((_Controler) o).getEnvironnement();
            for (int i = 0; i < e.length; i++) {
                for (int j = 0; j < e.length; j++) {
                    environnement[i][j] = new Case(e[i][j]);
                }
            }
        }*/
        performance = ((_Controler) o).getPerformance();
    }
}
