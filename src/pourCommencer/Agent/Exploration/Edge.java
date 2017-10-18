package pourCommencer.Agent.Exploration;

import pourCommencer.Agent.Action;

public class Edge {
        public Noeud start;
        public Noeud end;
        public Action action;

    public Edge(Noeud start, Noeud end, Action action) {
        this.start = start;
        this.end = end;
        this.action = action;
    }
}
