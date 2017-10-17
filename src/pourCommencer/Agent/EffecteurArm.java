package pourCommencer.Agent;

import pourCommencer.Environment.Action;
import pourCommencer.Environment.ActionType;
import pourCommencer.Environment._Environment;

public class EffecteurArm {

    private _Environment env;

    public EffecteurArm(_Environment env) {
        this.env = env;
    }

    //TODO ajouter mesure de performance pour le robot ?
    public void ramasser() {
        env.triggerAction(new Action(ActionType.GATHER_JEWELRY));
    }
}
