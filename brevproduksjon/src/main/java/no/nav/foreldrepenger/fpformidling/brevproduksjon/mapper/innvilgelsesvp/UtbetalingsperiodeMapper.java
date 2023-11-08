package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;


import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.UtbetalingsperiodeNy;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.UttakAktivitetMedPerioder;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;

public final class UtbetalingsperiodeMapper {
    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingsperiodeMapper.class);

    private UtbetalingsperiodeMapper() {
    }

    public static List<UttakAktivitetMedPerioder> mapUttakAktiviterMedUtbetPerioder(List<TilkjentYtelsePeriode> tilkjentPerioder, Språkkode språkkode) {
        List<UttakAktivitetMedPerioder> uttaksaktivitet = new ArrayList<>();
        List <UtbetalingsperiodeNy> utbetalingsperioder = new ArrayList<>();

        Set<String> alleAktiviteter = utledAlleAktiviteter(tilkjentPerioder);

        tilkjentPerioder.stream()
            .filter(tilkjentPeriode -> tilkjentPeriode.getDagsats() > 0)
            .forEach(tilkjentPeriode -> tilkjentPeriode.getAndeler()
                .forEach(andel -> utbetalingsperioder.add(opprettNyUtbetalingsperiode(tilkjentPeriode, andel, språkkode))));

        alleAktiviteter.forEach(aktivitet -> {
            var perioderPerAktivitet = utbetalingsperioder.stream().filter(periode-> periode.getAktivitetNavn().equals(aktivitet)).toList();
            uttaksaktivitet.add(new UttakAktivitetMedPerioder(aktivitet, perioderPerAktivitet));
        });

        var antallPerioderEtterMapping = uttaksaktivitet.stream().map(UttakAktivitetMedPerioder::utbetalingsperioder).mapToLong(Collection::size).sum();
        if (utbetalingsperioder.size() != antallPerioderEtterMapping) {
            //Noe er feil, logg og kast exception
            LOG.error("Utbetalingsperioder: {} Aktivitet og perioder: {}", utbetalingsperioder, uttaksaktivitet);
            throw new IllegalStateException("Antall utbetalingsperioder per andel og utbetalingsperioder per aktivitet stemmer ikke");
        }

        return uttaksaktivitet;
    }

    private static Set<String> utledAlleAktiviteter(List<TilkjentYtelsePeriode> tilkjentPerioder) {
        return tilkjentPerioder.stream()
            .filter(tilkjentPeriode -> tilkjentPeriode.getDagsats() > 0)
            .map(TilkjentYtelsePeriode::getAndeler)
            .flatMap(Collection::stream)
            .map(andel -> utledAktivitetsbeskrivelse(andel, andel.getAktivitetStatus()))
            .collect(Collectors.toSet());
    }

    public static LocalDate finnSisteStønadsdato(List<UtbetalingsperiodeNy> utbetalingsperioder) {
        return utbetalingsperioder.stream().map(UtbetalingsperiodeNy::getPeriodeTom).max(Comparator.naturalOrder()).orElse(null);

    }

    private static UtbetalingsperiodeNy opprettNyUtbetalingsperiode(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                                    TilkjentYtelseAndel andel,
                                                                    Språkkode språkkode) {
        return UtbetalingsperiodeNy.ny()
            .medPeriodeFom(tilkjentYtelsePeriode.getPeriodeFom(), språkkode)
            .medPeriodeTom(tilkjentYtelsePeriode.getPeriodeTom(), språkkode)
            .medDagsats(andel.getDagsats())
            .medUtbetaltTilSøker(andel.getUtbetalesTilBruker())
            .medUtbetalingsgrad(Prosent.of(andel.getUtbetalingsgrad()))
            .medAktivitetNavn(utledAktivitetsbeskrivelse(andel, andel.getAktivitetStatus()))
            .build();
    }

}
