package pourCommencer.Agent;

import pourCommencer.Environment.Action;
import pourCommencer.Environment.ActionType;
import pourCommencer.Environment._Environment;

/* On gère pas les collisions ici, le robot a
 * le droit comme tout le monde de se prendre un
 * mur */
public class EffecteurMouvement {
    _Environment env;

    public EffecteurMouvement(_Environment env) {
        this.env = env;
    }

    public void moveUp() {
        env.triggerAction(new Action(ActionType.MOVE_UP));
    }

    public void moveDown() {
        env.triggerAction(new Action(ActionType.MOVE_DOWN));
    }

    public void moveLeft() {
        env.triggerAction(new Action(ActionType.MOVE_LEFT));
    }

    public void moveRight() {
        env.triggerAction(new Action(ActionType.MOVE_RIGHT));
    }
}
