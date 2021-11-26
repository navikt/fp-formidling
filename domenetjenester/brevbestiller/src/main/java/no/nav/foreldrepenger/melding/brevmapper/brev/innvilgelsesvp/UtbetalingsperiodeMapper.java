package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;


import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.erFomRettEtterTomDato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Utbetalingsperiode;

public final class UtbetalingsperiodeMapper {

    public static List<Utbetalingsperiode> mapUtbetalingsperioder(List<BeregningsresultatPeriode> beregningsresultatPerioder, Språkkode språkkode) {
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        beregningsresultatPerioder.forEach(beregningsresultatPeriode -> {
            if (beregningsresultatPeriode.getDagsats() > 0) {
                if (utbetalingsperioder.isEmpty() || !erPerioderSammenhengendeOgSkalSlåSammen(getSisteUtbetalingsperiode(utbetalingsperioder), beregningsresultatPeriode)) {
                    Utbetalingsperiode utbetalingsperiode = opprettUtbetalingsperiode(språkkode, beregningsresultatPeriode,
                            beregningsresultatPeriode.getBeregningsresultatPeriodeFom());
                    utbetalingsperioder.add(utbetalingsperiode);
                } else {
                    var sammenhengendeFom = getSisteUtbetalingsperiode(utbetalingsperioder).getPeriodeFom();
                    Utbetalingsperiode utbetalingsperiode = opprettUtbetalingsperiode(språkkode, beregningsresultatPeriode, sammenhengendeFom);
                    utbetalingsperioder.remove(utbetalingsperioder.size()-1);
                    utbetalingsperioder.add(utbetalingsperiode);
                }
            }
        });
        return utbetalingsperioder;
    }

    public static LocalDate finnStønadsperiodeTom(List<Utbetalingsperiode> utbetalingsperioder) {
        return getSisteUtbetalingsperiode(utbetalingsperioder).getPeriodeTom();
    }

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(Utbetalingsperiode periodeEn, BeregningsresultatPeriode periodeTo) {
        boolean sammeDagsats = Objects.equals(periodeEn.getPeriodeDagsats(), periodeTo.getDagsats());
        boolean sammeUtbetaltTilSoker = Objects.equals(periodeEn.getUtbetaltTilSøker(), periodeTo.getUtbetaltTilSoker());
        return sammeDagsats && sammeUtbetaltTilSoker &&
                erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriode().getFomDato());
    }

    private static Utbetalingsperiode getSisteUtbetalingsperiode(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.get(utbetalingsperioder.size() - 1);
    }

    private static Utbetalingsperiode opprettUtbetalingsperiode(Språkkode språkkode, BeregningsresultatPeriode beregningsresultatPeriode,
                                                                LocalDate periodeFom) {
        return Utbetalingsperiode.ny()
                .medPeriodeFom(periodeFom, språkkode)
                .medPeriodeTom(beregningsresultatPeriode.getBeregningsresultatPeriodeTom(), språkkode)
                .medPeriodeDagsats(beregningsresultatPeriode.getDagsats())
                .medUtbetaltTilSøker(beregningsresultatPeriode.getUtbetaltTilSoker())
                .build();
    }
}
