package no.nav.foreldrepenger.melding.behandling;

public class Innsyn {
    private InnsynResultatType innsynResultatType;

    public Innsyn(InnsynResultatType innsynResultatType) {
        this.innsynResultatType = innsynResultatType;
    }

    public InnsynResultatType getInnsynResultatType() {
        return innsynResultatType;
    }
}
