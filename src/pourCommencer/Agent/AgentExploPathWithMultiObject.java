package pourCommencer.Agent;

import pourCommencer.Agent.Exploration.Noeud;
import pourCommencer.Environment.EnvObject;
import pourCommencer.Environment.EnvState;
import pourCommencer.Environment.Environment;
import pourCommencer.Environment.Position;
import pourCommencer.Excepetion.ExplorationException;

import java.util.*;

import static pourCommencer.Agent.SensorVision.*;

/**
 * Classe modélisant un agent utilisant l'exploration non informée en largeur.
 * Ici l'agent obtient un chemin maximisant sa performance sur un nombre d'actions limité (PROFONDEUR_MAX)
 * Cette limite est due à l'espace mémoire important utilisé.
 */
public class AgentExploPathWithMultiObject extends Robot {

    private static final int PROFONDEUR_MAX = 10;

    public AgentExploPathWithMultiObject(Environment env) {
        super(env);
    }

    @Override
    public void run() {
        robotWithExploration();
    }

    /**
     * Fonction coeur du cycle de vie de l'agent.
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
     * Construit l'etat mental de l'agent
     * @return l'état mental de l'agent
     */
    private MentalState buildMentalState() {
        MentalState mentalState = new MentalState();
        mentalState.beliefs = vision.snapshotState();
        mentalState.goal = MentalState.Desire.DEFAULT;
            try {
                mentalState.intentions = explorationLargeur(mentalState); //TODO <-Passer ca en paramètre
            } catch (ExplorationException e) {
                //TODO ne doit plus arriver !
                mentalState.intentions = new LinkedList<>();
            }
        return mentalState;
    }


    /**
     * Algorithme d'exploration en Largeur
     * Cet algorithme ne tient pas compte des "désires" du robot.
     * A la place, le désire est remplacer par "avoir la meilleure performance"
     * @param m l'état mental du robot
     * @return la listes des actions a effectuer
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
        for (Action a:possibleActionsByPositionEtMarquage(node.getEnvironnement(),node.getPositionRobot())) {
            performance = node.getPerformance() -1;
            env = new EnvState(node.getEnvironnement());
            env.getCase(node.getPositionRobot()).addEnvObject(EnvObject.ROBOT);
            switch (a) {
                case VACUUM_DUST:
                    if(isCaseDirtyAt(node.getEnvironnement(),node.getPositionRobot())){
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
            }
            s = new Noeud(node,env,node.getPathCost() + 1,node.getProfondeur()+1, futurePosition, performance); //TODO remplacer 1 par Action.getCoutAction()
            node.addSuccessor(s,a);
            successors.add(s);
        }
        node.setEnvironnement(null);
        return successors;
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
