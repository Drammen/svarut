package no.kommune.bergen.soa.svarut;

public class DispatchRateConfig {
    private int altinn=0;
    private int epost=0;
    private int post=0;

    public DispatchRateConfig(int altinn, int epost, int post) {
        this.altinn = altinn;
        this.epost = epost;
        this.post = post;
    }

    public int getAltinn() {
        return altinn;
    }

    public int getEpost() {
        return epost;
    }

    public int getPost() {
        return post;
    }
}
