package aspirobot.environnement;

// The environment state is like a matrix
public class EnvState {
	private int size;
	private Case[][] cases;
	private PerformanceCounter perfCounter;

	public PerformanceCounter getPerfCounter() {
		return perfCounter;
	}

	public void setPerfCounter(PerformanceCounter perfCounter) {
		this.perfCounter = perfCounter;
	}

	public EnvState(int size) {
		this.size = size;
		this.cases = new Case[size][size];

		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++)
				cases[i][j] = new Case(new Position(i, j));
	}

	// Copy
	public EnvState(EnvState envState) {
		this.size = envState.size;
		this.cases = new Case[size][size];

		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++)
				cases[i][j] = new Case(envState.cases[i][j]);
	}

	public Case getCase(Position pos) {
		return cases[pos.x][pos.y];
	}

	public int getEnvSize() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder representation = new StringBuilder();

		for (int i = 0; i < size; i++) {
			representation.append("-----");
		}
		representation.append('\n');

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Case aCase = getCase(new Position(i, j));
				representation.append(aCase + "|");
			}
			representation.append('\n');

			for (int j = 0; j < size; j++) {
				representation.append("-----");
			}
			representation.append('\n');
		}

		return representation.toString();
	}
}
