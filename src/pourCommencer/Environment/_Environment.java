package pourCommencer.Environment;

import pourCommencer.Agent.Action;

public interface _Environment {
    int getSize();
    EnvState getStateSnapshot();
    void placeDustAt(Position pos);
    void placeJewelAt(Position pos);
    void agentDoVaccumDust();
    void agentDoGatherJewel();
    void agentDoMoveUp();
    void agentDoMoveDown();
    void agentDoMoveLeft();
    void agentDoMoveRight();
}