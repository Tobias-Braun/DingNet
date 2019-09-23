package SelfAdaptation.qlearning;

import IotDomain.Gateway;
import IotDomain.LoraTransmission;
import IotDomain.Mote;
import IotDomain.NetworkEntity;
import IotDomain.Pair;
import SelfAdaptation.FeedbackLoop.GenericFeedbackLoop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QLearningAdaption extends GenericFeedbackLoop {

    private static double state_action_space_square = 2560000;

    private Pair<State, Action> lastStateActionPair;
    final HashMap<State, HashMap<Action, Float>> q_table;
    private final ArrayList<State> episodeList;
    private static final float alpha = 0.2f;
    private static final float gamma = 0f;
    private static final float epsilon = 0.15f;
    private final Random rand;
    private float complete_reward = 0;

    public QLearningAdaption() {
        super("Q-learning-Adaption");
        this.q_table = new HashMap<>();
        this.episodeList = new ArrayList<>();
        this.rand = new Random();
    }

    public double getEpsilon(int run) {
        return 1f / ((run * run / state_action_space_square) + 1);
    }

    @Override
    public String toString() {
        return "QLearningAdaption, filled with " + this.q_table.size() + " items";
    }

    @Override
    public void adapt(Mote mote, Gateway dataGateway) {
        State currentState = new State(mote.getXPos(), mote.getYPos());
        this.episodeList.add(currentState);
        float reward = calculateReward(mote, dataGateway);
        Action nextAction = chooseNextAction(currentState, mote.getEnvironment().getNumberOfRuns());
        Pair<State, Action> nextStateActionPair = new Pair<>(currentState, nextAction);
        updateQTable(nextStateActionPair, reward);
        this.lastStateActionPair = nextStateActionPair;
        getMoteEffector().setPower(mote, nextAction.getTransmission_power());
        getMoteEffector().setSpreadingFactor(mote, nextAction.getSpreading_factor());
        getMoteEffector().setSamplingRate(mote, nextAction.getSampling_rate());
    }

    public HashMap<State, HashMap<Action, Float>> getQTable() {
        return this.q_table;
    }

    public List<State> getEpisodeList() {
        return this.episodeList;
    }

    public Action chooseNextAction(State currentState, int run) {
        if (rand.nextFloat() >= getEpsilon(run)) {
            return chooseBestAction(currentState);
        } else {
            return chooseRandomAction();
        }
    }

    public Action chooseRandomAction() {
        final int transmission_power = -100;
        final int spreading_factor = 12; // 7 - 12
        final int sampling_rate = 10;
        return new Action(sampling_rate, spreading_factor, transmission_power);
    }

    public Action chooseBestAction(State currentState) {
        boolean noValuePresent = q_table.get(currentState) == null || q_table.get(currentState).isEmpty();
        if (noValuePresent) return chooseRandomAction();
        Map<Action, Float> actionValueMap = q_table.get(currentState);
        return actionValueMap.keySet()
                .stream()
                .max((o1, o2) -> (int) (actionValueMap.get(o1) * 10000 - actionValueMap.get(o2) * 10000)).get();
    }

    public float calculateReward(Mote mote, Gateway gateway) {
        if (lastStateActionPair == null) return 0;
        LinkedList<LoraTransmission> sentTransmissions = mote.getSentTransmissions(mote.getEnvironment().getNumberOfRuns() - 1);
        sentTransmissions.sort(new Comparator<LoraTransmission>() {
            @Override
            public int compare(LoraTransmission o1, LoraTransmission o2) {
                return o1.getDepartureTime().compareTo(o2.getDepartureTime());
            }
        });
        LoraTransmission lastTransmission = sentTransmissions.getLast();


        double reward;
        if (NetworkEntity.packetStrengthHighEnough(lastTransmission)) {
            reward = 1 * Math.pow(Math.E, -used_energy(lastTransmission));
        } else if (collision(gateway, lastTransmission)) {
            reward = 0;
        } else {
            reward = 0;
        }
        this.complete_reward += (float) reward;
        return (float) reward;
    }

    public boolean collision(Gateway receiver, LoraTransmission transmission) {
        if (lastStateActionPair == null || receiver.getReceivedTransmissions().getLast() == null) return false;
        var rec = receiver.getReceivedTransmissions();
        var last = rec.getLast();
        var get = last.get(receiver);
        return (get == null) ? false : get;
    }

    public double used_energy(LoraTransmission transmission) {
        if (lastStateActionPair == null) return 100;
        return Math.pow(10, (((double) this.lastStateActionPair.getRight().getTransmission_power()) / 10) * transmission.getTimeOnAir() / 1000);
    }

    public void updateQTable(Pair<State, Action> stateActionPair, float reward) {
        if (q_table.get(stateActionPair.getLeft()) == null) q_table.put(stateActionPair.getLeft(), new HashMap<>());
        q_table.get(stateActionPair.getLeft()).putIfAbsent(stateActionPair.getRight(), 1f);
        assignNewStateActionValue(stateActionPair, reward);
    }

    public void assignNewStateActionValue(Pair<State, Action> nextStateActionPair, float reward) {
        if (lastStateActionPair == null) return;
        q_table.get(lastStateActionPair.getLeft()).put(
                lastStateActionPair.getRight(),
                q_table.get(lastStateActionPair.getLeft()).get(lastStateActionPair.getRight())
                        + alpha * (reward + gamma
                        * q_table.get(nextStateActionPair.getLeft()).get(nextStateActionPair.getRight())
                        - q_table.get(lastStateActionPair.getLeft()).get(lastStateActionPair.getRight()))
        );
    }

    public float getCompleteReward() {
        return this.complete_reward;
    }

    public void resetEpisode() {
        this.complete_reward = 0;
        this.episodeList.clear();
    }
}
