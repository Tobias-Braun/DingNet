package SelfAdaptation.FeedbackLoop;

import IotDomain.Gateway;
import IotDomain.Mote;
import IotDomain.Simulation;
import SelfAdaptation.Instrumentation.MoteEffector;
import SelfAdaptation.Instrumentation.MoteProbe;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;

/**
 * A class representing an adaptation approach for the simulation.
 */
public abstract class GenericFeedbackLoop {

    protected boolean reset;

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public boolean getReset() {
        return reset;
    }

    /**
     * A string representing the name of the approach.
     */
    @Model
    private String name;

    private Simulation simulation;

    /**
     * The mote probe used by the feedback loop.
     */
    private MoteProbe moteProbe;

    /**
     * The mote effector used by the feedback loop.
     */
    private MoteEffector moteEffector;

    private int currentRun;

    /**
     * A boolean to know if the feedback loop is active
     */
    private Boolean active;

    /**
     * A method describing what the approach should do when being called on a mote.
     * @param mote The mote to adapt.
     */
    public abstract void adapt(Mote mote, Gateway dataGateway);

    /**
     * A method to activate the feedback loop.
     */
    public void start() {
        this.active = true;
    }

    /**
     * A method to deactivate the feedback loop.
     */
    public void stop(){
        this.active = false;
    }

    /**
     * A method to check if the feedback loop is active.
     * @return true if the feedback loop is active.
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * A constructor generating an adaptationApproach with a given name.
     * @param name The name of the approach.
     */
    public GenericFeedbackLoop(String name){
        this.name =name;
        this.active = false;
        this.currentRun = 0;
    }

    /**
     * Returns the name of the approach.
     * @return The name of the approach.
     */
    @Basic
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the approach to a given string.
     * @param name The name of the approach.
     */
    @Basic
    public void setName(String name) {
        this.name = name;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    /**
     * Returns the mote probe used by this feedbackLoop.
     * @return The mote probe used by this feedbackLoop.
     */
    public MoteProbe getMoteProbe() {
        return moteProbe;
    }

    /**
     * Returns the mote effector used by this feedbackLoop.
     * @return The mote effector used by this feedbackLoop.
     */
    public MoteEffector getMoteEffector() {
        return moteEffector;
    }

    /**
     * Sets the mote probe and adds this feedback loop to the mote probe.
     * @param moteProbe the mote probe to set.
     */
    public void setMoteProbe(MoteProbe moteProbe) {
        this.moteProbe = moteProbe;
        getMoteProbe().setGenericFeedbackLoop(this);
    }

    public void incrementCurrentRun() {
        this.currentRun += 1;
    }

    /**
     * Sets the effector to the given effector.
     * @param moteEffector The effector to set.
     */
    public void setMoteEffector(MoteEffector moteEffector) {
        this.moteEffector = moteEffector;
    }
}

