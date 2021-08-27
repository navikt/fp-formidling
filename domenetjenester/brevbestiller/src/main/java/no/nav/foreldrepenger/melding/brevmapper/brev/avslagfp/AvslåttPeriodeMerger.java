package no.nav.foreldrepenger.melding.brevmapper.brev.avslagfp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.melding.brevmapper.brev.felles.DatoVerktøy;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;

public class AvslåttPeriodeMerger {
    public static List<AvslåttPeriode> mergePerioder(List<AvslåttPeriode> avslåttePerioder) {
        if (avslåttePerioder.size() <= 1) {
            return avslåttePerioder; // ikke noe å se på.
        }
        return slåSammenSammenhengendePerioder(avslåttePerioder);
    }

    private static List<AvslåttPeriode> slåSammenSammenhengendePerioder(List<AvslåttPeriode> perioder) {
        List<AvslåttPeriode> resultat = new ArrayList<>();
        for (int index = 0; index < perioder.size() - 1; index++) {
            boolean sistePeriode = (index == perioder.size() - 2);
            AvslåttPeriode periodeEn = perioder.get(index);
            AvslåttPeriode periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeOgSkalSlåSammen(periodeEn, periodeTo)) {
                AvslåttPeriode nyPeriode = slåSammenPerioder(periodeEn, periodeTo);
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
        return sammeAvslagsÅrsak(periodeEn, periodeTo) && DatoVerktøy.erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom());
    }

    private static boolean sammeAvslagsÅrsak(AvslåttPeriode periodeEn, AvslåttPeriode periodeTo) {
        return Objects.equals(periodeEn.getAvslagsårsak(), periodeTo.getAvslagsårsak());
    }

    private static AvslåttPeriode slåSammenPerioder(AvslåttPeriode periodeEn, AvslåttPeriode periodeTo) {
        return AvslåttPeriode.ny()
                .medAvslagsårsak(periodeEn.getAvslagsårsak())
                .medPeriodeFom(periodeEn.getPeriodeFom(), periodeEn.getSpråkkode())
                .medPeriodeTom(periodeTo.getPeriodeTom(), periodeEn.getSpråkkode())
                .medAntallTapteDager(periodeEn.getAntallTapteDager() + periodeTo.getAntallTapteDager())
                .build();
    }
}
