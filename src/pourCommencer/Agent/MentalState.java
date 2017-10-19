package pourCommencer.Agent;

import pourCommencer.Environment.EnvState;

import java.util.LinkedList;

//TODO
public class MentalState {
    EnvState beliefs;
    enum Desire { DEFAULT, DUST, JEWEL}
    Desire goal;
    LinkedList<Action> intentions;
}
