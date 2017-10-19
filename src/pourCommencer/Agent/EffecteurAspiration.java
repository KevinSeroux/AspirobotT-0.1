package pourCommencer.Agent;

import pourCommencer.Environment._Environment;

public class EffecteurAspiration {
    _Environment env;

    public EffecteurAspiration(_Environment env) {
        this.env = env;
    }

    public void aspirer() {
        env.agentDoVaccumDust();
    }
}
