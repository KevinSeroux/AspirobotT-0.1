package aspirobot.agent;

import aspirobot.environnement._Environment;

public class EffecteurAspiration {
    _Environment env;

    public EffecteurAspiration(_Environment env) {
        this.env = env;
    }

    public void aspirer() {
        env.agentDoVaccumDust();
    }
}
