package no.nav.foreldrepenger.melding.uttak;

import java.util.Set;

public class Saldoer {

    private final Set<Stønadskonto> stønadskontoer;
    private final int tapteDagerFpff;

    public Saldoer(Set<Stønadskonto> stønadskontoer, int tapteDagerFpff) {
        this.stønadskontoer = stønadskontoer;
        this.tapteDagerFpff = tapteDagerFpff;
    }

    public Set<Stønadskonto> getStønadskontoer() {
        return stønadskontoer;
    }

    public int getTapteDagerFpff() {
        return tapteDagerFpff;
    }
}
