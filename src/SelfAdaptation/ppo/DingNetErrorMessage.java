package SelfAdaptation.ppo;

public class DingNetErrorMessage {

    private int xpos;
    private String errorMessage;

    public DingNetErrorMessage(int xpos, String errorMessage) {
        this.xpos = xpos;
        this.errorMessage = errorMessage;
    }

    public int getXpos() {
        return xpos;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return xpos + "," + errorMessage;
    }
}
