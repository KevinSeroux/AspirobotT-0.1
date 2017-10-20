package aspirobot.environnement;

import java.util.concurrent.ThreadLocalRandom;

import static aspirobot.Config.maxGenerationTime;
import static aspirobot.Config.probabilityGenerateDust;

// This class places random dust and jewel in the environment
public class EnvSimulator implements Runnable {
    private ThreadLocalRandom random;
    private _Environment env;

    public EnvSimulator(_Environment env) {
        this.env = env;
    }

    @Override
    public void run() {
        random = ThreadLocalRandom.current();

        while(true) {
            waitNextGeneration();
            Position newObjPos = generatePosition();
            chooseObjectByMonteCarlo(newObjPos);
        }
    }

    private void waitNextGeneration() {
        long nextGenerationTime = random.nextLong(maxGenerationTime);

        try {
            Thread.sleep(nextGenerationTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Position generatePosition() {
        int size = env.getSize();

        int x = random.nextInt(size);
        int y = random.nextInt(size);

        return new Position(x, y);
    }

    //TODO On pensait mettre des limites (genre 50% pour la dust et 20% pour les bijoux -Max
    private void chooseObjectByMonteCarlo(Position pos) {
        double draw = random.nextDouble(1);
        if(draw <= probabilityGenerateDust)
            generateDustAt(pos);
        else
            generateJewelAt(pos);
    }

    private void generateJewelAt(Position pos) {
        this.env.placeJewelAt(pos);
    }

    private void generateDustAt(Position pos) {
        this.env.placeDustAt(pos);
    }
}
