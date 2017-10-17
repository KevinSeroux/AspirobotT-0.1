package pourCommencer.Environment;

public enum ActionType {
    // Actions spécifiques a l'agent
    VACUUM_DUST, // Aspire la saleté
    GATHER_JEWELRY, // Récolte les bijoux
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,

    // Actions spécifiques à l'environnement
    NEW_DUST,
    NEW_JEWELRY,
    VACCUM_JEWELRY // Spécifique à l'env car non souhaité par l'agent
}
