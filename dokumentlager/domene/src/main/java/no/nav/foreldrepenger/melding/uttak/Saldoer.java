package no.nav.foreldrepenger.melding.uttak;

import java.util.Set;

public class Saldoer {

    private final Set<Stønadskonto> stønadskontoer;

    public Saldoer(Set<Stønadskonto> stønadskontoer) {
        this.stønadskontoer = stønadskontoer;
    }

    public Set<Stønadskonto> getStønadskontoer() {
        return stønadskontoer;
    }
}
