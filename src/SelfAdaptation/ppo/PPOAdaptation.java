package SelfAdaptation.ppo;

import IotDomain.Gateway;
import IotDomain.LoraTransmission;
import IotDomain.Mote;
import IotDomain.NetworkEntity;
import IotDomain.Pair;
import SelfAdaptation.FeedbackLoop.GenericFeedbackLoop;
import SelfAdaptation.common.Action;
import SelfAdaptation.common.RLAdaptation;
import SelfAdaptation.common.State;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class PPOAdaptation extends GenericFeedbackLoop implements RLAdaptation {

    private boolean isRunning = false;
    private ServerSocket server;
    private DingNetConnection connection;
    private State lastState;
    private Action lastAction = null;
    private float last_reward;
    private float complete_reward = 0;
    private final ArrayList<Pair<State,Action>> stateActionPairList;
    private long lastEnd;


    public static void main(String[] args) {
        try {
            PPOAdaptation adaption = new PPOAdaptation();
            System.out.println("accepting");
            adaption.accept();
            System.out.println("accepted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PPOAdaptation() throws IOException {
        super("PPOAdaptation");
        this.stateActionPairList = new ArrayList<>();
        this.lastEnd = System.currentTimeMillis();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void accept() {
        this.connection = new DingNetConnection();
    }

    @Override
    public void adapt(Mote mote, Gateway dataGateway) {
        Pair<State,Action> currentStateActionPair = new Pair<>(new State(mote.getXPos(), mote.getYPos()),null);
        this.stateActionPairList.add(currentStateActionPair);
        float reward = calculateReward(mote, dataGateway);
        long beforeChooseAction = System.currentTimeMillis();
        currentStateActionPair.setRight(chooseNextAction(currentStateActionPair.getLeft(), reward));
        long afterChooseAction = System.currentTimeMillis();
        this.lastAction = currentStateActionPair.getRight();
        this.lastState = currentStateActionPair.getLeft();
        getMoteEffector().setPower(mote, currentStateActionPair.getRight().getTransmission_power());
        getMoteEffector().setSpreadingFactor(mote, currentStateActionPair.getRight().getSpreading_factor());
        getMoteEffector().setSamplingRate(mote, currentStateActionPair.getRight().getSampling_rate());
        long end = System.currentTimeMillis();
        System.out.println("time total: " + (end - this.lastEnd) + ", from chooseAction " + (afterChooseAction - beforeChooseAction));
        this.lastEnd = end;
    }

    public float calculateReward(Mote mote, Gateway gateway) {
        if (lastAction == null) return 0;
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
            reward = Math.pow(Math.E, -used_energy(lastTransmission));
        } else if (collision(gateway, lastTransmission)) {
            reward = 0;
        } else {
            reward = 0;
        }
        this.complete_reward += (float) reward;
        this.last_reward = (float) reward;
        return (float) reward;
    }


    public boolean collision(Gateway receiver, LoraTransmission transmission) {
        if (lastAction == null || receiver.getReceivedTransmissions().getLast() == null) return false;
        var rec = receiver.getReceivedTransmissions();
        var last = rec.getLast();
        var get = last.get(receiver);
        return (get == null) ? false : get;
    }

    public double used_energy(LoraTransmission transmission) {
        if (lastAction == null) return 100;
        return Math.pow(10, (((double) this.lastAction.getTransmission_power()) / 10) * transmission.getTimeOnAir() / 1000);
    }

    Action chooseNextAction(State currentState, float reward) {
        if (this.isRunning) {
            DingNetMessage message = DingNetMessage.createOkMessage(new Observation(currentState.getPosX(), reward));
            System.out.println(message);
            this.connection.sendMessage(message);
        } else {
            this.isRunning = true;
        }
        DingNetMessage recv = this.connection.receiveMessage();
        switch (recv.getMessageType()) {
            case ACTION:
                return recv.getAction();
            case RESET:
                return this.resetEnvAndContinue();
            default:
                System.err.println("error in chooseNextAction, received this message: " + recv);
                System.exit(-1);
        }
        return null;
    }

    Action resetEnvAndContinue() {
        this.setReset(true);
        DingNetMessage recv = this.connection.receiveMessage();
        switch (recv.getMessageType()) {
            case ACTION:
                return recv.getAction();
            case RESET:
                return this.resetEnvAndContinue();
            default:
                System.err.println("error in chooseNextAction, received this message: " + recv.toString());
                System.exit(-1);
        }
        return null;
    }

    @Override
    public State getLastState() {
        return this.lastState;
    }

    @Override
    public Action getLastAction() {
        return this.lastAction;
    }

    @Override
    public ArrayList<Pair<State, Action>> getStateActionPairList() {
        return this.stateActionPairList;
    }

    @Override
    public void reset() {
        this.reset = false;
        this.lastState = null;
        this.lastAction = null;
        this.complete_reward = 0;
        this.last_reward = 0;
        this.getStateActionPairList().clear();
    }

    @Override
    public boolean shouldReset() {
        return this.reset;
    }

    @Override
    public float getEpisodeReward() {
        return this.complete_reward;
    }
}
