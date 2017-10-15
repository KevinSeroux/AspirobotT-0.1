import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Manor extends Thread {
    public final static int MANOR_SIZE = 10;

    private Case environnement[][] = new Case[MANOR_SIZE][MANOR_SIZE];
    private double performance;
    private long timeMaxToGenerate;

    public Manor(long timeMaxToGenerate) {
        performance = 0;
        for (int i = 0; i < MANOR_SIZE; i++) {
            for (int j = 0; j < MANOR_SIZE; j++) {
                environnement[i][j] = new Case(i, j);
            }
        }
        this.timeMaxToGenerate = timeMaxToGenerate;
    }

    @Override
    public String toString() {
        String representation = "";
        for (int j = 0; j < MANOR_SIZE; j++) {
            representation+="-----";
        }
        representation+="\n";
        for (int i = 0; i < MANOR_SIZE; i++) {
            for (int j = 0; j < MANOR_SIZE; j++) {
                representation+=environnement[i][j] + "|";
            }
            representation+="\n";
            for (int j = 0; j < MANOR_SIZE; j++) {
                representation+="-----";
            }
            representation+="\n";
        }
        return representation;
    }

    public boolean majEnv(Action a, Case c){
        if(a == Action.ASPIRE){
            if (c.containsEnvObject(EnvObject.DUST)){
                ajouterPerf(1);
                c.removeEnvObject(EnvObject.DUST);
            }
            if (c.containsEnvObject(EnvObject.JEWELRY)) {
                baisserPerf(2);
                c.removeEnvObject(EnvObject.JEWELRY);
            }
            return true;
        }
        else if(a == Action.GATHER){
            if (c.containsEnvObject(EnvObject.JEWELRY)) {
                ajouterPerf(1);
                c.removeEnvObject(EnvObject.JEWELRY);
            }
            return true;
        }
        else if (a == Action.MOVEU){
            if (c.x == 0) return false;
            environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
            environnement[c.x+1][c.y].addEnvObject(EnvObject.ROBOT);
            return true;
        }
        else if (a == Action.MOVED){
            if (c.x == MANOR_SIZE-1) return false;
            environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
            environnement[c.x-1][c.y].addEnvObject(EnvObject.ROBOT);

            return true;
        }
        else if (a == Action.MOVEL){
            if (c.y == 0) return false;
            environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
            environnement[c.x][c.y-1].addEnvObject(EnvObject.ROBOT);
            return true;
        }
        else if (a == Action.MOVER){
            if (c.y == MANOR_SIZE-1) return false;
            environnement[c.x][c.y].removeEnvObject(EnvObject.ROBOT);
            environnement[c.x][c.y+1].addEnvObject(EnvObject.ROBOT);
            return true;
        }
        return false;

    }

    private void baisserPerf(int i) {
        this.performance-=i;
    }

    private void ajouterPerf(int i) {
        this.performance+=i;
    }

    @Override
    public void run() {
        Random ri = new Random();
        Random rj = new Random();
        Random ro = new Random();
        int i;
        int j;
        int o;
        while(true){
            i = ri.nextInt(MANOR_SIZE);
            j = rj.nextInt(MANOR_SIZE);
            o = ro.nextInt(10);
            if(o<=7) environnement[i][j].addEnvObject(EnvObject.DUST);
            else environnement[i][j].addEnvObject(EnvObject.JEWELRY);
            System.out.println(this);
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(timeMaxToGenerate));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
