package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;

public class PeriodeMergerSvp {

    private PeriodeMergerSvp() {
    }

    public static List<SvpUttakResultatPeriode> mergeLikePerioder(List<SvpUttakResultatPeriode> sortertePerioder) {
        if (sortertePerioder.size() <= 1) {
            return sortertePerioder; // ikke noe å se på.
        }
        return slåSammenLikePerioder(sortertePerioder);
    }

    public static List<SvpUttakResultatPeriode> mergeSammenhengendePerioder(List<SvpUttakResultatPeriode> uttakResultatPerioder) {
        if (uttakResultatPerioder.size() <= 1) {
            return uttakResultatPerioder; // ikke noe å se på.
        }
        return uttakResultatPerioder; // TODO
    }

    private static List<SvpUttakResultatPeriode> slåSammenLikePerioder(List<SvpUttakResultatPeriode> perioder) {
        List<SvpUttakResultatPeriode> resultat = new ArrayList<>();
        for (int index = 0; index < perioder.size() - 1;) {
            SvpUttakResultatPeriode periodeEn = perioder.get(index);
            Stream<SvpUttakResultatPeriode> stream = perioder.subList(index + 1, perioder.size()).stream()
                    .filter(periode -> erLikeHvaBrevAngår(periodeEn, periode));

            if (stream.count() > 0) {
                long sumDagsats = stream.mapToLong(SvpUttakResultatPeriode::getAktivitetDagsats).sum();
                resultat.add(SvpUttakResultatPeriode.ny(periodeEn).medAktivitetDagsats(sumDagsats).build());
            }
            index = (int) stream.count() + 1;

            if (index == perioder.size() - 1) {
                resultat.add(perioder.get(index));
            }
        }
        return resultat;
    }

    private static boolean erLikeHvaBrevAngår(SvpUttakResultatPeriode periodeEn, SvpUttakResultatPeriode periodeTo) {
        boolean sammeTidsperiode = Objects.equals(periodeEn.getTidsperiode(), periodeTo.getTidsperiode());
        boolean sammeUtbetalingsgrad = (periodeEn.getUtbetalingsgrad() == periodeTo.getUtbetalingsgrad());
        return sammeTidsperiode && sammeUtbetalingsgrad;
    }
}
