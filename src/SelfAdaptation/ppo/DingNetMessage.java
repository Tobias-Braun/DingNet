package SelfAdaptation.ppo;

import SelfAdaptation.common.Action;

public class DingNetMessage {

    private DingNetMessageType messageType;
    private Action action;
    private Observation observation;
    private Config config;
    private DingNetErrorMessage errorMessage;

    DingNetMessage(DingNetMessageType messageType) {
        this.messageType = messageType;
    }

    private static Action getActionFromPayload(String payload) {
        String[] actionParameters = payload.split(",");
        Action action = new Action(-120, 7, 15);
        try {
            action = new Action(Integer.parseInt(actionParameters[0]), Integer.parseInt(actionParameters[1]), Integer.parseInt(actionParameters[2]));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(payload);
        }
        return action;
    }

    private static Config getconfigFromPayload(String payload) {
        String[] configParameters = payload.split(",");
        Config config = new Config(Integer.parseInt(configParameters[0]),
                Integer.parseInt(configParameters[1]),
                Integer.parseInt(configParameters[2]),
                Integer.parseInt(configParameters[3]),
                Integer.parseInt(configParameters[4]),
                Integer.parseInt(configParameters[5])
                );
        return config;
    }

    public DingNetMessageType getMessageType() {
        return messageType;
    }

    public Action getAction() {
        return action;
    }

    public Config getConfig() {
        return config;
    }

    public Observation getObservation() {
        return observation;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public void setErrorMessage(DingNetErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString() {
        switch (this.messageType) {
            case INIT:
                return this.messageType.getRepresentation() + ":" + this.config.toString() + "\n";
            case ACTION:
                return this.messageType.getRepresentation() + ":" + this.action.toString() + "\n";
            case RESET:
                return this.messageType.getRepresentation() + ":" + "\n";
            case END:
                return this.messageType.getRepresentation() + ":" + "\n";
            case OK:
                return this.messageType.getRepresentation() + ":" + this.observation.toString() + "\n";
            case ERROR:
                return this.messageType.getRepresentation() + ":" + this.errorMessage + "\n";
            default:
                return "";
        }
    }

    public static DingNetMessage fromMessage(String message) throws Exception {
        System.out.println(message);
        String[] innerParts = message.split(":");
        String messageTypeString = innerParts[0];
        String payload = innerParts.length == 2? innerParts[1] : "";
        DingNetMessageType messageType = DingNetMessageType.fromTypeString(messageTypeString);
        DingNetMessage dingNetMessage = new DingNetMessage(messageType);
        if (messageType == DingNetMessageType.ACTION) {
            dingNetMessage.setAction(getActionFromPayload(payload));
        }
        if (messageType == DingNetMessageType.INIT) {
            dingNetMessage.setConfig(getconfigFromPayload(payload));
        }
        if (messageType == DingNetMessageType.ERROR) {
            String[] payloadParts = payload.split(",");
            dingNetMessage.setErrorMessage(new DingNetErrorMessage(Integer.parseInt(payloadParts[0]), payloadParts[1]));
        }
        return dingNetMessage;
    }

    public static DingNetMessage createOkMessage(Observation observation) {
        DingNetMessage message = new DingNetMessage(DingNetMessageType.OK);
        message.setObservation(observation);
        return message;
    }

    public static DingNetMessage createErrorMessage(DingNetErrorMessage errorMessage) {
        DingNetMessage message = new DingNetMessage(DingNetMessageType.ERROR);
        message.setErrorMessage(errorMessage);
        return message;
    }
}


