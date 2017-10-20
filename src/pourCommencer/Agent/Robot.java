package pourCommencer.Agent;
import pourCommencer.Agent.Exploration.Noeud;
import pourCommencer.Environment.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;
import static pourCommencer.Agent.SensorVision.getAgentPosition;
import static pourCommencer.Config.actionTime;

/**
 * Classe facade implementant le minimum pour que l'agent vive
 * Pour des algorithmes plus avancer, se repporter aux classes filles
 */
public class Robot implements Runnable {


    // Count of times the agent has asked himself if he should observe
    private int observationCounter;

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
        //stupidRobot();
        //robotWithExploration();
        robotInforme();
    }

    /**
     * Agent stupide qui se déplace sans but de manière aléatoire
     * et qui ramasse ce qui est sur son chemin
     */
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

    private void robotInforme() {

        MentalState mentalState = new MentalState();
        while(true) {
            mentalState.beliefs = vision.snapshotState(); //Observation

            EnvState env = new EnvState(mentalState.beliefs);
            Noeud origine = new Noeud(null, env,0, 0,getAgentPosition(env));

            Noeud goal = findBestGoal(env,origine);
            mentalState.intentions = BuildTree(mentalState,origine,env,goal);

            while (!mentalState.intentions.isEmpty())
                executeAction(mentalState.intentions.poll());

        }


    }

    public LinkedList<Action> BuildTree(MentalState m, Noeud origine, EnvState e1,Noeud goal)
    {
        //Noeud goal = findBestGoal(e1,origine);
        LinkedList<Action> out = new LinkedList<Action>();
        EnvState e = new EnvState(e1);
        Noeud node = origine;
        EnvState enew = null;
        Noeud newNode = null;
        int pathcost=0;
        int profondeur=0;
        Position newPosition=null;
        int counter=0;

        boolean action = false;
        while( !action)
        {
            if(node.getPositionRobot().y < goal.getPositionRobot().y) {
                //DOWN
                newPosition = new Position(node.getPositionRobot().x, node.getPositionRobot().y+1);
                profondeur = node.getProfondeur() + 1;
                pathcost = node.getPathCost() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);
                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().y - node.getPositionRobot().y) + abs(newNode.getPositionRobot().x - node.getPositionRobot().x));
                out.add(counter,Action.MOVE_RIGHT);
                counter++;
                node= newNode;
            }



            if(node.getPositionRobot().y > goal.getPositionRobot().y) {
                //UP
                newPosition = new Position(node.getPositionRobot().x, node.getPositionRobot().y-1);
                profondeur = node.getProfondeur() + 1;
                pathcost = node.getPathCost() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);
                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().y - node.getPositionRobot().y) + abs(newNode.getPositionRobot().x - node.getPositionRobot().x));
                out.add(counter,Action.MOVE_LEFT);
                counter++;
                node= newNode;
            }



            if(node.getPositionRobot().x > goal.getPositionRobot().x) {
                //LEFT
                newPosition = new Position(node.getPositionRobot().x-1, node.getPositionRobot().y);
                profondeur = node.getProfondeur() + 1;
                pathcost = node.getPathCost() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);
                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().y - node.getPositionRobot().y) + abs(newNode.getPositionRobot().x - node.getPositionRobot().x));
                out.add(counter,Action.MOVE_UP);
                counter++;
                node= newNode;
            }



            if(node.getPositionRobot().x < goal.getPositionRobot().x) {
                //RIGHT
                newPosition = new Position(node.getPositionRobot().x+1, node.getPositionRobot().y);
                profondeur = node.getProfondeur() + 1;
                pathcost = node.getPathCost() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);
                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().y - node.getPositionRobot().y) + abs(newNode.getPositionRobot().x - node.getPositionRobot().x));
                out.add(counter,Action.MOVE_DOWN);
                counter++;
                node= newNode;
            }



            if(vision.doesCaseHaveJewelery(node.getEnvironnement(),node.getPositionRobot())
                    && node.getPositionRobot().x == goal.getPositionRobot().x && node.getPositionRobot().y == goal.getPositionRobot().y) {
                //Gather
                newPosition = new Position(node.getPositionRobot().x, node.getPositionRobot().y);
                profondeur = node.getProfondeur() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);
                out.add(counter, Action.GATHER_JEWELRY);
                counter++;
                action = true;
                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().x - node.getPositionRobot().x) + abs(newNode.getPositionRobot().y - node.getPositionRobot().y));

                node = newNode;
            }

            if(vision.isCaseDirtyAt(node.getEnvironnement(),node.getPositionRobot())
                    && node.getPositionRobot().x == goal.getPositionRobot().x && node.getPositionRobot().y == goal.getPositionRobot().y) {

                //VACUM
                newPosition = new Position(node.getPositionRobot().x, node.getPositionRobot().y);
                profondeur = node.getProfondeur() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);
                out.add(counter,Action.VACUUM_DUST);
                counter++;
                action=true;
                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().x - node.getPositionRobot().x) + abs(newNode.getPositionRobot().y - node.getPositionRobot().y));
                node= newNode;
            }




        }


        return out;
    }

    private Noeud findBestGoal(EnvState e, Noeud start) {
        int closestdist = 100;
        Noeud goal = null;
        int pathcost;
        EnvState enew;
        int profondeur;
        for (int i =0;i<10;i++)
        {
            for (int j =0;j<10;j++)
            {

                if( vision.doesCaseHaveJewelery(e,new Position(i,j)) && (abs(i-start.getPositionRobot().x) + abs(j-start.getPositionRobot().y)) < closestdist )
                {
                    closestdist = (abs(i-start.getPositionRobot().x) + abs(j-start.getPositionRobot().y));
                    profondeur = closestdist+1;
                    pathcost = closestdist;
                    enew = new EnvState(e);
                    enew.getCase(start.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                    enew.getCase(new Position(i,j)).addEnvObject(EnvObject.ROBOT);
                    goal = new Noeud(start,enew,pathcost,profondeur,new Position(i,j));
                }
                else
                if( vision.isCaseDirtyAt(e,new Position(i,j)) && (abs(i-start.getPositionRobot().x) + abs(j-start.getPositionRobot().y)) < closestdist )
                {
                    closestdist = (abs(i-start.getPositionRobot().x) + abs(j-start.getPositionRobot().y));
                    profondeur = closestdist+1;
                    pathcost = closestdist;
                    enew = new EnvState(e);
                    enew.getCase(start.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                    enew.getCase(new Position(i,j)).addEnvObject(EnvObject.ROBOT);
                    goal = new Noeud(start,enew,pathcost,profondeur,new Position(i,j));
                }
                else continue;
            }
        }
        return goal;
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
    protected boolean doObserve() {
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
            Thread.sleep(actionTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
