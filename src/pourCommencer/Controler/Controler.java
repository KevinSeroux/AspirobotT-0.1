package pourCommencer.Controler;

import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Controler extends Observable implements Runnable, _Controler {

    private static final int facteurAspirationBijoux = 2;
    private final int manorSize;
    private final static int POURCENT_DIRT = 50;
    private final static int POURCENT_JEWEL = 10;
    private final static long timeMaxToGenerateDirt = 10000;
    private final static long timeMaxToGenerateJewel = 20000;
    private final static double alpha = 3;
    private final static double beta = 5;
    private final static double coutAction = 1;

    private Case environnement[][];
    private double performance;

    private int nbDirtGenere = 0;
    private int nbDirtAspire = 0;
    private int nbJewelGenere = 0;
    private int nbJewelRamasse = 0;
    private int nbJewelAspire = 0;
    private int nbAction = 0;

    public Controler(int manorSize, Case caseDepartRobot) {
        this.manorSize = manorSize;
        this.environnement = new Case[manorSize][manorSize];
        for (int i = 0; i < manorSize; i++) {
            for (int j = 0; j < manorSize; j++) {
                environnement[i][j] = new Case(i, j);
            }
        }
        environnement[caseDepartRobot.x][caseDepartRobot.y].addEnvObject(EnvObject.ROBOT);
        this.performance = 0;
    }

    public double getPerformance(){
        return 100 * (
                (alpha*(manorSize*manorSize-(nbDirtGenere-nbDirtAspire)) + beta * (manorSize*manorSize -(nbJewelGenere-nbJewelAspire-nbJewelRamasse))) /
                        ((alpha*manorSize*manorSize + beta * (manorSize*manorSize -(nbJewelGenere-nbJewelAspire-nbJewelRamasse))) + facteurAspirationBijoux *beta * (nbJewelAspire) + nbAction*coutAction));
    }

    @Override
    public void run() {
        Random ri = new Random();
        Random rj = new Random();
        Random ro = new Random();
        double nextDirtGeneration = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(timeMaxToGenerateDirt);
        double nextJewelGeneration = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(timeMaxToGenerateJewel);

        while(true){
            if(shouldBeANewDirtySpace(nextDirtGeneration)){
                GenerateDirt(ri.nextInt(manorSize),rj.nextInt(manorSize));
                nextDirtGeneration = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(timeMaxToGenerateDirt);
            }
            if(shouldBeANewLostJewel(nextJewelGeneration)){
                GenerateJewel(ri.nextInt(manorSize),rj.nextInt(manorSize));
                nextJewelGeneration = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(timeMaxToGenerateJewel);
            }
            notifyObservers();
        }
    }

    //TODO peut être un synchronized quelque part ...
    public boolean majEnv(Action a, Case ca) {
            Case c = environnement[ca.x][ca.y];
            if (a == Action.ASPIRE) {
                if (c.containsEnvObject(EnvObject.DUST)) {
                    nbDirtAspire++;
                    c.removeEnvObject(EnvObject.DUST);
                    this.setChanged();
                }
                if (c.containsEnvObject(EnvObject.JEWELRY)) {
                    nbJewelAspire++;
                    c.removeEnvObject(EnvObject.JEWELRY);
                    this.setChanged();
                }
                nbAction++;
                return true;
            } else if (a == Action.GATHER) {
                if (c.containsEnvObject(EnvObject.JEWELRY)) {
                    nbJewelRamasse++;
                    c.removeEnvObject(EnvObject.JEWELRY);
                    this.setChanged();
                }
                nbAction++;
                return true;
            } else if (a == Action.MOVEU) {
                if (c.x == 0) return false;
                environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
                environnement[c.x - 1][c.y].addEnvObject(EnvObject.ROBOT);
                nbAction++;
                this.setChanged();
                return true;
            } else if (a == Action.MOVED) {
                if (c.x == manorSize - 1) return false;
                environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
                environnement[c.x + 1][c.y].addEnvObject(EnvObject.ROBOT);
                nbAction++;
                this.setChanged();
                return true;
            } else if (a == Action.MOVEL) {
                if (c.y == 0) return false;
                environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
                environnement[c.x][c.y - 1].addEnvObject(EnvObject.ROBOT);
                nbAction++;
                this.setChanged();
                return true;
            } else if (a == Action.MOVER) {
                if (c.y == manorSize - 1) return false;
                environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
                environnement[c.x][c.y + 1].addEnvObject(EnvObject.ROBOT);
                nbAction++;
                this.setChanged();
                return true;
            }
            this.notifyObservers();
            return false;
    }



    private void GenerateJewel(int x, int y) {
        if(this.environnement[x][y].addEnvObject(EnvObject.JEWELRY)) {
            nbJewelGenere++;
            setChanged();
        }
    }

    private void GenerateDirt(int x, int y) {
        if(this.environnement[x][y].addEnvObject(EnvObject.DUST)){
            nbDirtGenere++;
            setChanged();
        }

    }

    //TODO don't work
    private boolean shouldBeANewLostJewel(double nextJewelGeneration) {
        if((System.currentTimeMillis() > nextJewelGeneration) &&
                ((manorSize*manorSize - (nbJewelGenere - nbJewelRamasse - nbJewelAspire)) > 0) &&
                    ((manorSize*manorSize - (nbJewelGenere - nbJewelRamasse - nbJewelAspire))/
                            (manorSize*manorSize) < POURCENT_JEWEL))
            return true;
        return false;
    }

    //TODO don't work
    private boolean shouldBeANewDirtySpace(double nextDirtGeneration) {
        if((System.currentTimeMillis() > nextDirtGeneration) &&
                ((manorSize*manorSize - (nbDirtGenere - nbDirtAspire)) > 0) &&
                    ((manorSize*manorSize - (nbDirtGenere - nbDirtAspire))/(manorSize*manorSize) < POURCENT_DIRT))
            return true;
        return false;
    }

    //TODO changer cette méthode pour la vue
    public Case[][] getEnvironnement() {
        return environnement;
    }
}
