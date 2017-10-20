package aspirobot.agent;

import aspirobot.environnement.EnvState;

import java.util.LinkedList;

//TODO
public class MentalState {
    EnvState beliefs;
    enum Desire { DEFAULT, DUST, JEWEL}
    Desire goal;
    LinkedList<Action> intentions;
}
