package SelfAdaptation.ppo;

public class Config {
    private int tp_begin;
    private int tp_end;
    private int sf_begin;
    private int sf_end;
    private int sr_begin;
    private int sr_end;

    Config(int tp_begin, int tp_end, int sf_begin, int sf_end, int sr_begin, int sr_end) {
        this.tp_begin = tp_begin;
        this.tp_end = tp_end;
        this.sf_begin = sf_begin;
        this.sf_end = sf_end;
        this.sr_begin = sr_begin;
        this.sr_end = sr_end;
    }

    public int getTp_begin() {
        return tp_begin;
    }

    public int getTp_end() {
        return tp_end;
    }

    public int getSf_begin() {
        return sf_begin;
    }

    public int getSf_end() {
        return sf_end;
    }

    public int getSr_begin() {
        return sr_begin;
    }

    public int getSr_end() {
        return sr_end;
    }

    @Override
    public String toString() {
        return tp_begin + "," + tp_end + "," + sf_begin + "," + sf_end + "," + sr_begin + "." + sr_end;
    }
}
