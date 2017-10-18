package pourCommencer.Environment;

public enum EnvObject {
    DUST("d"),
    JEWELRY("j"),
    ROBOT("R");

    private final String description;

    EnvObject(String value) {
        description = value;
    }

    public String getDescription() {
        return description;
    }
}
