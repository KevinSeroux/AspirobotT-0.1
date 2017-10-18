package pourCommencer.Environment;

public class Manor extends Environment {

    private int size;
    private EnvState state;
    private Position agentPosition;

    public Manor(PerformanceCounter perfCounter, int manorSize, Position caseDepartRobot) {
        super(perfCounter);

        this.size = manorSize;
        this.state = new EnvState(size);

        updateAgentPosition(caseDepartRobot);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void triggerAction(Action action) {
        ActionType type = action.type;

        switch(type) {
            //TODO: Notify observer
            case NEW_DUST: {
                Position pos = (Position) action.data;
                state.getCase(pos).addEnvObject(EnvObject.DUST);
                break;
            }

            case NEW_JEWELRY: {
                Position pos = (Position) action.data;
                state.getCase(pos).removeEnvObject(EnvObject.JEWELRY);
                break;
            }

            default:
                actionAgent(type);
                break;
        }

        setChanged();
        notifyObservers(action);
    }

    private void actionAgent(ActionType actionType) {
        switch(actionType) {
            case VACUUM_DUST: {
                Case agentCase = state.getCase(agentPosition);
                agentCase.removeEnvObject(EnvObject.DUST);

                if (agentCase.containsEnvObject(EnvObject.JEWELRY)) {
                    setChanged();
                    notifyObservers(new Action(ActionType.VACCUM_JEWELRY));
                    /* Si la case actuelle contient aussi un bijoux on execute
                     * aussi le case GATHER_JEWELRY */
                }
                else
                    break;
            }

            /* Ne rien mettre entre VACCUM_DUST ET GATHER_JEWELRY,
             * voir autre commentaire ci-dessus */
            case GATHER_JEWELRY:
                state.getCase(agentPosition).removeEnvObject(EnvObject.JEWELRY);
                break;

            default:
                moveAgent(actionType);
                break;
        }
    }

    private void moveAgent(ActionType actionType) {
        Position pos = new Position(agentPosition);

        switch(actionType) {
            case MOVE_UP:
                if(pos.x == 0)
                    System.out.println("The agent hits the north wall");
                else
                    pos.x--;
                break;

            case MOVE_DOWN:
                if(pos.x == size - 1)
                    System.out.println("The agent hits the south wall");
                else
                    pos.x++;
                break;

            case MOVE_LEFT:
                if(pos.y == 0)
                    System.out.println("The agent hits the west wall");
                else
                    pos.y--;
                break;

            case MOVE_RIGHT:
                if(pos.y == size - 1)
                    System.out.println("The agent hits the est wall");
                else
                    pos.y++;
                break;

            default:
                throw new RuntimeException("Unknown move action for the agent");
        }

        updateAgentPosition(pos);
    }

    private void updateAgentPosition(Position newPos) {
        if(agentPosition != null)
            state.getCase(agentPosition).removeEnvObject(EnvObject.ROBOT);

        this.agentPosition = newPos;
        state.getCase(newPos).addEnvObject(EnvObject.ROBOT);
    }

    @Override
    public EnvState getState() {
        return state;
    }

    @Override
    public String toString() {
        String representation = "";
        for (int i = 0; i < size; i++) {
            representation += "-----";
        }
        representation += "\n";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                representation += state.getCase(new Position(i, j)) + "|";
            }
            representation += "\n";
            for (int j = 0; j < size; j++) {
                representation += "-----";
            }
            representation += "\n";
        }
        return representation;
    }
}
