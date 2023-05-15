package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.vilkår.VilkårType;

public final class SvpMapperUtil {

    private SvpMapperUtil() {
    }

    public static List<SvpUttakResultatPeriode> hentUttaksperioder(Optional<SvangerskapspengerUttak> svpUttaksresultat) {
        return svpUttaksresultat.map(SvangerskapspengerUttak::getUttakResultatArbeidsforhold)
            .orElse(Collections.emptyList())
            .stream()
            .flatMap(ura -> ura.getPerioder().stream())
            .toList();
    }

    public static String leggTilLovreferanse(Avslagsårsak avslagsårsak) {
        var vilkårTyper = VilkårType.getVilkårTyper(avslagsårsak);
        return vilkårTyper.stream().map(vt -> vt.getLovReferanse(FagsakYtelseType.SVANGERSKAPSPENGER)).findFirst().orElse("");
    }

    public static Optional<LocalDate> finnFørsteAvslåtteUttakDato(List<SvpUttakResultatPeriode> uttaksperioder, Behandlingsresultat behandlingsresultat) {
        return finnMinsteDatoFraAvslåttUttak(uttaksperioder).or(behandlingsresultat::getSkjæringstidspunkt);

    }

    public static Optional<LocalDate> finnMinsteDatoFraAvslåttUttak(List<SvpUttakResultatPeriode> perioder) {
        return finnMinsteFraDatoAvslåttUttak(perioder).or(() ->finnMinsteFraDatoFraInnvilgetUttak(perioder));
    }

    private static Optional<LocalDate> finnMinsteFraDatoAvslåttUttak(List<SvpUttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> PeriodeResultatType.AVSLÅTT.equals(p.getPeriodeResultatType()))
            .map(p -> p.getTidsperiode().getFomDato())
            .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnMinsteFraDatoFraInnvilgetUttak(List<SvpUttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> PeriodeResultatType.INNVILGET.equals(p.getPeriodeResultatType()))
            .map(p -> p.getTidsperiode().getFomDato())
            .min(LocalDate::compareTo);
    }

    public static int finnAntallArbeidsgivere(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Inntektsmeldinger iay) {
        var antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.getPerioder().stream())
            .map(SvpUttakResultatPeriode::getArbeidsgiverNavn)
            .distinct()
            .count();
        if (antallArbeidsgivere == 0) {
            antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream().map(SvpUttakResultatArbeidsforhold::getArbeidsgiver).distinct().count();
        }
        if (antallArbeidsgivere == 0) {
            antallArbeidsgivere = (int) iay.getInntektsmeldinger().stream().map(Inntektsmelding::arbeidsgiverReferanse).distinct().count();
        }
        return antallArbeidsgivere;
    }

    public static Optional<LocalDate> finnOpphørsdato(List<TilkjentYtelsePeriode> tilkjentYtelsePerioder) {
        return  tilkjentYtelsePerioder.stream()
            .filter(tilkjentYtelsePeriode -> tilkjentYtelsePeriode.getDagsats() > 0)
            .map(TilkjentYtelsePeriode::getPeriodeTom)
            .max(LocalDate::compareTo)
            .map(localDate -> justerForHelg(localDate.plusDays(1)));
    }

    public static LocalDate justerForHelg(LocalDate date) {
        if (erLørdag(date)) {
            return date.plusDays(2);
        } else if (erSøndag(date)) {
            return date.plusDays(1);
        } else
            return date;
    }
    private static boolean erLørdag(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SATURDAY);
    }

    private static boolean erSøndag(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

}
