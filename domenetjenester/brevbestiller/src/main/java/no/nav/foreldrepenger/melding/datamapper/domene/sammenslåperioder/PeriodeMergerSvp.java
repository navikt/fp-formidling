package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.datamapper.domene.svp.SVPUtbetalingsperiodeInnvilgelse;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;

import java.util.List;
import java.util.Objects;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.erFomRettEtterTomDato;

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

    public static boolean erPerioderSammenhengendeOgSkalSlåSammen(SVPUtbetalingsperiodeInnvilgelse periodeEn, BeregningsresultatPeriode periodeTo) {
        boolean sammeDagsats = Objects.equals(periodeEn.dagsats(), periodeTo.getDagsats());
        boolean sammeUtbetaltTilSoker = Objects.equals(periodeEn.utbetaltTilSoker(), periodeTo.getUtbetaltTilSoker());
        return sammeDagsats && sammeUtbetaltTilSoker &&
                erFomRettEtterTomDato(periodeEn.periode().getTomDato(), periodeTo.getPeriode().getFomDato());
    }
}
