package pourCommencer.Environment;

public enum EnvObject {
    DUST("d"),
    JEWELRY("j"),
    ROBOT("R");

    private final String description;

    private EnvObject(String value) {
        description = value;
    }

    public String getDescription() {
        return description;
    }
}
