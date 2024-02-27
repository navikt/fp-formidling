package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;


import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatoVerktøy.erFomRettEtterTomDato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.AktiviteterOgUtbetalingsperioder;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Utbetalingsperiode;

public final class UtbetalingsperiodeMapper {

    private UtbetalingsperiodeMapper() {
    }

    public static List<AktiviteterOgUtbetalingsperioder> mapUtbetalingsperioderPerAktivitet(List<TilkjentYtelsePeriode> tilkjentPerioder, Språkkode språkkode) {
        List<AktiviteterOgUtbetalingsperioder> aktiviteterOgUtbetalingsperioder = new ArrayList<>();
        List <Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        var gjeldendeTilkjentPerioder = fjernPerioderMedIngenDagsats(tilkjentPerioder);

        Set<String> aktiviteterIPerioden = utledAktiviteterFraPerioder(gjeldendeTilkjentPerioder);

        gjeldendeTilkjentPerioder
            .forEach(tilkjentPeriode -> tilkjentPeriode.getAndeler().stream()
                .filter(tilkjentYtelseAndel -> tilkjentYtelseAndel.getUtbetalingsgrad().compareTo(BigDecimal.ZERO) > 0)
                .forEach(andel -> utbetalingsperioder.add(opprettUtbetalingsperiode(tilkjentPeriode, andel, språkkode))));

        aktiviteterIPerioden.forEach(aktivitet -> {
            var perioderPerAktivitet = utbetalingsperioder.stream().filter(periode-> periode.getAktivitetNavn().equals(aktivitet)).toList();
            aktiviteterOgUtbetalingsperioder.add(new AktiviteterOgUtbetalingsperioder(aktivitet, slåSammenPerioderOmSammenhengende(new ArrayList<>(perioderPerAktivitet))));
        });

        return aktiviteterOgUtbetalingsperioder;
    }

    private static Utbetalingsperiode opprettUtbetalingsperiode(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                                TilkjentYtelseAndel andel,
                                                                Språkkode språkkode) {
        return Utbetalingsperiode.ny()
            .medPeriodeFom(tilkjentYtelsePeriode.getPeriodeFom(), språkkode)
            .medPeriodeTom(tilkjentYtelsePeriode.getPeriodeTom(), språkkode)
            .medDagsats(andel.getDagsats())
            .medUtbetaltTilSøker(andel.getUtbetalesTilBruker())
            .medUtbetalingsgrad(Prosent.of(andel.getUtbetalingsgrad()))
            .medAktivitetNavn(AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse(andel, andel.getAktivitetStatus()))
            .build();
    }

    private static List<Utbetalingsperiode> slåSammenPerioderOmSammenhengende(List<Utbetalingsperiode> perioder) {
        if (perioder.size() <= 1) {
            return perioder; // ikke noe å se på.
        }

        List<Utbetalingsperiode> resultat = new ArrayList<>();
        for (var index = 0; index < perioder.size() - 1; index++) {
            var sistePeriode = (index == perioder.size() - 2);
            var periodeEn = perioder.get(index);
            var periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeEllerLikeOgSkalSlåSammen(periodeEn, periodeTo)) {
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

    private static boolean erPerioderSammenhengendeEllerLikeOgSkalSlåSammen(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        var sammeUtbetalingsgrad = periodeEn.getUtbetalingsgrad().equals(periodeTo.getUtbetalingsgrad());
        var sammeDagsats = periodeEn.getDagsats() == periodeTo.getDagsats();
        var sammeUtbetaltTilSøker = periodeEn.getUtbetaltTilSøker() == periodeTo.getUtbetaltTilSøker();
        return sammeUtbetalingsgrad && sammeDagsats && sammeUtbetaltTilSøker && (erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom()) || erFomOgTomLike(periodeEn,
            periodeTo));
    }

    private static boolean erFomOgTomLike(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        return periodeEn.getPeriodeFom().equals(periodeTo.getPeriodeFom()) && periodeEn.getPeriodeTom().equals(periodeTo.getPeriodeTom());
    }
    private static Utbetalingsperiode slåSammenPerioder(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        return Utbetalingsperiode.ny()
            .medPeriodeFom(periodeEn.getPeriodeFom(), periodeEn.getSpråkkode())
            .medPeriodeTom(periodeTo.getPeriodeTom(), periodeEn.getSpråkkode())
            .medAktivitetNavn(periodeEn.getAktivitetNavn())
            .medDagsats(periodeEn.getDagsats())
            .medUtbetalingsgrad(periodeEn.getUtbetalingsgrad())
            .medUtbetaltTilSøker(periodeEn.getUtbetaltTilSøker())
            .build();
    }

    private static List<TilkjentYtelsePeriode> fjernPerioderMedIngenDagsats(List<TilkjentYtelsePeriode> tilkjentPerioder) {
        return tilkjentPerioder.stream()
            .filter(tilkjentPeriode -> tilkjentPeriode.getDagsats() > 0)
            .toList();
    }

    private static Set<String> utledAktiviteterFraPerioder(List<TilkjentYtelsePeriode> tilkjentPerioder) {
        return tilkjentPerioder.stream()
            .map(TilkjentYtelsePeriode::getAndeler)
            .flatMap(Collection::stream)
            .map(andel -> AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse(andel, andel.getAktivitetStatus()))
            .collect(Collectors.toSet());
    }

    public static LocalDate finnSisteStønadsdato(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.stream().map(Utbetalingsperiode::getPeriodeTom).max(Comparator.naturalOrder()).orElse(null);

    }

}
