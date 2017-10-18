package pourCommencer.Environment;

import pourCommencer.Event;

public class Manor extends Environment {

    private EnvState state;
    private Position agentPosition;

    public Manor(PerformanceCounter perfCounter, int manorSize, Position caseDepartRobot) {
        super(perfCounter);
        this.state = new EnvState(manorSize);

        updateAgentPosition(caseDepartRobot);
    }

    @Override
    public int getSize() {
        return state.getEnvSize();
    }

    // Return a copy of the current state, a modification won't work
    @Override
    public EnvState getStateSnapshot() {
        EnvState copy = new EnvState(state);
        return copy;
    }

    @Override
    public void placeDustAt(Position pos) {
        state.getCase(pos).addEnvObject(EnvObject.DUST);
        setChanged();
        notifyObservers(Event.DUST_GENERATED);
    }

    @Override
    public void placeJewelAt(Position pos) {
        state.getCase(pos).addEnvObject(EnvObject.JEWELRY);
        setChanged();
        notifyObservers(Event.JEWELRY_GENERATED);
    }

    /* On surcharge chaque action par défaut car ce n'est pas
     * parce qu'un agent veut faire quelque chose qu'il le peut */

    @Override
    public void agentDoVaccumDust() {
        Event event = Event.DUST_VACCUMED;

        // If the remove succeed, then we suppose there was a jewel
        if(getAgentCase().removeEnvObject(EnvObject.JEWELRY)) {
            event = Event.JEWELRY_VACCUMED;
            setChanged();
            notifyObservers(event);
        }

        // If there was no dust it is an useless action;
        if(!getAgentCase().removeEnvObject(EnvObject.DUST))
            event = Event.VOID_VACCUMED;

        setChanged();
        notifyObservers(event);
    }

    @Override
    public void agentDoGatherJewel() {
        Event event = Event.JEWELRY_GATHERED;

        // If there was no jewelry it is an useless action;
        if(!getAgentCase().removeEnvObject(EnvObject.JEWELRY))
            event = Event.VOID_GATHERED;

        setChanged();
        notifyObservers(event);
    }

    @Override
    public void agentDoMoveUp() {
        Event event = Event.AGENT_MOVED;
        Position pos = new Position(agentPosition);

        if(pos.x == 0)
            event = Event.AGENT_HIT_NORTH;
        else {
            pos.x--;
            updateAgentPosition(pos);
        }

        setChanged();
        notifyObservers(event);
    }

    @Override
    public void agentDoMoveDown() {
        Event event = Event.AGENT_MOVED;
        Position pos = new Position(agentPosition);

        if(pos.x == getSize() - 1)
            event = Event.AGENT_HIT_SOUTH;
        else {
            pos.x++;
            updateAgentPosition(pos);
        }

        setChanged();
        notifyObservers(event);
    }

    @Override
    public void agentDoMoveLeft() {
        Event event = Event.AGENT_MOVED;
        Position pos = new Position(agentPosition);

        if(pos.y == 0)
            event = Event.AGENT_HIT_WEST;
        else {
            pos.y--;
            updateAgentPosition(pos);
        }

        setChanged();
        notifyObservers(event);
    }

    @Override
    public void agentDoMoveRight() {
        Event event = Event.AGENT_MOVED;
        Position pos = new Position(agentPosition);

        if(pos.y == getSize() - 1)
            event = Event.AGENT_HIT_EST;
        else {
            pos.y++;
            updateAgentPosition(pos);
        }

        setChanged();
        notifyObservers(event);
    }

    private Case getAgentCase() {
        return state.getCase(agentPosition);
    }

    private void updateAgentPosition(Position newPos) {
        if(agentPosition != null)
            state.getCase(agentPosition).removeEnvObject(EnvObject.ROBOT);

        this.agentPosition = newPos;
        state.getCase(newPos).addEnvObject(EnvObject.ROBOT);
    }
}
