package aspirobot.environnement;

import aspirobot.agent.Action;
import aspirobot.Event;
import aspirobot.EventType;

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
	public double get() {
		int nbPoussiereSurLePlateau = (nbDirtGenere-nbDirtAspire);
		int nbBijouxSurLePlateau = (nbJewelGenere-nbJewelAspire-nbJewelRamasse);
		int taillePlateau = manorSize*manorSize;
		return 100 * (
				(alpha*(taillePlateau-nbPoussiereSurLePlateau) + beta * (taillePlateau -nbBijouxSurLePlateau)) /
						((alpha*taillePlateau + beta * (taillePlateau -nbBijouxSurLePlateau)) + facteurAspirationBijoux *beta * (nbJewelAspire) + nbAction*coutAction));
	}


	public double getSimulated(Action action) {

		int nbDirtAspire2=nbDirtAspire;
		int nbDirtGenere2=nbDirtGenere;
		int nbAction2 = nbAction;
		int nbJewelRamasse2=nbJewelRamasse;
		int nbJewelGenere2=nbJewelGenere;
		int nbJewelAspire2=nbJewelAspire;



		switch(action) {

			case VACUUM_DUST:

				nbDirtAspire2++;
				nbDirtGenere2--;
				nbAction2++;
				break;

			case VACUUM_JEWELRY:

				nbJewelAspire2++;
				nbJewelGenere2--;
				nbAction2++;
				break;

			case GATHER_JEWELRY:

				nbJewelRamasse2++;
				nbJewelGenere2--;
				nbAction2++;
				break;

			case MOVE_DOWN:
				nbAction2++;
				break;

			case MOVE_LEFT:
				nbAction2++;
				break;

			case MOVE_RIGHT:
				nbAction2++;
				break;

			case MOVE_UP:
				nbAction2++;
				break;

			default:
				nbAction2++;
				break;
		}


		int nbPoussiereSurLePlateau = (nbDirtGenere-nbDirtAspire);
		int nbBijouxSurLePlateau = (nbJewelGenere-nbJewelAspire-nbJewelRamasse);
		int taillePlateau = manorSize*manorSize;
		return 100 * (
				(alpha*(taillePlateau-nbPoussiereSurLePlateau) + beta * (taillePlateau -nbBijouxSurLePlateau)) /
						((alpha*taillePlateau + beta * (taillePlateau -nbBijouxSurLePlateau)) + facteurAspirationBijoux *beta * (nbJewelAspire) + nbAction*coutAction));
	}

	// This method should be called when the environment is updated
	@Override
	public void update(Observable observable, Object o) {
		Event event = (Event) o;
		if(event.getType() == EventType.AGENT)
			nbAction++;

		switch(event) {
			case DUST_GENERATED:
				nbDirtGenere++;
				break;

			case DUST_VACCUMED:
				nbDirtAspire++;
				break;

			case JEWELRY_GENERATED:
				nbJewelGenere++;
				break;

			case JEWELRY_GATHERED:
				nbJewelRamasse++;
				break;

			case JEWELRY_VACCUMED:
				nbJewelAspire++;
				break;
		}
	}
}
