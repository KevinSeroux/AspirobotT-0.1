package pourCommencer.Agent;

import pourCommencer.Controler.Action;
import pourCommencer.Controler.Case;
import pourCommencer.Controler.EnvObject;
import pourCommencer.Controler._Controler;

public class EffecteurAspiration {
    _Controler controler;

    public EffecteurAspiration(_Controler controler) {
        this.controler = controler;
    }

    //TODO ajouter mesure de performance ?
    public void aspirer(Case c){
        c.removeEnvObject(EnvObject.DUST);
        c.removeEnvObject(EnvObject.JEWELRY);
        controler.majEnv(Action.ASPIRE,c);
    }
}
