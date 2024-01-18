package no.nav.foreldrepenger.fpformidling.domene.uttak.fp;

import java.util.Comparator;
import java.util.List;

public record ForeldrepengerUttak(List<UttakResultatPeriode> perioder, List<UttakResultatPeriode> perioderAnnenPart, boolean aleneomsorg,
                                  boolean annenForelderHarRett, boolean annenForelderRettEØS, boolean oppgittAnnenForelderRettEØS) {

    @Override
    public List<UttakResultatPeriode> perioder() {
        return fomSorted(perioder);
    }

    @Override
    public List<UttakResultatPeriode> perioderAnnenPart() {
        return fomSorted(perioderAnnenPart);
    }


    private static List<UttakResultatPeriode> fomSorted(List<UttakResultatPeriode> perioder) {
        return perioder.stream().sorted(Comparator.comparing(UttakResultatPeriode::getFom)).toList();
    }

    public static ForeldrepengerUttak tomtUttak() {
        return new ForeldrepengerUttak(List.of(), List.of(), false, false, false, false);
    }
}
