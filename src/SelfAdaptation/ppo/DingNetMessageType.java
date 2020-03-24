package SelfAdaptation.ppo;

public enum DingNetMessageType {

    INIT("INIT"),
    ACTION("ACTION"),
    RESET("RESET"),
    END("END"),
    OK("OK"),
    ERROR("ERROR");

    private final String representation;

    DingNetMessageType(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static DingNetMessageType fromTypeString(final String typeString) throws Exception {
        switch(typeString) {
            case "INIT":
                return INIT;
            case "ACTION":
                return ACTION;
            case "RESET":
                return RESET;
            case "END":
                return END;
            case "OK":
                return OK;
            case "ERROR":
                return ERROR;
            default:
                throw new Exception("typeString part does not match a message type: '" + typeString + "'.");
        }
    }
}
