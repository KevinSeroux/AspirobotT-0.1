package pourCommencer.Controler;

public interface _Controler {
    public boolean majEnv(Action a, Case c);
    public double getPerformance();
    //TODO changer cette méthode pour la vue
    public Case[][] getEnvironnement();
}
