package pourCommencer.Agent;

import javafx.util.Pair;
import pourCommencer.Agent.Exploration.Noeud;
import pourCommencer.Environment.*;
import pourCommencer.Excepetion.*;

import java.util.*;
import java.util.concurrent.Callable;

import static pourCommencer.Agent.SensorVision.getAgentPosition;
import static pourCommencer.Agent.SensorVision.isCaseDirtyAt;
import static pourCommencer.Agent.SensorVision.isCaseJewelAt;

public class AgentExploPathWithMultiObject extends Robot {

    private ArrayList<MentalState.Desire> impossibleGoal= new ArrayList<>();

    private static final int PROFONDEUR_MAX = 10;

    public AgentExploPathWithMultiObject(Environment env) {
        super(env);
    }

    @Override
    public void run() {
        robotWithExploration();
    }

    private void robotWithExploration() {
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MentalState mentalState = new MentalState();
        //TODO -- Faire comme algo stupid
        while(true) {
            mentalState.beliefs = super.vision.snapshotState(); //Observation
            mentalState.goal = chooseRandomDesire(mentalState.beliefs); //Choix stupid de but
            if (mentalState.goal != MentalState.Desire.DEFAULT) {
                try {
                    mentalState.intentions = explorationLargeur(mentalState);
                    //mentalState.intentions = explorationDepthFirstSearch(mentalState);
                    //mentalState.intentions = explorationDepthLimited(mentalState);
                    //mentalState.intentions = explorationIterativeDeepening(mentalState);

                } catch (ExplorationException e) {
                    System.out.println("CHANGEMENT DE BUT --------------------------------------- CHANGEMENT DE BUT");
                    impossibleGoal.add(mentalState.goal);
                    continue;
                }
                impossibleGoal.clear();
                while (!mentalState.intentions.isEmpty())
                    super.executeAction(mentalState.intentions.poll());
            }else{
                impossibleGoal.clear();
            }*/


        // Execute all actions for the first observation
        MentalState mentalState = this.buildMentalState();
        while(!mentalState.intentions.isEmpty())
            executeAction(mentalState.intentions.poll());

        // Then, place some observations between actions
        while (true) {
            if(doObserve())
            //if (mentalState.intentions.isEmpty())
                mentalState = buildMentalState();

            executeAction(mentalState.intentions.poll());

            // Notify the frequency learning system of the new perf
            exploFrequency.addMeasure(perfCounter.get());
        }



    }

    private MentalState buildMentalState() {
        MentalState mentalState = new MentalState();
        mentalState.beliefs = vision.snapshotState();
        mentalState.goal = chooseRandomDesire(mentalState.beliefs);
        if(mentalState.goal != MentalState.Desire.DEFAULT){
            try {
                mentalState.intentions = explorationLargeur(mentalState); //TODO <-Passer ca en paramètre
            } catch (ExplorationException e) {
                //TODO ne doit plus arriver !
                System.out.println("CHANGEMENT DE BUT --------------------------------------- CHANGEMENT DE BUT");
                impossibleGoal.add(mentalState.goal);
                mentalState.intentions = new LinkedList<>();
            }
        }else{
            mentalState.intentions = new LinkedList<>();
        }

        return mentalState;
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


    private LinkedList<Action> explorationLargeur(MentalState m) throws ExplorationException {


        EnvState e = new EnvState(m.beliefs);
        Position initiale = getAgentPosition(e);
        Noeud origine = new Noeud(null, e,0, 0,initiale, 0); //Position actuelle du robot ?
        origine.getEnvironnement().getCase(initiale).removeEnvObject(EnvObject.ROBOT);
        LinkedList<Noeud> fringe = new LinkedList<>();
        fringe.addAll(expand(origine));
        Noeud node = null;
        boolean trouve = false;
        while (true){
            if(fringe.size() == 0){
                trouve = true;
                break; //retunr failure
            }
            node = fringe.removeFirst();
            if (node.getProfondeur() < PROFONDEUR_MAX)
                fringe.addAll(expand(node));
        }
        if(trouve){
            LinkedList<Action> todo = new LinkedList<>();
            node = bestMoveToDo(origine);
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
            //System.out.println("------------------------\n BEST MOVE TO DO \n -------------------------");
            //TODO renvoyer action nulle -Max
            throw new ExplorationException();
        }
    }



    private Noeud bestMoveToDo(Noeud origine) {
        return  origine.meilleurNoeud();
    }

    private boolean goalTest(MentalState m, Noeud node) {
        //System.out.println("Position : x : " + node.getPositionRobot().x+ ", y : "+node.getPositionRobot().y);
        Case caseCourante = node.getEnvironnement().getCase(node.getPositionRobot());

        if(node.getParent() != null) {
            //if (caseCourante.containsEnvObject(EnvObject.DUST) && m.goal == MentalState.Desire.DUST) {
            if (m.goal == MentalState.Desire.DUST) {
                //TODO BUG Si jewel + dust sur la meme case
                try {
                    return node.getParent().getSuccessor().get(node) == Action.VACUUM_DUST;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //if (caseCourante.containsEnvObject(EnvObject.JEWELRY) && m.goal == MentalState.Desire.JEWEL) {
            if(m.goal == MentalState.Desire.JEWEL){
                return node.getParent().getSuccessor().get(node) == Action.GATHER_JEWELRY;
            }
        /*if (m.goal == MentalState.Desire.DEFAULT){
            if(node.getParent().getSuccessor().get(node) == Action.DO_NOTHING) return true;
            return false;
        }*/
        }
        return false;
    }

    private Collection<? extends Noeud> expand(Noeud node) {
        LinkedList<Noeud> successors = new LinkedList<>();
        Noeud s;
        Position futurePosition = null;
        /*env = new EnvState(env);
        env.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.DUST);
        env.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.JEWELRY);*/
        EnvState env;// =node.getEnvironnement();
        int performance;// = node.getPerformance() -1;
        for (Action a:possibleActionsByPositionEtMarquage(node.getEnvironnement(),node.getPositionRobot())) { //-------------------TODO
            performance = node.getPerformance() -1;
            //env = node.getEnvironnement();
            env = new EnvState(node.getEnvironnement());
            env.getCase(node.getPositionRobot()).addEnvObject(EnvObject.ROBOT);
            switch (a) {
                case VACUUM_DUST:
                    if(isCaseDirtyAt(node.getEnvironnement(),node.getPositionRobot())){
                        //performance+=Action.VACUUM_DUST.getPerf() - Action.VACUUM_DUST.getCoutAction(); //TODO
                        performance+=10;
                        env.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.DUST);
                        if (isCaseJewelAt(node.getEnvironnement(),node.getPositionRobot())) {
                            performance -= 40;
                            env.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.JEWELRY);
                        }
                    }
                case GATHER_JEWELRY:
                    if(a == Action.GATHER_JEWELRY && isCaseJewelAt(node.getEnvironnement(),node.getPositionRobot())){
                        //performance+=Action.VACUUM_DUST.getPerf() - Action.VACUUM_DUST.getCoutAction(); //TODO
                        performance+=20;
                        env.getCase(node.getPositionRobot()).removeEnvObject(EnvObject.JEWELRY);
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
            s = new Noeud(node,env,node.getPathCost() + 1,node.getProfondeur()+1, futurePosition, performance); //TODO remplacer 1 par Action.getCoutAction()
            node.addSuccessor(s,a);
            successors.add(s);
        }
        node.setEnvironnement(null);
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

    private Set<Action> possibleActionsByPositionEtMarquage(EnvState belief, Position p) {
        Set<Action> actionsList = new HashSet<>();
        int envSize = belief.getEnvSize();

        if(p.x >= 1 && !belief.getCase(new Position(p.x-1,p.y)).containsEnvObject(EnvObject.ROBOT))
            actionsList.add(Action.MOVE_UP);

        if(p.x < envSize - 1 && !belief.getCase(new Position(p.x+1,p.y)).containsEnvObject(EnvObject.ROBOT))
            actionsList.add(Action.MOVE_DOWN);

        if(p.y >= 1 && !belief.getCase(new Position(p.x,p.y-1)).containsEnvObject(EnvObject.ROBOT))
            actionsList.add(Action.MOVE_LEFT);

        if(p.y < envSize - 1 && !belief.getCase(new Position(p.x,p.y+1)).containsEnvObject(EnvObject.ROBOT))
            actionsList.add(Action.MOVE_RIGHT);

        if(SensorVision.isCaseDirtyAt(belief, p))
            actionsList.add(Action.VACUUM_DUST);

        if(SensorVision.doesCaseHaveJewelery(belief, p))
            actionsList.add(Action.GATHER_JEWELRY);

        return actionsList;
    }


}
