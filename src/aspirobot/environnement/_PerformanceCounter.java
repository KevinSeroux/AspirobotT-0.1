package aspirobot.environnement;

import aspirobot.agent.Action;

public interface _PerformanceCounter {
	double get();

    double getSimulated(Action action);
}
