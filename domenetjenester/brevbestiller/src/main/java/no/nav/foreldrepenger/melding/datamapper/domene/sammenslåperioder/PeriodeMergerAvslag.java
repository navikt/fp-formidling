package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.slåSammenPerioder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AvslagsAarsakType;

public class PeriodeMergerAvslag {

    private PeriodeMergerAvslag() {
    }

    public static List<AvslagsAarsakType> mergePerioder(List<AvslagsAarsakType> avSlåttePerioder) {
        if (avSlåttePerioder.size() <= 1) {
            return avSlåttePerioder; // ikke noe å se på.
        }
        return slåSammenSammenhengendePerioder(avSlåttePerioder);
    }

    private static List<AvslagsAarsakType> slåSammenSammenhengendePerioder(List<AvslagsAarsakType> perioder) {
        List<AvslagsAarsakType> resultat = new ArrayList<>();
        for (int index = 0; index < perioder.size() - 1; index++) {
            boolean sistePeriode = (index == perioder.size() - 2);
            AvslagsAarsakType periodeEn = perioder.get(index);
            AvslagsAarsakType periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeOgSkalSlåSammen(periodeEn, periodeTo)) {
                AvslagsAarsakType nyPeriode = slåSammenPerioder(periodeEn, periodeTo);
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

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(AvslagsAarsakType periodeEn, AvslagsAarsakType periodeTo) {
        return sammeAvslagsÅrsak(periodeEn, periodeTo) && erFomRettEtterTomDato(periodeEn, periodeTo);
    }

    private static boolean sammeAvslagsÅrsak(AvslagsAarsakType periodeEn, AvslagsAarsakType periodeTo) {
        return Objects.equals(periodeEn.getAvslagsAarsakKode(), periodeTo.getAvslagsAarsakKode());
    }
}
