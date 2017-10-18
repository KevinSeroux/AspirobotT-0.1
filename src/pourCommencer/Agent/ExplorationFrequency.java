package pourCommencer.Agent;

import java.util.concurrent.ThreadLocalRandom;

public class ExplorationFrequency {
	private static final int EXAMPLE_COUNT = 5;

	private ThreadLocalRandom random;

	private double bestExploFreq;
	private double bestExploFreqSlope;
	private double randomExploFreq;
	private double randomExploSlope;
	private double lastMeasure;

	private int remainingLearnExample;
	private int remainingFailCount;

	public ExplorationFrequency(double initialValue) {
		remainingFailCount = 3;
		bestExploFreq = randomExploFreq = initialValue;
		random = ThreadLocalRandom.current();
		reset();
	}

	public boolean isTraining() {
		return remainingFailCount >= 0;
	}

	public double get() {
		double retExploFreq;

		if(isTraining())
			retExploFreq = randomExploFreq;
		else
			retExploFreq = bestExploFreq;

		return retExploFreq;
	}

	// Ajoute une mesure de perf d'exploration
	void addMeasure(double currentMeasure) {
		// If there is no more failed allowed, do nothing
		if(isTraining()) {
			// Repeat 4x times from the second time
			if(1 <= remainingLearnExample && remainingLearnExample <= 4)
				appendMeasureToSlope(currentMeasure);
			lastMeasure = currentMeasure;

			remainingLearnExample--;
			if(remainingLearnExample == 0) {
				// If the random freq gave better results, keep it
				updateBestFrequency();
				randomExploFreq = random.nextDouble(0.1, 1);
				reset();
			}
		}
	}

	private void appendMeasureToSlope(double currentMeasure) {
		double diff = currentMeasure - lastMeasure;
		randomExploSlope += diff;
	}

	private void updateBestFrequency() {
		if(randomExploSlope > bestExploFreqSlope) {
			bestExploFreq = randomExploFreq;
			bestExploFreqSlope = randomExploSlope;
		} else
			remainingFailCount--;
	}

	private void reset() {
		remainingLearnExample = EXAMPLE_COUNT;
		randomExploSlope = 0;
	}

	// Test
	public static void main(String[] args) throws Exception {
		final double[][] perfMeasures = {
			{1, 2, 3, 2, 3}, // Best
			{10, 11, 9, 10, 11},
			{10, 9, 8, 7, 6}, // Worst
		};

		ExplorationFrequency exploFreq = new ExplorationFrequency(1);
		double expectedBestFreq = exploFreq.get();

		for(int i = 0; i < 3; i++) {
			double freq = exploFreq.get();
			for(int j = 0; j < 5; j++) {
				if(exploFreq.get() != freq)
					throw new Exception();

				exploFreq.addMeasure(perfMeasures[i][j]);
			}
		}

		// Force the end of the training
		exploFreq.remainingFailCount = 0;
		// Check best is still returned
		if(exploFreq.get() != expectedBestFreq)
			throw new Exception();
	}
}
