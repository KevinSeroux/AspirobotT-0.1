package pourCommencer.Agent;

import java.util.concurrent.ThreadLocalRandom;

import static pourCommencer.Config.learnFailuresCount;
import static pourCommencer.Config.learnMeasuresCount;

/* This class allows the agent to learn to adjust
 * the exploration frequency */
public class ExplorationFrequency {
	private ThreadLocalRandom random;

	private double bestExploFreq;
	private double bestExploFreqSlope;
	private double randomExploFreq;
	private double randomExploSlope;
	private double lastMeasure;

	private int remainingLearnExample;
	private int remainingFailCount;

	public ExplorationFrequency(double initialValue) {
		remainingFailCount = learnFailuresCount;
		bestExploFreq = randomExploFreq = initialValue;
		bestExploFreqSlope = Double.NEGATIVE_INFINITY;
		random = ThreadLocalRandom.current();
		reset();
	}

	public boolean isTraining() {
		return remainingFailCount > 0;
	}

	public double get() {
		double retExploFreq;

		if(isTraining())
			retExploFreq = randomExploFreq;
		else
			retExploFreq = bestExploFreq;

		return retExploFreq;
	}

	// Add a performance measure
	void addMeasure(double currentMeasure) {
		// If there is no more failed allowed, do nothing
		if(isTraining()) {
			/* Repeat 4x times from the second time
			 * What we do is add:
			 * (measure 3 - measure 2) + (measure 4 - measure 3) + ... */
			if(1 <= remainingLearnExample && remainingLearnExample <= 4)
				appendMeasureToSlope(currentMeasure);
			/* We need to keep the current measure for the next time
			 * the method is called */
			lastMeasure = currentMeasure;

			remainingLearnExample--;
			if(remainingLearnExample == 0) {
				// If the random freq gave better results, keep it
				updateBestFrequency();
				randomExploFreq = random.nextDouble(0.01, 1);
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
		remainingLearnExample = learnMeasuresCount;
		randomExploSlope = 0;
	}

	// An unit test to ensure the learning work
	public static void main(String[] args) throws Exception {
		final double[][] perfMeasures = {
			{10, 11, 9, 10, 11},
			{1, 2, 3, 2, 3}, // Best
			{10, 9, 8, 7, 6}, // Worst
			{7, 5, 6, 6, 6},
		};

		ExplorationFrequency exploFreq = new ExplorationFrequency(1);
		exploFreq.remainingFailCount = 2;
		double expectedBestFreq = Double.NEGATIVE_INFINITY;

		for(int i = 0; i < 4; i++) {
			double freq = exploFreq.get();
			if(i == 1)
				expectedBestFreq = freq;

			for(int j = 0; j < 5; j++) {
				if(exploFreq.get() != freq)
					throw new Exception();

				exploFreq.addMeasure(perfMeasures[i][j]);
			}
		}

		// Check best is still returned
		if(exploFreq.get() != expectedBestFreq)
			throw new Exception();
	}
}
