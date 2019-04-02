package no.nav.foreldrepenger.melding.uttak;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UttakResultatPerioder {
    private List<UttakResultatPeriode> perioder = new ArrayList<>();

    public List<UttakResultatPeriode> getPerioder() {
        return perioder.stream().sorted(Comparator.comparing(UttakResultatPeriode::getFom)).collect(Collectors.toList());
    }
}
