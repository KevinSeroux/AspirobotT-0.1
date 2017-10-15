public class Robot extends Thread{
    private Case env[][];
    private Manor controleur;


    public Robot(Case[][] env, Manor controleur) {
        this.controleur = controleur;
        for (int i = 0; i < controleur.MANOR_SIZE; i++) {
            for (int j = 0; j < controleur.MANOR_SIZE; j++) {

            }
        }
    }
}
