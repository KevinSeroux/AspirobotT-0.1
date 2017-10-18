package pourCommencer.Agent.Exploration;
import pourCommencer.Agent.Action;
import pourCommencer.Environment.EnvState;
import pourCommencer.Environment.Position;

import java.util.HashMap;
import java.util.Map;

public class Noeud {
    private Noeud parent;
    private int pathCost;
    HashMap<Noeud,Action> successor;
    EnvState environnement;
    private int profondeur;
    private int performance;

    public Noeud getParent() {
        return parent;
    }

    public void setParent(Noeud parent) {
        this.parent = parent;
    }

    public int getPathCost() {
        return pathCost;
    }

    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;
    }

    public HashMap<Noeud, Action> getSuccessor() {
        return successor;
    }

    public void addSuccessor(Noeud n, Action a) {
        this.successor.put(n,a);
    }


    public EnvState getEnvironnement() {
        return environnement;
    }

    public void setEnvironnement(EnvState environnement) {
        this.environnement = environnement;
    }


    public int getProfondeur() {
        return profondeur;
    }

    public void setProfondeur(int profondeur) {
        this.profondeur = profondeur;
    }

    public Position getPositionRobot() {
        return positionRobot;
    }

    public void setPositionRobot(Position positionRobot) {
        this.positionRobot = positionRobot;
    }

    private Position positionRobot;

    public int getPerformance() {
        return performance;
    }

    public Noeud meilleurNoeud(){
        Noeud bestFound = this;
        for(Map.Entry<Noeud, Action> entry : successor.entrySet()) {
            Noeud key = entry.getKey();

            Noeud n = key.meilleurNoeud();
            if(bestFound.getPerformance() < n.getPerformance())
                bestFound = n;
        }
        return bestFound;
    }

    public Noeud(Noeud parent, EnvState environnement, int pathCost, int profondeur, Position position, int performance) {
        this.parent = parent;
        this.pathCost = pathCost;
        this.environnement = environnement;
        this.profondeur = profondeur;
        this.performance = performance;
        this.successor = new HashMap<>();
        this.positionRobot = position;
    }



}
