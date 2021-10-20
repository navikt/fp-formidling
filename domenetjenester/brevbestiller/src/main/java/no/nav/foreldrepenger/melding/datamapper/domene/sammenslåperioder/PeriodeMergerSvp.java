package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.erFomRettEtterTomDato;

import java.util.Objects;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.datamapper.domene.svp.SVPUtbetalingsperiodeInnvilgelse;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;

public class PeriodeMergerSvp {

    private PeriodeMergerSvp() {
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
