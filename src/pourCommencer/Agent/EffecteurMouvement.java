package pourCommencer.Agent;

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
        env.agentDoMoveUp();
    }

    public void moveDown() {
        env.agentDoMoveDown();
    }

    public void moveLeft() {
        env.agentDoMoveLeft();
    }

    public void moveRight() {
        env.agentDoMoveRight();
    }
}
