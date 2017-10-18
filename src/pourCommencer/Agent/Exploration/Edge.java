package pourCommencer.Agent.Exploration;

import pourCommencer.Environment.ActionType;

public class Edge {
        public Noeud start;
        public Noeud end;
        public ActionType action;

    public Edge(Noeud start, Noeud end, ActionType action) {
        this.start = start;
        this.end = end;
        this.action = action;
    }
}
