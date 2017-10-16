package pourCommencer.Agent;

import pourCommencer.Controler.Action;
import pourCommencer.Controler.Case;
import pourCommencer.Controler.EnvObject;
import pourCommencer.Controler._Controler;

public class SensorVision {
    private _Controler controler;

    public SensorVision(_Controler controler) {
        this.controler = controler;
    }

    public Case[][] getCurrentEnvironement(){
        Case[][] env = controler.getEnvironnement();
        Case[][] copyEnv = new  Case[env.length][env.length];
        for (int i = 0; i < env.length; i++) {
            for (int j = 0; j < env.length; j++) {
                copyEnv[i][j] = new Case(env[i][j]);
            }
        }
        return copyEnv;
    }

    //TODO  je suis pas sur que ca doit se trouver dans cette classe.
    public boolean deplacementPossible(Case env, Action a, int tailleEnv){
        switch (a){
            case MOVEUP:
                if (env.x == 0) return false;
                return true;
            case MOVEDOWN:
                if (env.x == tailleEnv-1) return false;
                return true;
            case MOVELEFT:
                if (env.y == 0) return false;
                return true;
            case MOVERIGHT:
                if (env.y == tailleEnv-1) return false;
                return true;
            default:
                return false;
        }
    }

    public boolean caseIsDirty(Case c){
        if (c.containsEnvObject(EnvObject.DUST)) return true;
        return false;
    }

    public boolean caseHaveJewelery(Case c){
        if (c.containsEnvObject(EnvObject.JEWELRY)) return true;
        return false;
    }

}
