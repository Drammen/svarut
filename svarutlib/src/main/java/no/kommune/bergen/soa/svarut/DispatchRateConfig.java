package no.kommune.bergen.soa.svarut;

public class DispatchRateConfig {
    private int altinn=0;
    private int norgedotno=0;
    private int epost=0;
    private int post=0;

    public DispatchRateConfig(int altinn, int norgedotno, int epost, int post) {
        this.altinn = altinn;
        this.norgedotno = norgedotno;
        this.epost = epost;
        this.post = post;
    }

    public int getAltinn() {
        return altinn;
    }

    public int getNorgedotno() {
        return norgedotno;
    }

    public int getEpost() {
        return epost;
    }

    public int getPost() {
        return post;
    }
}
