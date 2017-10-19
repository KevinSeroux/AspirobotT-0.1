package pourCommencer.Agent;

import pourCommencer.Agent.Exploration.Noeud;
import pourCommencer.Environment.*;
import pourCommencer.Excepetion.ExpandActionTypeException;
import pourCommencer.Excepetion.explorationLargeurNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;
import static pourCommencer.Agent.SensorVision.getAgentPosition;

//TODO
class MentalState {
    EnvState beliefs;
    enum Desire { DEFAULT, DUST, JEWEL}
    Desire goal;
    LinkedList<Action> intentions;
}

public class Robot implements Runnable {
    private _PerformanceCounter perfCounter;
    private ExplorationFrequency exploFrequency;
    private EffecteurArm bras;
    private EffecteurAspiration aspiration;
    private EffecteurMouvement mouvement;
    private SensorVision vision;
    private int observationCounter;

    public int getMovementCounter() {
        return movementCounter;
    }

    public void setMovementCounter(int movementCounter) {
        this.movementCounter = movementCounter;
    }

    private int movementCounter = 0;

    public Robot(Environment env) {
        /* L'agent n'est supposé interagir avec l'environement
         * que par ses capteurs et ses effecteurs */

        this.perfCounter = env.getPerfCounter();
        this.exploFrequency = new ExplorationFrequency(0.1);
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

    private void stupidRobot() {
        // Execute all actions for the first observation
        MentalState mentalState = buildMentalState();
        while(!mentalState.intentions.isEmpty())
            executeAction(mentalState.intentions.poll());

        while (true) {
            if(doObserve())
                mentalState = buildMentalState();

            executeAction(mentalState.intentions.poll());

            // Notifie le système d'apprentissage de la performance de l'action
            exploFrequency.addMeasure(perfCounter.get());
        }
    }

    private void robotWithExploration() {
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

    private void robotInforme() {
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

                    EnvState env = new EnvState(mentalState.beliefs);
                    Noeud origine = new Noeud(null, env,0, 0,getAgentPosition(env));
                    mentalState.intentions = BuildTree(mentalState,origine,env);

            }

                while (!mentalState.intentions.isEmpty())
                    executeAction(mentalState.intentions.poll());

        }


    }

    public LinkedList<Action> BuildTree(MentalState m, Noeud origine, EnvState e1)
    {
        Noeud goal = findBestGoal(e1,origine);
//        graph = new LinkedList<Noeud>();
        LinkedList<Action> out = new LinkedList<Action>();
        EnvState e = new EnvState(e1);
        Position initiale = getAgentPosition(e);
//        Noeud origine = new Noeud(null, e,0, 0,initiale);;
//        origine.getEnvironnement().getCase(initiale).removeEnvObject(EnvObject.ROBOT);
//        graph.add(origine);
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
//                graph.add(newNode);
                out.add(counter,Action.MOVE_DOWN);
                counter++;
                //graph=BuildTree(m,newNode,graph,enew);
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
//                graph.add(newNode);
                out.add(counter,Action.MOVE_UP);
                counter++;
                // graph=BuildTree(m,newNode,graph,enew);
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
//                graph.add(newNode);
                //graph=BuildTree(m,newNode,graph,enew);
                out.add(counter,Action.MOVE_LEFT);
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
//                graph.add(newNode);
                out.add(counter,Action.MOVE_RIGHT);
                counter++;
                //  graph=BuildTree(m,newNode,graph,enew);
                node= newNode;
            }



            if(vision.doesCaseHaveJewelery(node.getEnvironnement(),new Position(node.getPositionRobot().y,node.getPositionRobot().x)) && node.getPositionRobot().x == goal.getPositionRobot().x && node.getPositionRobot().y == goal.getPositionRobot().y) {
                //Gather
                newPosition = new Position(node.getPositionRobot().x, node.getPositionRobot().y);
                profondeur = node.getProfondeur() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);

                if (this.vision.doesCaseHaveJewelery(node.getEnvironnement(), node.getPositionRobot())) {
                    pathcost += (int) enew.getPerfCounter().getSimulated(Action.GATHER_JEWELRY);
                    enew.getCase(newPosition).removeEnvObject(EnvObject.JEWELRY);
                    out.add(counter, Action.GATHER_JEWELRY);
                    counter++;
                    action = true;
                } else pathcost = node.getPathCost() + 1;

                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().x - node.getPositionRobot().x) + abs(newNode.getPositionRobot().y - node.getPositionRobot().y));
//            graph.add(newNode);
                //  graph=BuildTree(m,newNode,graph,enew);

                node = newNode;
            }

            if(vision.isCaseDirtyAt(node.getEnvironnement(),new Position(node.getPositionRobot().y,node.getPositionRobot().x)) && node.getPositionRobot().x == goal.getPositionRobot().x && node.getPositionRobot().y == goal.getPositionRobot().y) {

                //VACUM
                newPosition = new Position(node.getPositionRobot().x, node.getPositionRobot().y);
                profondeur = node.getProfondeur() + 1;
                enew = new EnvState(e);
                enew.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.ROBOT);
                enew.getCase(newPosition).addEnvObject(EnvObject.ROBOT);
                out.add(counter,Action.VACUUM_DUST);
                counter++;
                action=true;
//                if (this.vision.isCaseDirtyAt(node.getEnvironnement(), node.getPositionRobot())) {
////                    pathcost += (int) enew.getPerfCounter().getSimulated(Action.VACUUM_DUST);
//                    enew.getCase(newPosition).removeEnvObject(EnvObject.DUST);
//                    out.add(counter,Action.VACUUM_DUST);
//                    action=true;
//                } else {
//                    if (this.vision.doesCaseHaveJewelery(node.getEnvironnement(), node.getPositionRobot())) {
//                        pathcost += (int) enew.getPerfCounter().getSimulated(Action.VACUUM_JEWELRY);
//                        enew.getCase(newPosition).removeEnvObject(EnvObject.JEWELRY);
//                        out.add(counter,Action.VACUUM_JEWELRY);
//                        action=true;
//                    } else pathcost = node.getPathCost() + 1;
//                }
                newNode = new Noeud(node, enew, pathcost, profondeur, newPosition);
                newNode.setHeuristique(node.getHeuristique() + abs(newNode.getPositionRobot().x - node.getPositionRobot().x) + abs(newNode.getPositionRobot().y - node.getPositionRobot().y));
//                graph.add(newNode);
                // graph=BuildTree(m,newNode,graph,enew);
                node= newNode;
            }




        }


        return out;
        }

    private Noeud findBestGoal(EnvState e, Noeud start) {
       int closestdist = 100;
       Position newPosition;
       Noeud goal = null;
        int pathcost;
        EnvState enew;
        int profondeur;
       for (int i =0;i<10;i++)
       {
           for (int j =0;j<10;j++)
           {

               if( vision.doesCaseHaveJewelery(e,new Position(j,i)) && (abs(i-start.getPositionRobot().x) + abs(j-start.getPositionRobot().y)) < closestdist )
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
                   if( vision.isCaseDirtyAt(e,new Position(j,i)) && (abs(i-start.getPositionRobot().x) + abs(j-start.getPositionRobot().y)) < closestdist )
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

    private LinkedList<Action> explorationLargeur(MentalState m) throws explorationLargeurNotFoundException, ExpandActionTypeException {
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
            LinkedList<Action> todo = new LinkedList<>();
            System.out.println("Dernier noeud position "+node.getPositionRobot().x + " "+ node.getPositionRobot().y);
            while(node != origine){
                todo.push(node.getParent().getSuccessor().get(node));
                node = node.getParent();
            }
            for (Action a:todo
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
        for (Action a:possibleActionsByPosition(node.getEnvironnement(),node.getPositionRobot())) {
            switch (a) {
                case VACUUM_DUST:
                case GATHER_JEWELRY:
                    futurePosition = new Position(node.getPositionRobot().x,node.getPositionRobot().y); //TODO faire une methode gauche droite & co ca pourrait être sympa :D -Max
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
                default:
                    throw new ExpandActionTypeException();
            }
            s = new Noeud(node,node.getEnvironnement(),node.getPathCost() + 1,node.getProfondeur()+1, futurePosition); //TODO remplacer 1 par Action.getCoutAction()
            node.addSuccessor(s,a);
            successors.add(s);
        }
        return successors;
    }


    private Set<Action> possibleActionsByPosition(EnvState belief, Position p) {
        Set<Action> actionsList = new HashSet<>();
        int envSize = belief.getEnvSize();

        if(p.x >= 1)
            actionsList.add(Action.MOVE_UP);

        if(p.x < envSize - 1)
            actionsList.add(Action.MOVE_DOWN);

        if(p.y >= 1)
            actionsList.add(Action.MOVE_LEFT);

        if(p.y < envSize - 1)
            actionsList.add(Action.MOVE_RIGHT);

        if(SensorVision.isCaseDirtyAt(belief, p))
            actionsList.add(Action.VACUUM_DUST);

        if(SensorVision.doesCaseHaveJewelery(belief, p))
            actionsList.add(Action.GATHER_JEWELRY);

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

    private MentalState.Desire chooseBestDesire(EnvState state)
    {

        return MentalState.Desire.DEFAULT;
    }
    private MentalState buildMentalState() {
        MentalState mentalState = new MentalState();
        mentalState.beliefs = vision.snapshotState();
        mentalState.goal = chooseGoal(mentalState.beliefs);
        Set<Action> actionsPossible = possibleActions(mentalState.beliefs);
        mentalState.intentions = chooseIntentions(mentalState.goal, actionsPossible);

        return mentalState;
    }

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

    //TODO C'est faux ici (inversion x et y)
    //DONE Changé par Max pour x et y
    /* L'agent s'interroge ici sur les actions qu'il peut faire.
     * Il trie les actions n'apportant pas d'intérêt */
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

        //For the test choose several intentions
        int countActions2Generate = ThreadLocalRandom.current().nextInt(10);
        for(int i = 0; i < countActions2Generate; i++) {
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
        }

        return intentions;
    }

    private void executeAction(Action action) {
        if(action != null) {
            switch (action) {
                case VACUUM_DUST:
                    aspiration.aspirer();
                    movementCounter++;
                    break;

                case GATHER_JEWELRY:
                    bras.ramasser();
                    movementCounter++;
                    break;

                case MOVE_UP:
                    mouvement.moveUp();
                    movementCounter++;
                    break;

                case MOVE_DOWN:
                    mouvement.moveDown();
                    movementCounter++;
                    break;

                case MOVE_LEFT:
                    mouvement.moveLeft();
                    movementCounter++;
                    break;

                case MOVE_RIGHT:
                    mouvement.moveRight();
                    movementCounter++;
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
