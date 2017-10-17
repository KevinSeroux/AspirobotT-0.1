package pourCommencer.Agent;

import pourCommencer.Environment.Action;
import pourCommencer.Environment.ActionType;
import pourCommencer.Environment._Environment;

public class EffecteurAspiration {
    _Environment env;

    public EffecteurAspiration(_Environment env) {
        this.env = env;
    }

    //TODO ajouter mesure de performance ?
    public void aspirer() {
        /* Ici on gère pas explicitement le cas où l'agent aspire
         * un bijou, c'est une conséquence de sa volonté d'aspirer
         * de la poussière */
        env.triggerAction(new Action(ActionType.VACUUM_DUST));
    }
}
