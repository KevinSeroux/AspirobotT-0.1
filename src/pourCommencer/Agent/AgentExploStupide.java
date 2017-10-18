package pourCommencer.Agent;

import pourCommencer.Agent.Exploration.Noeud;
import pourCommencer.Environment.*;
import pourCommencer.Excepetion.ExpandActionTypeException;
import pourCommencer.Excepetion.explorationLargeurNotFoundException;

import java.util.*;

import static pourCommencer.Agent.SensorVision.getAgentPosition;
import static pourCommencer.Agent.SensorVision.isCaseDirtyAt;
import static pourCommencer.Agent.SensorVision.isCaseJewelAt;

public class AgentExploStupide extends Robot {

    private ArrayList<MentalState.Desire> impossibleGoal= new ArrayList<>();

    public AgentExploStupide(Environment env) {
        super(env);
    }

    @Override
    public void run() {
        robotWithExploration();
    }

    private void robotWithExploration() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MentalState mentalState = new MentalState();
        while(true) {
            mentalState.beliefs = super.vision.snapshotState(); //Observation
            mentalState.goal = chooseRandomDesire(mentalState.beliefs); //Choix stupid de but
            if (mentalState.goal != MentalState.Desire.DEFAULT) {
                try {
                    mentalState.intentions = explorationLargeur(mentalState);
                } catch (explorationLargeurNotFoundException e) {
                    impossibleGoal.add(mentalState.goal);
                    continue;
                } catch (ExpandActionTypeException e) {
                    e.printStackTrace();
                    continue;
                }
                impossibleGoal.clear();
                while (!mentalState.intentions.isEmpty())
                    super.executeAction(mentalState.intentions.poll());
            }else{
                impossibleGoal.clear();
            }
        }


    }
    private MentalState.Desire chooseRandomDesire(EnvState state){
        /*Random r = new Random(); //TODO - faudrait l'avoir en permanant non ? ou via thread Random - Max
        //TODO Faire ca en regardant si y'a au moins 1 pourssière dans l'env
        if(r.nextInt(100)<70) return MentalState.Desire.DUST;
        //TODO Faire ca en regardant si y'a au moins 1 Jewel dans l'env
        return MentalState.Desire.JEWEL;*/

        if(SensorVision.isThereJewel(state)){
            if (!impossibleGoal.contains(MentalState.Desire.JEWEL))
                return MentalState.Desire.JEWEL;
        }

        if (SensorVision.isThereDust(state)){
            if (!impossibleGoal.contains(MentalState.Desire.DUST))
                return MentalState.Desire.DUST;
        }

        return MentalState.Desire.DEFAULT; //TODO pour moi c'est Do Nothing -Max
    }

    private LinkedList<Action> explorationLargeur(MentalState m) throws explorationLargeurNotFoundException, ExpandActionTypeException {
        EnvState e = new EnvState(m.beliefs);
        Position initiale = getAgentPosition(e);
        Noeud origine = new Noeud(null, e,0, 0,initiale, 0); //Position actuelle du robot ?
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
            if (node.getProfondeur() < PROFONDEUR_MAX)
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
            System.out.println("------------------------\n BEST MOVE TO DO \n -------------------------");
            //TODO renvoyer action nulle -Max
            throw new explorationLargeurNotFoundException();
        }
    }

    private Noeud bestMoveToDo(Noeud origine) {
        return  origine.meilleurNoeud();
    }

    private boolean goalTest(MentalState m, Noeud node) {
        //System.out.println("Position : x : " + node.getPositionRobot().x+ ", y : "+node.getPositionRobot().y);
        Case caseCourante = node.getEnvironnement().getCase(node.getPositionRobot());
        if(caseCourante.containsEnvObject(EnvObject.DUST) && m.goal == MentalState.Desire.DUST){
            //TODO BUG Si jewel + dust sur la meme case
            if(node.getParent().getSuccessor().get(node) == Action.VACUUM_DUST) return true;
            return false;
        }

        if(caseCourante.containsEnvObject(EnvObject.JEWELRY) && m.goal == MentalState.Desire.JEWEL){
            if(node.getParent().getSuccessor().get(node) == Action.GATHER_JEWELRY) return true;
            return false;
        }
        /*if (m.goal == MentalState.Desire.DEFAULT){
            if(node.getParent().getSuccessor().get(node) == ActionType.DO_NOTHING) return true;
            return false;
        }*/
        return false;
    }

    private Collection<? extends Noeud> expand(Noeud node) throws ExpandActionTypeException {
        LinkedList<Noeud> successors = new LinkedList<>();
        Noeud s;
        Position futurePosition = null;
        int performance = node.getPerformance() -1;
        for (Action a:possibleActionsByPosition(node.getEnvironnement(),node.getPositionRobot())) {
            switch (a) {
                case VACUUM_DUST:
                    if(isCaseDirtyAt(node.getEnvironnement(),node.getPositionRobot())){
                        //performance+=ActionType.VACUUM_DUST.getPerf() - ActionType.VACUUM_DUST.getCoutAction(); //TODO
                        performance+=10;
                        if (isCaseJewelAt(node.getEnvironnement(),node.getPositionRobot()))
                        performance-=40;
                    }
                case GATHER_JEWELRY:
                    if(a == Action.GATHER_JEWELRY && isCaseJewelAt(node.getEnvironnement(),node.getPositionRobot())){
                        //performance+=ActionType.VACUUM_DUST.getPerf() - ActionType.VACUUM_DUST.getCoutAction(); //TODO
                        performance+=20;
                    }
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
            }
            s = new Noeud(node,node.getEnvironnement(),node.getPathCost() + 1,node.getProfondeur()+1, futurePosition, performance); //TODO remplacer 1 par ActionType.getCoutAction()
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


}
