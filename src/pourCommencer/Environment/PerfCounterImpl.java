package pourCommencer.Environment;

import pourCommencer.Agent.Action;
import pourCommencer.Event;

import java.util.Observable;

import static pourCommencer.Agent.Action.VACUUM_DUST;

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
	public double get() {
		int nbPoussiereSurLePlateau = (nbDirtGenere-nbDirtAspire);
		int nbBijouxSurLePlateau = (nbJewelGenere-nbJewelAspire-nbJewelRamasse);
		int taillePlateau = manorSize*manorSize;
		return 100 * (
				(alpha*(taillePlateau-nbPoussiereSurLePlateau) + beta * (taillePlateau -nbBijouxSurLePlateau)) /
						((alpha*taillePlateau + beta * (taillePlateau -nbBijouxSurLePlateau)) + facteurAspirationBijoux *beta * (nbJewelAspire) + nbAction*coutAction));
	}

	@Override
	public void update(Observable observable, Object o) {
		Event event = (Event) o;

		switch(event) {
			case DUST_GENERATED:
				nbDirtGenere++;
				break;

			case DUST_VACCUMED:
				nbDirtAspire++;
				nbAction++;
				break;

			case JEWELRY_GENERATED:
				nbJewelGenere++;
				break;

			case JEWELRY_GATHERED:
				nbJewelRamasse++;
				nbAction++;
				break;

			case JEWELRY_VACCUMED:
				nbJewelAspire++;
				nbAction++;
				break;

			case AGENT_MOVED:
				nbAction++;
				break;

			case AGENT_HIT_NORTH:
				nbAction++;
				break;

			case AGENT_HIT_SOUTH:
				nbAction++;
				break;

			case AGENT_HIT_WEST:
				nbAction++;
				break;

			case AGENT_HIT_EST:
				nbAction++;
				break;

			case USELESS_ACTION:
				nbAction++;
				break;
		}
	}
}
