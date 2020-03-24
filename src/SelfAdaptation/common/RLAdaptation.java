package SelfAdaptation.common;

import IotDomain.Pair;
import IotDomain.Simulation;

import java.util.ArrayList;

public interface RLAdaptation {

    State getLastState();
    Action getLastAction();
    ArrayList<Pair<State, Action>> getStateActionPairList();
    void setSimulation(Simulation simulation);
    void incrementCurrentRun();
    void reset();
    boolean shouldReset();
    void stop();
    void start();
    float getEpisodeReward();
}
