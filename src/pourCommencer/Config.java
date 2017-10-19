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


	//Recherche non informée
    public final static int COUT_ACTION = 1;
    public final static int GAIN_DUST = 10;
    public final static int GAIN_JEWEL = 20;
    public final static int PERTE_ASPI_JEWEL = 40;

}
