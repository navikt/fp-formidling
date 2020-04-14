package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.erFomRettEtterTomDato;

import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;

public class PeriodeMergerSvp {

    private PeriodeMergerSvp() {
    }


    public static List<SvpUttakResultatPeriode> mergeSammenhengendePerioder(List<SvpUttakResultatPeriode> uttakResultatPerioder) {
        if (uttakResultatPerioder.size() <= 1) {
            return uttakResultatPerioder; // ikke noe å se på.
        }
        return uttakResultatPerioder; // TODO
    }


    public static boolean erPerioderSammenhengendeOgSkalSlåSammen(SvpUttakResultatPeriode periodeEn, SvpUttakResultatPeriode periodeTo) {
        boolean sammeUtbetalingsgrad = (periodeEn.getUtbetalingsgrad() == periodeTo.getUtbetalingsgrad());
        return sammeUtbetalingsgrad &&
                erFomRettEtterTomDato(periodeEn.getTom().toLocalDate(), periodeTo.getFom().toLocalDate());
    }

    public static boolean erPerioderSammenhengendeOgSkalSlåSammen(BeregningsresultatPeriode periodeEn, BeregningsresultatPeriode periodeTo) {
        boolean sammeDagsats = Objects.equals(periodeEn.getDagsats(), periodeTo.getDagsats());
        boolean sammeUtbetaltTilSoker = Objects.equals(periodeEn.getUtbetaltTilSoker(), periodeTo.getUtbetaltTilSoker());
        return sammeDagsats && sammeUtbetaltTilSoker &&
                erFomRettEtterTomDato(periodeEn.getPeriode().getTomDato(), periodeTo.getPeriode().getFomDato());
    }
}
