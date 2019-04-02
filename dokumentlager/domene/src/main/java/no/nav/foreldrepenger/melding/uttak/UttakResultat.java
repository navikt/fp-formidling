package no.nav.foreldrepenger.melding.uttak;

import java.util.ArrayList;
import java.util.List;

public class UttakResultat {
    private List<UttakResultatPeriode> perioder = new ArrayList<>();
    private UttakResultatPerioder overstyrtPerioder;
    private UttakResultatPerioder opprinneligPerioder;

    public UttakResultatPerioder getGjeldendePerioder() {
        if (overstyrtPerioder == null && opprinneligPerioder == null) {
            throw new IllegalStateException("Ingen uttaksperioder er satt");
        }
        return overstyrtPerioder != null ? overstyrtPerioder : opprinneligPerioder;
    }
}
