package pourCommencer.Environment;

public interface _Environment {
    int getSize();
    void triggerAction(Action action);
    EnvState getState();
}