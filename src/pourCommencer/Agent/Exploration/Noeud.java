package pourCommencer.Agent.Exploration;
import pourCommencer.Environment.ActionType;
import pourCommencer.Environment.EnvState;
import pourCommencer.Environment.Position;

import java.util.HashMap;
import java.util.LinkedList;

public class Noeud {
    private Noeud parent;
    private int pathCost;
    HashMap<Noeud,ActionType> successor;
    EnvState environnement;
    private int profondeur;

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

    public HashMap<Noeud, ActionType> getSuccessor() {
        return successor;
    }

    public void addSuccessor(Noeud n, ActionType a) {
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

    public Noeud(Noeud parent, EnvState environnement, int pathCost, int profondeur,Position position) {
        this.parent = parent;
        this.pathCost = pathCost;
        this.environnement = environnement;
        this.profondeur = profondeur;
        this.successor = new HashMap<>();
        this.positionRobot = position;
    }



}
