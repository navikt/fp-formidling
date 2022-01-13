package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public final class TilkjentYtelseMapper {

    public static long finnMånedsbeløp(TilkjentYtelseForeldrepenger tilkjentYtelse) {
        return finnFørsteInnvilgedePeriode(tilkjentYtelse).map(TilkjentYtelseMapper::getMånedsbeløp).orElse(0L);
    }

    public static long finnDagsats(TilkjentYtelseForeldrepenger tilkjentYtelse) {
        return finnFørsteInnvilgedePeriode(tilkjentYtelse).map(TilkjentYtelsePeriode::getDagsats).orElse(0L);
    }

    public static int finnAntallArbeidsgivere(TilkjentYtelseForeldrepenger tilkjentYtelse) {
        return (int) tilkjentYtelse.getPerioder().stream()
                .map(TilkjentYtelsePeriode::getAndeler)
                .flatMap(Collection::stream)
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus()))
                .map(TilkjentYtelseAndel::getArbeidsgiver)
                .flatMap(Optional::stream)
                .map(Arbeidsgiver::arbeidsgiverReferanse)
                .distinct()
                .count();
    }

    public static boolean harIngenRefusjon(TilkjentYtelseForeldrepenger tilkjentYtelseFP) {
        return (harBrukerAndel(tilkjentYtelseFP) && !harArbeidsgiverAndel(tilkjentYtelseFP)) ||
                (!harBrukerAndel(tilkjentYtelseFP) && !harArbeidsgiverAndel(tilkjentYtelseFP));
    }

    public static boolean harDelvisRefusjon(TilkjentYtelseForeldrepenger tilkjentYtelseFP) {
        return harBrukerAndel(tilkjentYtelseFP) && harArbeidsgiverAndel(tilkjentYtelseFP);
    }

    public static boolean harFullRefusjon(TilkjentYtelseForeldrepenger tilkjentYtelseFP) {
        return !harBrukerAndel(tilkjentYtelseFP) && harArbeidsgiverAndel(tilkjentYtelseFP);
    }

    public static boolean harBrukerAndel(TilkjentYtelseForeldrepenger tilkjentYtelseFP) {
        return tilkjentYtelseFP.getPerioder().stream()
                .map(TilkjentYtelsePeriode::getAndeler)
                .flatMap(List::stream)
                .anyMatch(TilkjentYtelseAndel::erBrukerMottaker);
    }

    public static boolean harArbeidsgiverAndel(TilkjentYtelseForeldrepenger tilkjentYtelseFP) {
        return tilkjentYtelseFP.getPerioder().stream()
                .map(TilkjentYtelsePeriode::getAndeler)
                .flatMap(List::stream)
                .anyMatch(TilkjentYtelseAndel::erArbeidsgiverMottaker);
    }

    public static boolean harUtbetaling(TilkjentYtelseForeldrepenger tilkjentYtelseFP) {
        return tilkjentYtelseFP.getPerioder().stream()
                .map(TilkjentYtelsePeriode::getAndeler)
                .flatMap(List::stream)
                .anyMatch(a -> a.getDagsats() > 0);
    }

    public static int finnAntallRefusjonerTilArbeidsgivere(TilkjentYtelseForeldrepenger tilkjentYtelseFP) {
        return (int) tilkjentYtelseFP.getPerioder().stream()
                .flatMap(periode -> periode.getAndeler().stream())
                .filter(TilkjentYtelseAndel::erArbeidsgiverMottaker)
                .map(tilkjentYtelseAndel -> tilkjentYtelseAndel.getArbeidsgiver().map(Arbeidsgiver::arbeidsgiverReferanse)
                        .orElse(tilkjentYtelseAndel.getArbeidsforholdRef() != null ? tilkjentYtelseAndel.getArbeidsforholdRef().getReferanse()
                                : "ukjent")) // om ikke annet som en sikring i test-miljøer.
                .distinct().count();
    }

    private static Optional<TilkjentYtelsePeriode> finnFørsteInnvilgedePeriode(TilkjentYtelseForeldrepenger tilkjentYtelse) {
        return tilkjentYtelse.getPerioder()
                .stream()
                .filter(harDagsatsOverNull())
                .min(Comparator.comparing(TilkjentYtelsePeriode::getPeriodeFom));
    }

    private static Predicate<TilkjentYtelsePeriode> harDagsatsOverNull() {
        return tilkjentYtelsePeriode -> tilkjentYtelsePeriode.getDagsats() != null && tilkjentYtelsePeriode.getDagsats() > 0;
    }

    private static long getMånedsbeløp(TilkjentYtelsePeriode førstePeriode) {
        return førstePeriode.getDagsats() * 260 / 12;
    }
}
