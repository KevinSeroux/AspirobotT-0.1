package pourCommencer.Agent;
import pourCommencer.Environment.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static pourCommencer.Agent.SensorVision.getAgentPosition;

//TODO
class MentalState {
    EnvState beliefs;
    enum Desire { DEFAULT, DUST, JEWEL}
    Desire goal;
    LinkedList<Action> intentions;
}

public class Robot implements Runnable {


    // Count of times the agent has asked himself if he should observe
    private int observationCounter;

    protected static final int PROFONDEUR_MAX = 20;
    protected  _PerformanceCounter perfCounter;
    protected ExplorationFrequency exploFrequency;
    protected EffecteurArm bras;
    protected EffecteurAspiration aspiration;
    protected EffecteurMouvement mouvement;
    protected SensorVision vision;

    public Robot(Environment env) {
        /* L'agent n'est supposé interagir avec l'environement
         * que par ses capteurs et ses effecteurs */

        this.perfCounter = env.getPerfCounter();
        this.exploFrequency = new ExplorationFrequency(0.5);
        this.bras = new EffecteurArm(env);
        this.aspiration = new EffecteurAspiration(env);
        this.mouvement = new EffecteurMouvement(env);
        this.vision = new SensorVision(env);

        this.observationCounter = 0;
    }

    public ExplorationFrequency getExplorationFrequency() {
        return exploFrequency;
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

        // Then, place some observations between actions
        while (true) {
            if(doObserve())
                mentalState = buildMentalState();

            executeAction(mentalState.intentions.poll());

            // Notify the frequency learning system of the new perf
            exploFrequency.addMeasure(perfCounter.get());
        }
    }

    private MentalState buildMentalState() {
        MentalState mentalState = new MentalState();
        mentalState.beliefs = vision.snapshotState();
        mentalState.goal = chooseGoal(mentalState.beliefs);
        Set<Action> actionsPossible = possibleActions(mentalState.beliefs);
        mentalState.intentions = chooseIntentions(mentalState.goal, actionsPossible);

        return mentalState;
    }

    /* If the observation frequency is 0.5 so the agent
     * execute 2 actions for each observation
     */
    private boolean doObserve() {
        boolean doObserve;
        observationCounter++;

        double probabilityObservation =
                observationCounter * exploFrequency.get();

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

    /* The agent asks himself what are the actions he can do.
     * Pointless and impossible actions are sort out */
    private Set<Action> possibleActions(EnvState belief) {
        Set<Action> actionsList = new HashSet<>();
        int envSize = belief.getEnvSize();
        Position pos = getAgentPosition(belief);

        if(pos.x >= 1)
            actionsList.add(Action.MOVE_UP);

        if(pos.x < envSize - 1)
            actionsList.add(Action.MOVE_DOWN);

        if(pos.y >= 1)
            actionsList.add(Action.MOVE_LEFT);

        if(pos.y < envSize - 1)
            actionsList.add(Action.MOVE_RIGHT);

        if(SensorVision.isCaseDirtyAt(belief, pos))
            actionsList.add(Action.VACUUM_DUST);

        if(SensorVision.doesCaseHaveJewelery(belief, pos))
            actionsList.add(Action.GATHER_JEWELRY);

        return actionsList;
    }

    //TODO goal et liste d'intentions
    private LinkedList<Action> chooseIntentions(
            MentalState.Desire goal, Set<Action> actionsPossible
    ) {
        LinkedList<Action> intentions = new LinkedList<>();

        // Ramasse les bijoux avant d'aspirer
        if (actionsPossible.contains(Action.GATHER_JEWELRY))
            intentions.push(Action.GATHER_JEWELRY);

        else if (actionsPossible.contains(Action.VACUUM_DUST))
            intentions.push(Action.VACUUM_DUST);

        else {
            // A random move
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int countPossibleActions = actionsPossible.size();
            int actionNumber = random.nextInt(countPossibleActions);

            intentions.push((Action) (actionsPossible.toArray())[actionNumber]);
        }

        return intentions;
    }


    /* The agent try to perform the given action.
     * The action can fails since the environment has rules.
     * ex: He can hit a wall */
    protected void executeAction(Action action) {
        if(action != null) {
            switch (action) {
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

        // Simulate the time to do one action
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
