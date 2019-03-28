package no.nav.foreldrepenger.melding.uttak;

import java.util.HashSet;
import java.util.Set;

public class Stønadskontoberegning {
    private Set<Stønadskonto> stønadskontoer = new HashSet<>();

    public Set<Stønadskonto> getStønadskontoer() {
        return stønadskontoer;
    }
}
