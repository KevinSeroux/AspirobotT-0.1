import java.util.ArrayList;

public class Case {

    private ArrayList<EnvObject> object;
    public final int x;
    public final int y;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.object = new ArrayList<EnvObject>();
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
