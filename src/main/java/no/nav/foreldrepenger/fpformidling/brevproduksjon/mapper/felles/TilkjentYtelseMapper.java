package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

public final class TilkjentYtelseMapper {

    private TilkjentYtelseMapper() {
    }

    public static long finnMånedsbeløp(TilkjentYtelseDagytelseDto tilkjentYtelse) {
        return finnFørsteInnvilgedePeriode(tilkjentYtelse).map(TilkjentYtelseMapper::getMånedsbeløp).orElse(0L);
    }

    public static long finnDagsats(TilkjentYtelseDagytelseDto tilkjentYtelse) {
        return finnFørsteInnvilgedePeriode(tilkjentYtelse).map(TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto::dagsats).orElse(0);
    }

    public static int finnAntallArbeidsgivere(TilkjentYtelseDagytelseDto tilkjentYtelse) {
        return (int) tilkjentYtelse.perioder()
            .stream()
            .map(TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto::andeler)
            .flatMap(Collection::stream)
            .filter(andel -> TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER.equals(andel.aktivitetstatus()))
            .map(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto::arbeidsgiverReferanse)
            .filter(Objects::nonNull)
            .distinct()
            .count();
    }

    public static boolean harIngenRefusjon(TilkjentYtelseDagytelseDto tilkjentYtelseFP) {
        return (harBrukerAndel(tilkjentYtelseFP) && !harArbeidsgiverAndel(tilkjentYtelseFP)) || (!harBrukerAndel(tilkjentYtelseFP)
                && !harArbeidsgiverAndel(tilkjentYtelseFP));
    }

    public static boolean harDelvisRefusjon(TilkjentYtelseDagytelseDto tilkjentYtelseFP) {
        return harBrukerAndel(tilkjentYtelseFP) && harArbeidsgiverAndel(tilkjentYtelseFP);
    }

    public static boolean harFullRefusjon(TilkjentYtelseDagytelseDto tilkjentYtelseFP) {
        return !harBrukerAndel(tilkjentYtelseFP) && harArbeidsgiverAndel(tilkjentYtelseFP);
    }

    public static boolean harBrukerAndel(TilkjentYtelseDagytelseDto tilkjentYtelseFP) {
        return tilkjentYtelseFP.perioder()
            .stream()
            .map(TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto::andeler)
            .flatMap(List::stream)
            .anyMatch(a -> a.tilSoker() != null && a.tilSoker() != 0);
    }

    public static boolean harArbeidsgiverAndel(TilkjentYtelseDagytelseDto tilkjentYtelseFP) {
        return tilkjentYtelseFP.perioder()
            .stream()
            .map(TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto::andeler)
            .flatMap(List::stream)
            .anyMatch(TilkjentYtelseMapper::erArbeidsgiverMottaker);
    }

    private static boolean erArbeidsgiverMottaker(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto a) {
        return a.refusjon() != null && a.refusjon() != 0;
    }

    public static boolean harUtbetaling(TilkjentYtelseDagytelseDto tilkjentYtelseFP) {
        return tilkjentYtelseFP.perioder().stream().map(
            TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto::andeler).flatMap(List::stream).anyMatch(TilkjentYtelseMapper::harUtbetaling);
    }

    private static boolean harUtbetaling(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto a) {
        return (a.tilSoker() != null && a.tilSoker() > 0) || (a.refusjon() != null && a.refusjon() > 0);
    }

    public static int finnAntallRefusjonerTilArbeidsgivere(TilkjentYtelseDagytelseDto tilkjentYtelseFP) {
        return (int) tilkjentYtelseFP.perioder()
            .stream()
            .flatMap(periode -> periode.andeler().stream())
            .filter(TilkjentYtelseMapper::erArbeidsgiverMottaker)
            .map(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto::arbeidsgiverReferanse)
            .distinct()
            .count();
    }

    private static Optional<TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto> finnFørsteInnvilgedePeriode(TilkjentYtelseDagytelseDto tilkjentYtelse) {
        return tilkjentYtelse.perioder().stream().filter(harDagsatsOverNull()).min(Comparator.comparing(
            TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto::fom));
    }

    private static Predicate<TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto> harDagsatsOverNull() {
        return tilkjentYtelsePeriode -> tilkjentYtelsePeriode.dagsats() != null && tilkjentYtelsePeriode.dagsats() > 0;
    }

    private static long getMånedsbeløp(TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto førstePeriode) {
        return førstePeriode.dagsats() * 260 / 12;
    }
}
