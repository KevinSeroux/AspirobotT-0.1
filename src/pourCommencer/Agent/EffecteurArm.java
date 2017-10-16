package pourCommencer.Agent;

import pourCommencer.Controler.Action;
import pourCommencer.Controler.Case;
import pourCommencer.Controler.EnvObject;
import pourCommencer.Controler._Controler;

public class EffecteurArm {

    _Controler controler;

    public EffecteurArm(_Controler controler) {
        this.controler = controler;
    }

    //TODO ajouter mesure de performance pour le robot ?
    public void ramasser(Case c){
        c.removeEnvObject(EnvObject.JEWELRY);
        controler.majEnv(Action.GATHER, c);
    }
}
