package SelfAdaptation.qlearning;

import java.util.Objects;

public class Action {

    private final int transmission_power; // 1 - 15
    private final int spreading_factor; // 1 - 12
    private final int sampling_rate; // 15 - 24

    @Override
    public String toString() {
        return transmission_power + ":" + spreading_factor + ":" + sampling_rate;
    }

    public Action(int sampling_rate, int spreading_factor, int transmission_power) {
        this.transmission_power = transmission_power;
        this.spreading_factor = spreading_factor;
        this.sampling_rate = sampling_rate;
    }

    public int getTransmission_power() {
        return transmission_power;
    }

    public int getSpreading_factor() {
        return spreading_factor;
    }

    public int getSampling_rate() {
        return sampling_rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return transmission_power == action.transmission_power &&
                spreading_factor == action.spreading_factor &&
                sampling_rate == action.sampling_rate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transmission_power, spreading_factor, sampling_rate);
    }
}
