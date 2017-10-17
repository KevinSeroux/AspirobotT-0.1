package pourCommencer.Agent;

import pourCommencer.Environment.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Robot implements Runnable {
    private EffecteurArm bras;
    private EffecteurAspiration aspiration;
    private EffecteurMouvement mouvement;
    private SensorVision vision;

    public Robot(_Environment env) {
        /* L'agent n'est supposé interagir avec l'environement
         * que par ses capteurs et ses effecteurs */

        this.bras = new EffecteurArm(env);
        this.aspiration = new EffecteurAspiration(env);
        this.mouvement = new EffecteurMouvement(env);
        this.vision = new SensorVision(env);
    }

    @Override
    public void run() {
        stupidRobot();
    }

    private void stupidRobot() {
        Random deplacement = new Random();

        while (true) {
            // Important : Une action par itération

            EnvState state = vision.snapshotState();
            Set<ActionType> actionsPossible = possibleActions(state);
            ActionType action = chooseAction(actionsPossible);
            executeAction(action);

            //TODO a enlever, c'est juste pour les tests (voir ce qu'il se passe
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* L'agent s'interroge ici sur les actions qu'il peut faire.
     * Il trie les actions n'apportant pas d'intérêt */
    private Set<ActionType> possibleActions(EnvState state) {
        Set<ActionType> actionsList = new HashSet<>();
        int envSize = state.getEnvSize();
        Position pos = SensorVision.getAgentPosition(state);

        if(pos.y >= 1)
            actionsList.add(ActionType.MOVE_UP);

        if(pos.y < envSize - 1)
            actionsList.add(ActionType.MOVE_DOWN);

        if(pos.x >= 1)
            actionsList.add(ActionType.MOVE_LEFT);

        if(pos.x < envSize - 1)
            actionsList.add(ActionType.MOVE_RIGHT);

        if(SensorVision.isCaseDirtyAt(state, pos))
            actionsList.add(ActionType.VACUUM_DUST);

        if(SensorVision.doesCaseHaveJewelery(state, pos))
            actionsList.add(ActionType.GATHER_JEWELRY);

        return actionsList;
    }

    private ActionType chooseAction(Set<ActionType> actionsPossible) {
        ActionType action = null;

        // Ramasse les bijoux avant d'aspirer
        if(actionsPossible.contains(ActionType.GATHER_JEWELRY))
            action = ActionType.GATHER_JEWELRY;

        else if(actionsPossible.contains(ActionType.VACUUM_DUST))
            action = ActionType.VACUUM_DUST;

        else {
            // A random move
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int countPossibleActions = actionsPossible.size();
            int actionNumber = random.nextInt(countPossibleActions);

            action = (ActionType)(actionsPossible.toArray())[actionNumber];
        }

        return action;
    }

    private void executeAction(ActionType actionType) {
        switch(actionType) {
            case VACUUM_DUST:
                aspiration.aspirer();
                break;

            case GATHER_JEWELRY:
                bras.ramasser();
                break;

            case MOVE_UP:
                mouvement.moveUp();
                break;

            case MOVE_DOWN:
                mouvement.moveDown();
                break;

            case MOVE_LEFT:
                mouvement.moveLeft();
                break;

            case MOVE_RIGHT:
                mouvement.moveRight();
                break;

            default: // No action
                break;
        }
    }
}
