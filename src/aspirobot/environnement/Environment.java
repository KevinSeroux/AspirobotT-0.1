package aspirobot.environnement;

import java.util.Observable;

public abstract class Environment extends Observable implements _Environment {
	private _PerformanceCounter perfCounter;

	public Environment(PerformanceCounter perfCounter) {
		addObserver(perfCounter);
		this.perfCounter = perfCounter;
	}

	public _PerformanceCounter getPerfCounter() {
		return perfCounter;
	}
}
