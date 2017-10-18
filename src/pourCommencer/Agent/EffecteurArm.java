package pourCommencer.Agent;

import pourCommencer.Environment._Environment;

public class EffecteurArm {

    private _Environment env;

    public EffecteurArm(_Environment env) {
        this.env = env;
    }

    public void ramasser() {
        env.agentDoGatherJewel();
    }
}
