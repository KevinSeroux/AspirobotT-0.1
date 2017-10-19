package pourCommencer.Environment;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Case {

    private Set<EnvObject> object;
    private final Position pos;

    public Case(Position pos) {
        this.pos = pos;

        // Make the collection thread-safe
        this.object = Collections.newSetFromMap(
            new ConcurrentHashMap<EnvObject, Boolean>()
        );
    }

    public Case(Case c) {
        this(c.pos);
        this.object.addAll(c.object);
    }

    public boolean addEnvObject(EnvObject e) {
        if (object.contains(e)) return false;
        return object.add(e);
    }

    public boolean removeEnvObject(EnvObject e) {
        return object.remove(e);
    }

    public boolean containsEnvObject(EnvObject e) {
        return object.contains(e);
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();

        for (EnvObject e: object) {
            representation.append(e.getDescription());
        }
        while (representation.length() < 4){
            representation.append(' ');
        }

        return representation.toString();
    }
}
