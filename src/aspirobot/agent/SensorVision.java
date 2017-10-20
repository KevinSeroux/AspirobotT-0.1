package aspirobot.agent;

import aspirobot.environnement.*;

/* Cette classe est celle qui est notifiée en cas de modification
 * de l'environnement, elle sert de tampon entre le thread de l'agent
  * et celui de l'environement */
public class SensorVision {
    private _Environment env;

    public SensorVision(_Environment env) {
        this.env = env;
    }

    // Retourne une snapshot de l'env
    public EnvState snapshotState() {
        return env.getStateSnapshot();
    }

    static public boolean isCaseDirtyAt(EnvState state, Position pos) {
        return state.getCase(pos).containsEnvObject(EnvObject.DUST);
    }
    static public boolean isCaseJewelAt(EnvState state, Position pos) {
        return state.getCase(pos).containsEnvObject(EnvObject.JEWELRY);
    }

    static public boolean doesCaseHaveJewelery(EnvState state, Position pos) {
        return state.getCase(pos).containsEnvObject(EnvObject.JEWELRY);
    }

    static public Position getAgentPosition(EnvState state) {
        int size = state.getEnvSize();

        // Search for each case if the robot is here
        // TODO: Improve this
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++) {
                Position pos = new Position(i, j);
                if (state.getCase(pos).containsEnvObject(EnvObject.ROBOT))
                    return pos;
            }

        return null;
    }

    static public boolean isThereDust(EnvState state) {
        int size = state.getEnvSize();

        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++) {
                Position pos = new Position(i, j);
                if (state.getCase(pos).containsEnvObject(EnvObject.DUST))
                    return true;
            }

        return false;

    }
    static public boolean isThereJewel(EnvState state) {
        int size = state.getEnvSize();

        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++) {
                Position pos = new Position(i, j);
                if (state.getCase(pos).containsEnvObject(EnvObject.JEWELRY))
                    return true;
            }

        return false;

    }
}
