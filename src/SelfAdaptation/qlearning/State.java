package SelfAdaptation.qlearning;

import java.util.Objects;

public class State {

    private final int posX;
    private final int posY;

    public State(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosX() {
        return posX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return posX == state.posX &&
                posY == state.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    @Override
    public String toString() {
        return "x:" + posX + ",y:" + posY;
    }
}
