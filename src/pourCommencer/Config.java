package pourCommencer;

public class Config {
	private static final long timeUnit = 1000; // Default: 1000ms
	public static final long actionTime = 1 * timeUnit;
	public static final long maxGenerationTime = 5 * timeUnit;

	// Simulation of the environment
	public static final double probabilityGenerateDust = 0.9;
	public static final double probabilityGenerateJewel = 1 - probabilityGenerateDust;

	// Learning
	public static final int learnMeasuresCount = 5; // Default: 5
	public static final int learnFailuresCount = 3; // Default: 3
}
