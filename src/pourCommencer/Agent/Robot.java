package pourCommencer.Agent;

import pourCommencer.Controler.Action;
import pourCommencer.Controler.Case;
import pourCommencer.Controler.EnvObject;
import pourCommencer.Controler._Controler;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Robot implements Runnable{
    private Case env[][];
    private _Controler controleur;
    private EffecteurArm bras;
    private EffecteurAspiration aspiration;
    private EffecteurMouvement mouvement;
    private SensorVision vision;
    private Case currentCase;

    public Robot( _Controler controleur, Case depart) {
        this.controleur = controleur;
        this.bras = new EffecteurArm(controleur);
        this.aspiration = new EffecteurAspiration(controleur);
        this.mouvement = new EffecteurMouvement(controleur);
        this.vision = new SensorVision(controleur);
        this.currentCase = new Case(depart);
        env = this.vision.getCurrentEnvironement();
        env[depart.x][depart.y].addEnvObject(EnvObject.ROBOT); //TODO je sais pas si c'est utile de gerer ca pour le robot...
    }


    @Override
    public void run() {
        stupidRobot();
    }

    private void stupidRobot() {
        Random deplacement = new Random();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                env=vision.getCurrentEnvironement();
            }
        }, 0, 5000);
        while (true){
            if (vision.caseHaveJewelery(currentCase)){
                bras.ramasser(currentCase);
            }
            if(vision.caseIsDirty(currentCase)){
                aspiration.aspirer(currentCase);
            }
            switch (deplacement.nextInt(4)){
                case 0 : this.currentCase = mouvement.deplacement(env,currentCase, Action.MOVEU); break;
                case 1 : this.currentCase = mouvement.deplacement(env,currentCase, Action.MOVEL); break;
                case 2 : this.currentCase = mouvement.deplacement(env,currentCase, Action.MOVED); break;
                case 3 : this.currentCase = mouvement.deplacement(env,currentCase, Action.MOVER); break;
                default:
                    break;
            }
            //TODO a enlever, c'est juste pour les tests (voir ce qu'il se passe
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
