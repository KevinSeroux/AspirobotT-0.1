package pourCommencer.Agent;

import pourCommencer.Agent.Exploration.Edge;
import pourCommencer.Agent.Exploration.Noeud;
import pourCommencer.Environment.*;
import pourCommencer.Excepetion.ExpandActionTypeException;
import pourCommencer.Excepetion.explorationLargeurNotFoundException;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static pourCommencer.Agent.SensorVision.getAgentPosition;

//TODO
class MentalState {
    EnvState beliefs;
    enum Desire { DEFAULT, DUST, JEWEL}
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
        //stupidRobot();
        robotWithExploration();
    }

    private void stupidRobot() {
        // Execute all actions for the first observation
        //TODO pourquoi c'est la et pas dans le while(true), ici c'est stupid robot on devrait même pas avoir detat mental- Max
        MentalState mentalState = buildMentalState();
        while(!mentalState.intentions.isEmpty())
            executeAction(mentalState.intentions.poll());

        while (true) {
            if(doObserve()) //TODO ni d'heuristique
                mentalState = buildMentalState();

            executeAction(mentalState.intentions.poll());
        }
    }


    private void robotWithExploration(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MentalState mentalState = new MentalState();
        while(true) {
            mentalState.beliefs = vision.snapshotState(); //Observation
            mentalState.goal = chooseRandomDesire(mentalState.beliefs); //Choix stupid de but
            if (mentalState.goal != MentalState.Desire.DEFAULT) {
                try {
                    mentalState.intentions = explorationLargeur(mentalState);
                } catch (explorationLargeurNotFoundException e) {
                    e.printStackTrace();
                    continue;
                } catch (ExpandActionTypeException e) {
                    e.printStackTrace();
                    continue;
                }
                //TODO
                while (!mentalState.intentions.isEmpty())
                    executeAction(mentalState.intentions.poll());
            }
        }


    }

    private LinkedList<ActionType> explorationLargeur(MentalState m) throws explorationLargeurNotFoundException, ExpandActionTypeException {
        EnvState e = new EnvState(m.beliefs);
        Position initiale = getAgentPosition(e);
        Noeud origine = new Noeud(null, e,0, 0,initiale); //Position actuelle du robot ?
        origine.getEnvironnement().getCase(initiale).removeEnvObject(EnvObject.ROBOT);
        LinkedList<Noeud> fringe = new LinkedList<Noeud>();
        fringe.add(origine);
        Noeud node = null;
        boolean trouve = false;
        while (true){
            if(fringe.size() == 0){
                trouve = false;
                break; //retunr failure
            }
            node = fringe.removeFirst();
            if(goalTest(m, node)){
                trouve = true;
                break; //return node
            }
            fringe.addAll(expand(node));
        }
        if(trouve){
            LinkedList<ActionType> todo = new LinkedList<>();
            System.out.println("Dernier noeud position "+node.getPositionRobot().x + " "+ node.getPositionRobot().y);
            while(node != origine){
                todo.push(node.getParent().getSuccessor().get(node));
                node = node.getParent();
            }
            for (ActionType a:todo
                 ) {
                System.out.println("Action : "+a);
            }
            return todo;
        }else{
            throw new explorationLargeurNotFoundException();
        }
    }

    private boolean goalTest(MentalState m, Noeud node) {
        //System.out.println("Position : x : " + node.getPositionRobot().x+ ", y : "+node.getPositionRobot().y);
        Case caseCourante = node.getEnvironnement().getCase(node.getPositionRobot());
        if(caseCourante.containsEnvObject(EnvObject.DUST) && m.goal == MentalState.Desire.DUST) return true;
        if(caseCourante.containsEnvObject(EnvObject.JEWELRY) && m.goal == MentalState.Desire.JEWEL) return true;
        return false;
    }

    private Collection<? extends Noeud> expand(Noeud node) throws ExpandActionTypeException {
        LinkedList<Noeud> successors = new LinkedList<>();
        Noeud s;
        Position futurePosition = null;
        for (ActionType a:possibleActionsByPosition(node.getEnvironnement(),node.getPositionRobot())) {
            switch (a) {
                case VACUUM_DUST:
                case GATHER_JEWELRY:
                case VACCUM_JEWELRY:
                    futurePosition = new Position(node.getPositionRobot().x,node.getPositionRobot().y);
                    break;
                case MOVE_UP:
                    futurePosition = new Position(node.getPositionRobot().x-1,node.getPositionRobot().y);
                    break;
                case MOVE_DOWN:
                    futurePosition = new Position(node.getPositionRobot().x+1,node.getPositionRobot().y);
                    break;
                case MOVE_LEFT:
                    futurePosition = new Position(node.getPositionRobot().x,node.getPositionRobot().y-1);
                    break;
                case MOVE_RIGHT:
                    futurePosition = new Position(node.getPositionRobot().x,node.getPositionRobot().y+1);
                    break;
                case NEW_DUST:
                case NEW_JEWELRY:
                    throw new ExpandActionTypeException();
            }
            s = new Noeud(node,node.getEnvironnement(),node.getPathCost() + 1,node.getProfondeur()+1, futurePosition); //TODO remplacer 1 par ActionType.getCoutAction()
            node.addSuccessor(s,a);
            successors.add(s);
        }
        return successors;
    }


    private Set<ActionType> possibleActionsByPosition(EnvState belief, Position p) {
        Set<ActionType> actionsList = new HashSet<>();
        int envSize = belief.getEnvSize();

        if(p.x >= 1)
            actionsList.add(ActionType.MOVE_UP);

        if(p.x < envSize - 1)
            actionsList.add(ActionType.MOVE_DOWN);

        if(p.y >= 1)
            actionsList.add(ActionType.MOVE_LEFT);

        if(p.y < envSize - 1)
            actionsList.add(ActionType.MOVE_RIGHT);

        if(SensorVision.isCaseDirtyAt(belief, p))
            actionsList.add(ActionType.VACUUM_DUST);

        if(SensorVision.doesCaseHaveJewelery(belief, p))
            actionsList.add(ActionType.GATHER_JEWELRY);

        return actionsList;
    }

    private MentalState.Desire chooseRandomDesire(EnvState state){
        /*Random r = new Random(); //TODO - faudrait l'avoir en permanant non ? ou via thread Random - Max
        //TODO Faire ca en regardant si y'a au moins 1 pourssière dans l'env
        if(r.nextInt(100)<70) return MentalState.Desire.DUST;
        //TODO Faire ca en regardant si y'a au moins 1 Jewel dans l'env
        return MentalState.Desire.JEWEL;*/

        if(SensorVision.isThereJewel(state)){
            return MentalState.Desire.JEWEL;
        }

        if (SensorVision.isThereDust(state)){
            return MentalState.Desire.DUST;
        }

        return MentalState.Desire.DEFAULT; //TODO pour moi c'est Do Nothing -Max
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

    //TODO C'est faux ici (inversion x et y)
    /* L'agent s'interroge ici sur les actions qu'il peut faire.
     * Il trie les actions n'apportant pas d'intérêt */
    private Set<ActionType> possibleActions(EnvState belief) {
        Set<ActionType> actionsList = new HashSet<>();
        int envSize = belief.getEnvSize();
        Position pos = getAgentPosition(belief);

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
