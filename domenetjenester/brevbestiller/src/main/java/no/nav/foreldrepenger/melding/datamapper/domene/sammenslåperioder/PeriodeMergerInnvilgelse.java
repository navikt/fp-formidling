package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.likeAktiviteter;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.sammeStatusOgÅrsak;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.slåSammenPerioder;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;

public class PeriodeMergerInnvilgelse {

    static List<String> nonEqualKoderSomLikevelOppfyllerMerge =
            List.of("2001", "2002", "2003", "2004", "2005", "2006", "2007");

    private PeriodeMergerInnvilgelse() {
    }

    public static List<PeriodeType> mergePerioder(List<PeriodeType> perioder) {
        if (perioder.size() <= 1) {
            return perioder; // ikke noe å se på.
        }
        return slåSammenSammenhengendePerioder(perioder);
    }

    private static List<PeriodeType> slåSammenSammenhengendePerioder(List<PeriodeType> perioder) {
        List<PeriodeType> resultat = new ArrayList<>();
        if (perioder.isEmpty()) {
            return resultat;
        }
        for (int index = 0; index < perioder.size() - 1; index++) {
            boolean sistePeriode = (index == perioder.size() - 2);
            PeriodeType periodeEn = perioder.get(index);
            PeriodeType periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeOgSkalSlåSammen(periodeEn, periodeTo)) {
                PeriodeType nyPeriode = slåSammenPerioder(periodeEn, periodeTo);
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

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(PeriodeType periodeEn, PeriodeType periodeTo) {
        return sammeStatusOgÅrsak(periodeEn, periodeTo) && likeAktiviteter(periodeEn, periodeTo) && erFomRettEtterTomDato(periodeEn, periodeTo);
    }
}
