package no.nav.foreldrepenger.melding.verge;

public class Verge {

    private boolean brevTilSøker;
    private boolean brevTilVerge;
    private String fnr;

    public Verge(boolean brevTilSøker, boolean brevTilVerge, String fnr) {
        this.brevTilSøker = brevTilSøker;
        this.brevTilVerge = brevTilVerge;
        this.fnr = fnr;
    }

    public String getFnr() {
        return fnr;
    }

    public boolean isBrevTilSøker() {
        return brevTilSøker;
    }

    public boolean isBrevTilVerge() {
        return brevTilVerge;
    }

    public boolean brevTilBegge() {
        return brevTilVerge && brevTilSøker;
    }
}
