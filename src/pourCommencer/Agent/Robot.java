package pourCommencer.Agent;

import pourCommencer.Environment.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

//TODO
class MentalState {
    EnvState beliefs;
    enum Desire { DEFAULT, }
    Desire goal;
    LinkedList<ActionType> intentions;
}

public class Robot implements Runnable {
    private EffecteurArm bras;
    private EffecteurAspiration aspiration;
    private EffecteurMouvement mouvement;
    private SensorVision vision;

    private double explorationFrequency; // It is learned
    private int observationCounter;

    public Robot(_Environment env) {
        /* L'agent n'est supposé interagir avec l'environement
         * que par ses capteurs et ses effecteurs */

        this.bras = new EffecteurArm(env);
        this.aspiration = new EffecteurAspiration(env);
        this.mouvement = new EffecteurMouvement(env);
        this.vision = new SensorVision(env);

        this.explorationFrequency = 1;
        this.observationCounter = 0;
    }

    @Override
    public void run() {
        stupidRobot();
    }

    private void stupidRobot() {
        // Execute all actions for the first observation
        MentalState mentalState = buildMentalState();
        while(!mentalState.intentions.isEmpty())
            executeAction(mentalState.intentions.poll());

        while (true) {
            if(doObserve())
                mentalState = buildMentalState();

            executeAction(mentalState.intentions.poll());
        }
    }

    private MentalState buildMentalState() {
        MentalState mentalState = new MentalState();

        mentalState.beliefs = vision.snapshotState();
        mentalState.goal = chooseGoal(mentalState.beliefs);
        Set<ActionType> actionsPossible = possibleActions(mentalState.beliefs);
        mentalState.intentions = chooseIntentions(mentalState.goal, actionsPossible);

        return mentalState;
    }

    private boolean doObserve() {
        boolean doObserve;
        observationCounter++;

        double probabilityObservation =
                observationCounter * explorationFrequency;

        if (probabilityObservation >= 1) {
            // Reset the counter
            doObserve = true;
            observationCounter = 0;
        } else {
            doObserve = false;
        }

        return doObserve;
    }

    //TODO
    private MentalState.Desire chooseGoal(EnvState belief) {
        return MentalState.Desire.DEFAULT;
    }

    /* L'agent s'interroge ici sur les actions qu'il peut faire.
     * Il trie les actions n'apportant pas d'intérêt */
    private Set<ActionType> possibleActions(EnvState belief) {
        Set<ActionType> actionsList = new HashSet<>();
        int envSize = belief.getEnvSize();
        Position pos = SensorVision.getAgentPosition(belief);

        if(pos.y >= 1)
            actionsList.add(ActionType.MOVE_UP);

        if(pos.y < envSize - 1)
            actionsList.add(ActionType.MOVE_DOWN);

        if(pos.x >= 1)
            actionsList.add(ActionType.MOVE_LEFT);

        if(pos.x < envSize - 1)
            actionsList.add(ActionType.MOVE_RIGHT);

        if(SensorVision.isCaseDirtyAt(belief, pos))
            actionsList.add(ActionType.VACUUM_DUST);

        if(SensorVision.doesCaseHaveJewelery(belief, pos))
            actionsList.add(ActionType.GATHER_JEWELRY);

        return actionsList;
    }

    //TODO
    private LinkedList<ActionType> chooseIntentions(
            MentalState.Desire goal, Set<ActionType> actionsPossible
    ) {
        LinkedList<ActionType> intentions = new LinkedList<>();

        // Ramasse les bijoux avant d'aspirer
        if(actionsPossible.contains(ActionType.GATHER_JEWELRY))
            intentions.push(ActionType.GATHER_JEWELRY);

        else if(actionsPossible.contains(ActionType.VACUUM_DUST))
            intentions.push(ActionType.VACUUM_DUST);

        else {
            // A random move
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int countPossibleActions = actionsPossible.size();
            int actionNumber = random.nextInt(countPossibleActions);

            intentions.push((ActionType)(actionsPossible.toArray())[actionNumber]);
        }

        return intentions;
    }

    private void executeAction(ActionType actionType) {
        if(actionType != null) {
            switch (actionType) {
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

        // Simule le temps pour faire une action
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
