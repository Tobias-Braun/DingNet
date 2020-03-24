package SelfAdaptation.ppo;

public class Observation {

    private int xpos;
    private float reward;

    public Observation(int xpos, float reward) {
        this.xpos = xpos;
        this.reward = reward;
    }

    public int getXpos() {
        return xpos;
    }

    public float getReward() {
        return reward;
    }

    @Override
    public String toString() {
        return xpos + "," + reward;
    }
}
