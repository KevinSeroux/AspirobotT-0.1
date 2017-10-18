package pourCommencer.Agent;

import pourCommencer.Environment._Environment;

public class EffecteurArm {

    private _Environment env;

    public EffecteurArm(_Environment env) {
        this.env = env;
    }

    //TODO ajouter mesure de performance pour le robot ?
    public void ramasser() {
        env.agentDoGatherJewel();
    }
}
