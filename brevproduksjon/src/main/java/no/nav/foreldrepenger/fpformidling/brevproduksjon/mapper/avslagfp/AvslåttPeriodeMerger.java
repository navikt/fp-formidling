package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatoVerktøy.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak.erRegnetSomLike;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;

public class AvslåttPeriodeMerger {
    public static List<AvslåttPeriode> mergePerioder(List<AvslåttPeriode> avslåttePerioder) {
        if (avslåttePerioder.size() <= 1) {
            return avslåttePerioder; // ikke noe å se på.
        }
        return slåSammenSammenhengendePerioder(avslåttePerioder);
    }

    private static List<AvslåttPeriode> slåSammenSammenhengendePerioder(List<AvslåttPeriode> perioder) {
        List<AvslåttPeriode> resultat = new ArrayList<>();
        for (var index = 0; index < perioder.size() - 1; index++) {
            var sistePeriode = (index == perioder.size() - 2);
            var periodeEn = perioder.get(index);
            var periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeOgSkalSlåSammen(periodeEn, periodeTo)) {
                var nyPeriode = slåSammenPerioder(periodeEn, periodeTo);
                perioder.set(index + 1, nyPeriode);
                if (sistePeriode) {
                    resultat.add(nyPeriode);
                }
            } else {
                resultat.add(periodeEn);
                if (sistePeriode) {
                    resultat.add(periodeTo);
                }
            }
        }
        return resultat;
    }

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(AvslåttPeriode periodeEn, AvslåttPeriode periodeTo) {
        return sammeAvslagsårsak(periodeEn, periodeTo)
                && erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom());
    }

    private static boolean sammeAvslagsårsak(AvslåttPeriode periodeEn, AvslåttPeriode periodeTo) {
        return Objects.equals(periodeEn.getAvslagsårsak(), periodeTo.getAvslagsårsak())
                || erRegnetSomLike(periodeEn.getAvslagsårsak(), periodeTo.getAvslagsårsak());
    }

    private static AvslåttPeriode slåSammenPerioder(AvslåttPeriode periodeEn, AvslåttPeriode periodeTo) {
        return AvslåttPeriode.ny()
                .medAvslagsårsak(periodeEn.getAvslagsårsak())
                .medPeriodeFom(periodeEn.getPeriodeFom(), periodeEn.getSpråkkode())
                .medPeriodeTom(periodeTo.getPeriodeTom(), periodeEn.getSpråkkode())
                .medAntallTapteDager(finnRiktigAntallTapteDager(periodeEn, periodeTo), BigDecimal.ZERO)
                .build();
    }

    private static int finnRiktigAntallTapteDager(AvslåttPeriode periodeEn, AvslåttPeriode periodeTo) {
        var tapteDagerPeriodeEn = periodeEn.getTapteDagerTemp();
        var tapteDagerPeriodeTo = periodeTo.getTapteDagerTemp();

        if (!Objects.equals(tapteDagerPeriodeEn, BigDecimal.ZERO) && !Objects.equals(tapteDagerPeriodeTo, BigDecimal.ZERO)) {
            return  tapteDagerPeriodeEn.add(tapteDagerPeriodeTo).setScale(1, RoundingMode.DOWN).intValue();
        } else {
            return periodeEn.getAntallTapteDager() + periodeTo.getAntallTapteDager();
        }
    }
}
