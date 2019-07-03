package SelfAdaptation.machine_learning;

import IotDomain.Gateway;
import IotDomain.Mote;
import IotDomain.Pair;
import SelfAdaptation.FeedbackLoop.GenericFeedbackLoop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

public class QLearningAdaption extends GenericFeedbackLoop {

    private Pair<State, Action> lastStateActionPair;
    private final HashMap<Pair<State, Action>, Float> q_table ;
    private final ArrayList<State> state_list;
    private static final float alpha = 0.5f;
    private static final float gamma = 0.95f;
    private static final float epsilon = 0.15f;
    private int lastEnergyLevel;
    private final Random rand;

    public QLearningAdaption() {
        super("Q-learning-Adaption");
        this.lastEnergyLevel = 0;
        this.q_table = new HashMap<>();
        this.state_list = new ArrayList<>();
        this.lastStateActionPair = new Pair<>(new State(0,0), new Action(5, 5, 15));
        this.q_table.put(this.lastStateActionPair, 0f);
        this.rand = new Random();
    }

    @Override
    public String toString() {
        return "QLearningAdaption, filled with " + this.q_table.size() + " items";
    }

    @Override
    public void adapt(Mote mote, Gateway dataGateway) {
        State currentState = new State(mote.getXPos() - mote.getXPos() % 4, mote.getYPos() - mote.getYPos() % 4);
        boolean noneMatch = this.state_list.stream().noneMatch((state -> state.equals(currentState)));
        if (noneMatch) this.state_list.add(currentState);
        float reward = calculateReward(mote, dataGateway);
        Action nextAction = chooseNextAction(currentState);
        Pair<State, Action> nextStateActionPair = new Pair<>(currentState, nextAction);
        updateQTable(nextStateActionPair, reward);
        this.lastStateActionPair = nextStateActionPair;
        getMoteEffector().setPower(mote, nextAction.getTransmission_power());
        getMoteEffector().setSpreadingFactor(mote, nextAction.getSpreading_factor());
        getMoteEffector().setSamplingRate(mote, nextAction.getSampling_rate());
    }

    public void print_q_table() {
        for (Pair<State, Action> key : this.q_table.keySet()) {
            System.out.println(key.getLeft().toString() + "-" + key.getRight().toString() + " -> " + q_table.get(key).toString());
        }
    }

    private Action chooseNextAction(State currentState) {
        if (rand.nextFloat() >= epsilon) {
            return chooseBestAction(currentState);
        } else {
            return chooseRandomAction();
        }
    }

    private Action chooseRandomAction() {
        final int transmission_power = rand.nextInt(15) + 1;
        final int spreading_factor = rand.nextInt(12) + 1;
        final int sampling_rate = rand.nextInt(10) + 15;
        return new Action(transmission_power, spreading_factor, sampling_rate);
    }

    private Action chooseBestAction(State currentState) {
        boolean noValuePresent = !q_table.keySet()
                .stream()
                .anyMatch(p -> p.getLeft()
                .equals(currentState));
        if (noValuePresent) return chooseRandomAction();
        Optional<Pair<State, Action>> actionOptional = q_table.keySet()
                .stream()
                .filter(actionState -> actionState.getLeft().equals(currentState))
                .min((o1, o2) -> (int) (q_table.get(o1) * 1000 - q_table.get(o2) * 1000));
        return actionOptional.get().getRight();
    }

    private float calculateReward(Mote mote, Gateway gateway) {
        int energy_diff = lastEnergyLevel - mote.getEnergyLevel();
        double reward = getMoteProbe().getHighestReceivedSignal(mote) / (energy_diff + 1); // +1 to ensure non-zero divident
        return (float) reward;
    }

    private void updateQTable(Pair<State, Action> stateActionPair, float reward) {
        q_table.putIfAbsent(stateActionPair, 0f);
        assignNewStateActionValue(stateActionPair, reward);
    }

    private void assignNewStateActionValue(Pair<State, Action> nextStateActionPair, float reward) {
        q_table.put(lastStateActionPair, q_table.get(lastStateActionPair) + alpha * (reward + gamma * q_table.get(nextStateActionPair) - q_table.get(lastStateActionPair)));
    }
}
