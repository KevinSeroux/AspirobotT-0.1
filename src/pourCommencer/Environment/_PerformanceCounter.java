package pourCommencer.Environment;

import pourCommencer.Agent.Action;

public interface _PerformanceCounter {
	double get();

    double getSimulated(Action action);
}
