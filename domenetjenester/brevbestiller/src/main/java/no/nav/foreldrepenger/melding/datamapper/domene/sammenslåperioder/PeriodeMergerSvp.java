package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

            AtomicLong sumDagsats = new AtomicLong();
            AtomicInteger count = new AtomicInteger();

            perioder.subList(index + 1, perioder.size()).stream()
                    .filter(periode -> erLikeHvaBrevAngår(periodeEn, periode))
                    .mapToLong(SvpUttakResultatPeriode::getAktivitetDagsats)
                    .forEach(value -> {
                        sumDagsats.getAndAdd(value);
                        count.getAndIncrement();
                    });

            if (count.get() > 0) { ;
                resultat.add(SvpUttakResultatPeriode.ny(periodeEn).medAktivitetDagsats(sumDagsats.get()).build());
            }
            index = count.get() + 1;

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
