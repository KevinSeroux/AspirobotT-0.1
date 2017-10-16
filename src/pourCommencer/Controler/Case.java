package pourCommencer.Controler;

import pourCommencer.Controler.EnvObject;

import java.util.HashSet;
import java.util.Set;

public class Case {

    private Set<EnvObject> object;
    public final int x;
    public final int y;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.object = new HashSet<>();
    }

    public Case(Case c){
        this.object = new HashSet<>();
        for (EnvObject e :
                c.object) {
            this.object.add(e);
        }
        this.x=c.x;
        this.y=c.y;
    }

    public boolean addEnvObject(EnvObject e){
        if (object.contains(e)) return false;
        return object.add(e);
    }

    public boolean removeEnvObject(EnvObject e){
        return object.remove(e);
    }

    public boolean containsEnvObject(EnvObject e){
        return object.contains(e);
    }

    @Override
    public String toString() {
        String representation = "";
        for (EnvObject e:object) {
            representation += e.getDescription();
        }
        while (representation.length() < 4){
            representation+=" ";
        }
        return representation;
    }
}
