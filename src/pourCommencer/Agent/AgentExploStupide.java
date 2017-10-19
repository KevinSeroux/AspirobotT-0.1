package pourCommencer.Agent;

import javafx.util.Pair;
import pourCommencer.Agent.Exploration.Noeud;
import pourCommencer.Environment.*;
import pourCommencer.Excepetion.*;

import java.util.*;
import static pourCommencer.Agent.SensorVision.getAgentPosition;
import static pourCommencer.Agent.SensorVision.isCaseDirtyAt;
import static pourCommencer.Agent.SensorVision.isCaseJewelAt;

/**
 * Cette classe modélise un agent ayant des désires simples mais pas optimaux (seulement le plus proche)
 * Si un bijoux est dans l'environement alors il va le chercher
 * Sinon si une poussière est dans l'environnement il va la chercher
 *
 * Cette classe peut utiliser plusieurs algorithmes d'exploration non informée. Cependant, un profondeur max de recherche
 * et l'utilisation de marqueur dans l'environnement ont été utiliser pour soulager la mémoire (car il peut y avoir des boucles)
 */
public class AgentExploStupide extends Robot {


    protected static final int PROFONDEUR_MAX = 20;

    /**
     * Permet de modéliser les buts où l'exploration a échouée
     */
    private ArrayList<MentalState.Desire> impossibleGoal= new ArrayList<>();

    public AgentExploStupide(Environment env) {
        super(env);
    }

    @Override
    public void run() {
        robotWithExploration();
    }

    /**
     * Coeur du cycle de vie de l'agent
     */
    private void robotWithExploration() {
        // Execute all actions for the first observation
        MentalState mentalState = this.buildMentalState();
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

    /**
     * Construit l'état mental de l'agent
     * @return l'état mental de l'agent
     */
    private MentalState buildMentalState() {
        MentalState mentalState = new MentalState();
        mentalState.beliefs = vision.snapshotState();
        mentalState.goal = chooseDesire(mentalState.beliefs);
        if(mentalState.goal != MentalState.Desire.DEFAULT){
            try {
                mentalState.intentions = explorationLargeur(mentalState); //TODO <-Passer ca en paramètre
            } catch (ExplorationException e) {
                impossibleGoal.add(mentalState.goal);
                mentalState.intentions = new LinkedList<>();
            }
        }else{
            mentalState.intentions = new LinkedList<>();
        }

        return mentalState;
    }

    /**
     * Choisi un desire en fonction de l'état de l'environnement
     * @param state
     * @return
     */
    private MentalState.Desire chooseDesire(EnvState state){
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

    /**
     * Algorithme d'exploration en largeur
     * @param m l'état mental de l'agent
     * @return la liste des actions à effectuer
     * @throws ExplorationException
     */
    private LinkedList<Action> explorationLargeur(MentalState m) throws ExplorationException {
        EnvState e = new EnvState(m.beliefs);
        Position initiale = getAgentPosition(e);
        Noeud origine = new Noeud(null, e,0, 0,initiale, 0);
        origine.getEnvironnement().getCase(initiale).removeEnvObject(EnvObject.ROBOT);
        LinkedList<Noeud> fringe = new LinkedList<>();
        fringe.addAll(expand(origine));
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
            throw new ExplorationException();
        }
    }

    /**
     * Exploration itérative en profondeur
     * @param m l'état mental de l'agent
     * @return la liste des actions à éffectuer
     * @throws ExplorationException
     */
    private LinkedList<Action> explorationIterativeDeepening(MentalState m) throws ExplorationException {
        EnvState e;
        Position initiale = getAgentPosition(m.beliefs);
        Pair resultat = null;
        Noeud origine = null;
        int profondeur;
        for(profondeur = 0; profondeur < PROFONDEUR_MAX*100 ; profondeur++){
            e = new EnvState(m.beliefs);
            origine = new Noeud(null, e,0, 0,initiale, 0); //Position actuelle du robot ?
            origine.getEnvironnement().getCase(initiale).removeEnvObject(EnvObject.ROBOT);
            resultat = recursiveDLS(origine,profondeur,m);
            if(resultat.getKey().equals("noeud")){
                break;
            }
        }
        if (profondeur >= PROFONDEUR_MAX*100) throw new ExplorationException();
        Noeud node = (Noeud) resultat.getValue();
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
    }

    /**
     * Algorithme de recherche en profondeur
     * @param m l'etat mental de l'agent
     * @return la liste des actions à effectuer
     * @throws ExplorationException
     */
    private LinkedList<Action> explorationDepthLimited(MentalState m) throws ExplorationException {
        EnvState e = new EnvState(m.beliefs);
        Position initiale = getAgentPosition(e);
        Noeud origine = new Noeud(null, e,0, 0,initiale, 0); //Position actuelle du robot ?
        origine.getEnvironnement().getCase(initiale).removeEnvObject(EnvObject.ROBOT);
        Pair resultat = recursiveDLS(origine,PROFONDEUR_MAX,m);
        if (resultat.getKey().equals("noeud")){
            Noeud node = (Noeud) resultat.getValue();
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
        }
        else{
            System.out.println("Fail exploration");
            throw new ExplorationException();
        }
    }

    /**
     * Permet de faire la recherche en profondeur limitée récursive
     * @param node le noeud sur lequel faire le traitement
     * @param profondeur profondeur max autorisée
     * @param m l'état mental de l'agent
     * @return une Pair ayant pour valeur :
     *             * "noeud", Noeud : si la recherche aboutie
     *             * "cuttoff", null : si il y a cuttoff
     *             * "failure", null : si c'est un echec
     */
    private Pair<String,Noeud> recursiveDLS(Noeud node, int profondeur, MentalState m)  {
        boolean cutOffOccurred = false;
        if(goalTest(m,node)){
            return new Pair<>("noeud",node);
        }
        else if(node.getProfondeur() == profondeur){
            return new Pair<>("cuttoff",null);
        }else{
                for (Noeud successor :
                        expand(node)) {
                    Pair result = recursiveDLS(successor, profondeur, m);
                    if (result.getKey().equals("cuttoff"))
                        cutOffOccurred = true;
                    else if(!result.getKey().equals("failure"))
                        return result;
                }
                if(cutOffOccurred){
                    return new Pair<>("cuttoff",null);
                }
        }
        return new Pair<>("failure",null);
    }

    /**
     * Algorithme de recherche en profondeur
     * @param m l'état mental de l'agent
     * @return la listes des actions à faire
     * @throws ExplorationException
     */
    private LinkedList<Action> explorationDepthFirstSearch(MentalState m) throws ExplorationException {
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
                trouve = false;
                break; //retunr failure
            }
            node = fringe.removeLast();
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
            throw new ExplorationException();
        }
    }


    /**
     * Cherche le noeud dans l'arbre ayant la meilleure performance
     * @param origine la racine de l'arbre
     * @return le noeud avec la meilleure performance
     */
    private Noeud bestMoveToDo(Noeud origine) {
        return  origine.meilleurNoeud();
    }

    /**
     * Permet de savoir si le but de l'agent est accompli
     * @param m l'état mental de l'agent
     * @param node le noeud sur lequel effectuer le test
     * @return si le but est accompli ou non
     */
    private boolean goalTest(MentalState m, Noeud node) {
        if(node.getParent() != null) {
            if (m.goal == MentalState.Desire.DUST) {
                try {
                    return node.getParent().getSuccessor().get(node) == Action.VACUUM_DUST;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(m.goal == MentalState.Desire.JEWEL){
                return node.getParent().getSuccessor().get(node) == Action.GATHER_JEWELRY;
            }
        }
        return false;
    }

    /**
     * Fonction permetant d'etrande un noeud avec les nouveaux noeud possibles
     * @param node le noeud à étendre
     * @return les nouveaux noeuds à explorer
     */
    private Collection<? extends Noeud> expand(Noeud node) {
        LinkedList<Noeud> successors = new LinkedList<>();
        Noeud s;
        Position futurePosition = null;
        EnvState env;
        int performance;
        for (Action a:possibleActionsByPositionEtMarquage(node.getEnvironnement(),node.getPositionRobot())) { //-------------------TODO
            performance = node.getPerformance() -1;
            env = node.getEnvironnement();
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
        return successors;
    }


    /**
     * Cette méthode renvoie toutes les actions possibles qu'un agent peu effectuer dans l'environnement donné
     * @param belief l'environement de l'agent
     * @param p la position de l'agent
     * @return la liste des actions possibles
     */
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

    /**
     * Retourne les actions possibles en fonction d'un environnement marqué par le robot lors de son passage
     * @param belief une grille représentant l'environnement dans lequel se trouve le robot
     * @param p la position actuelle du robot dans l'environnement
     * @return un ensemble d'action possibles
     */
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
