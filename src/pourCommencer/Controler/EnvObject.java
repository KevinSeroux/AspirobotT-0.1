package pourCommencer.Controler;

public enum EnvObject {
    DUST("D"),
    JEWELRY("J"),
    BASKET("B"),
    ROBOT("R");

    private final String description;

    private EnvObject(String value){
        description = value;
    }

    public String getDescription() {
        return description;
    }
}
