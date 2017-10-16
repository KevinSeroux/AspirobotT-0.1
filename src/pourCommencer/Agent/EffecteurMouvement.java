package pourCommencer.Agent;

import pourCommencer.Controler.Action;
import pourCommencer.Controler.Case;
import pourCommencer.Controler.EnvObject;
import pourCommencer.Controler._Controler;

public class EffecteurMouvement {

    private _Controler controler;

    public EffecteurMouvement(_Controler controler) {
        this.controler = controler;
    }

    //TODO Je pense qu'on est pas obligé de gérer l'EnvObject ici, mais plutot appeler le controler et retourner la nouvelle position
    public Case deplacement( Case[][] env, Case currentPosition, Action a){
        if (controler.majEnv(a,currentPosition)){
            switch (a){
                case MOVEUP:
                    env[currentPosition.x][currentPosition.y].removeEnvObject(EnvObject.ROBOT);
                    env[currentPosition.x-1][currentPosition.y].addEnvObject(EnvObject.ROBOT);
                    return env[currentPosition.x-1][currentPosition.y];
                case MOVEDOWN:
                    env[currentPosition.x][currentPosition.y].removeEnvObject(EnvObject.ROBOT);
                    env[currentPosition.x+1][currentPosition.y].addEnvObject(EnvObject.ROBOT);
                    return  env[currentPosition.x+1][currentPosition.y];
                case MOVELEFT:
                    env[currentPosition.x][currentPosition.y].removeEnvObject(EnvObject.ROBOT);
                    env[currentPosition.x][currentPosition.y-1].addEnvObject(EnvObject.ROBOT);
                    return env[currentPosition.x][currentPosition.y-1];
                case MOVERIGHT:
                    env[currentPosition.x][currentPosition.y].removeEnvObject(EnvObject.ROBOT);
                    env[currentPosition.x][currentPosition.y+1].addEnvObject(EnvObject.ROBOT);
                    return env[currentPosition.x][currentPosition.y+1];
                default:
                    return currentPosition;
            }
        }
        return currentPosition;
    }
}
