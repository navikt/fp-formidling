package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;


import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatoVerktøy.erFomRettEtterTomDato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;

public final class UtbetalingsperiodeMapper {

    public static List<Utbetalingsperiode> mapUtbetalingsperioder(List<TilkjentYtelsePeriode> tilkjentYtelsePerioder, Språkkode språkkode) {
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        tilkjentYtelsePerioder.forEach(tilkjentYtelsePeriode -> {
            if (tilkjentYtelsePeriode.getDagsats() > 0) {
                if (utbetalingsperioder.isEmpty() || !erPerioderSammenhengendeOgSkalSlåSammen(getSisteUtbetalingsperiode(utbetalingsperioder), tilkjentYtelsePeriode)) {
                    var utbetalingsperiode = opprettUtbetalingsperiode(språkkode, tilkjentYtelsePeriode,
                            tilkjentYtelsePeriode.getPeriodeFom());
                    utbetalingsperioder.add(utbetalingsperiode);
                } else {
                    var sammenhengendeFom = getSisteUtbetalingsperiode(utbetalingsperioder).getPeriodeFom();
                    var utbetalingsperiode = opprettUtbetalingsperiode(språkkode, tilkjentYtelsePeriode, sammenhengendeFom);
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

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(Utbetalingsperiode periodeEn, TilkjentYtelsePeriode periodeTo) {
        var sammeDagsats = Objects.equals(periodeEn.getPeriodeDagsats(), periodeTo.getDagsats());
        var sammeUtbetaltTilSoker = Objects.equals(periodeEn.getUtbetaltTilSøker(), periodeTo.getUtbetaltTilSøker());
        return sammeDagsats && sammeUtbetaltTilSoker &&
                erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriode().getFomDato());
    }

    private static Utbetalingsperiode getSisteUtbetalingsperiode(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.get(utbetalingsperioder.size() - 1);
    }

    private static Utbetalingsperiode opprettUtbetalingsperiode(Språkkode språkkode, TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                                LocalDate periodeFom) {
        return Utbetalingsperiode.ny()
                .medPeriodeFom(periodeFom, språkkode)
                .medPeriodeTom(tilkjentYtelsePeriode.getPeriodeTom(), språkkode)
                .medPeriodeDagsats(tilkjentYtelsePeriode.getDagsats())
                .medUtbetaltTilSøker(tilkjentYtelsePeriode.getUtbetaltTilSøker())
                .build();
    }
}
