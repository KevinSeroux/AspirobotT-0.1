package pourCommencer.Environment;

import java.util.Observable;

public class PerfCounterImpl extends PerformanceCounter {
	private final static double alpha = 3;
	private final static double beta = 5;
	private final static int facteurAspirationBijoux = 2;
	private final static double coutAction = 1;

	private int manorSize;
	private int nbDirtGenere = 0;
	private int nbDirtAspire = 0;
	private int nbJewelGenere = 0;
	private int nbJewelRamasse = 0;
	private int nbJewelAspire = 0;
	private int nbAction = 0;

	public PerfCounterImpl(int manorSize) {
		this.manorSize = manorSize;
	}

	@Override
	public double getPerformance() {
		return 100 * (
				(alpha*(manorSize*manorSize-(nbDirtGenere-nbDirtAspire)) + beta * (manorSize*manorSize -(nbJewelGenere-nbJewelAspire-nbJewelRamasse))) /
						((alpha*manorSize*manorSize + beta * (manorSize*manorSize -(nbJewelGenere-nbJewelAspire-nbJewelRamasse))) + facteurAspirationBijoux *beta * (nbJewelAspire) + nbAction*coutAction));
	}

	@Override
	public void update(Observable observable, Object o) {
		Action envObject = (Action)o;
		boolean isAgentAction = true;

		switch(envObject.type) {
			case NEW_DUST:
				isAgentAction = false;
				nbDirtGenere++;
				break;

			case VACUUM_DUST:
				nbDirtAspire++;
				break;

			case VACCUM_JEWELRY:
				nbJewelAspire++;
				break;

			case NEW_JEWELRY:
				isAgentAction = false;
				nbJewelGenere++;
				break;

			case GATHER_JEWELRY:
				nbJewelRamasse++;
				break;
		}

		if(isAgentAction) nbAction++;
	}
}
