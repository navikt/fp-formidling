package no.nav.foreldrepenger.melding.ytelsefordeling;

public class OppgittRettighet {
    private boolean harAleneomsorgForBarnet;

    public OppgittRettighet(boolean harAleneomsorgForBarnet) {
        this.harAleneomsorgForBarnet = harAleneomsorgForBarnet;
    }

    public boolean isHarAleneomsorgForBarnet() {
        return harAleneomsorgForBarnet;
    }
}
