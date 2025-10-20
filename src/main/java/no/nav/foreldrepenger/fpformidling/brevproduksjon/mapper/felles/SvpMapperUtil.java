package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Svangerskapspenger;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

public final class SvpMapperUtil {

    private static final String NÆRINGSDRIVENDE = "næringsdrivende";
    private static final String FRILANSER = "frilanser";

    private SvpMapperUtil() {
    }

    public static List<Svangerskapspenger.Uttaksperiode> hentUttaksperioder(Svangerskapspenger svpUttaksresultat) {
        return svpUttaksresultat.uttakArbeidsforhold().stream().flatMap(ura -> ura.perioder().stream()).toList();
    }

    public static String leggTilLovreferanse(Collection<VilkårType> vilkårFraBehandling, Avslagsårsak avslagsårsak) {
        return FellesMapper.lovhjemmelFraAvslagsårsak(FagsakYtelseType.SVANGERSKAPSPENGER, vilkårFraBehandling, avslagsårsak)
            .stream()
            .findFirst()
            .orElse("");
    }

    public static Optional<LocalDate> finnFørsteAvslåtteUttakDato(List<Svangerskapspenger.Uttaksperiode> uttaksperioder,
                                                                  BrevGrunnlagDto.Behandlingsresultat behandlingsresultat) {
        return finnMinsteDatoFraAvslåttUttak(uttaksperioder).or(() -> Optional.of(behandlingsresultat.skjæringstidspunkt().dato()));

    }

    public static Optional<LocalDate> finnMinsteDatoFraAvslåttUttak(List<Svangerskapspenger.Uttaksperiode> perioder) {
        return finnMinsteFraDatoAvslåttUttak(perioder).or(() -> finnMinsteFraDatoFraInnvilgetUttak(perioder));
    }

    private static Optional<LocalDate> finnMinsteFraDatoAvslåttUttak(List<Svangerskapspenger.Uttaksperiode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT.equals(p.periodeResultatType()))
            .map(Svangerskapspenger.Uttaksperiode::fom)
            .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnMinsteFraDatoFraInnvilgetUttak(List<Svangerskapspenger.Uttaksperiode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> BrevGrunnlagDto.PeriodeResultatType.INNVILGET.equals(p.periodeResultatType()))
            .map(Svangerskapspenger.Uttaksperiode::fom)
            .min(LocalDate::compareTo);
    }

    public static int finnAntallArbeidsgivere(List<Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                              List<BrevGrunnlagDto.Inntektsmelding> inntektsmeldinger,
                                              UnaryOperator<String> hentNavn) {
        var antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream()
            .map(uttakArbeidsforhold -> getArbeidsgiverNavn(uttakArbeidsforhold, uttakArbeidsforhold.arbeidsgiverReferanse(), hentNavn))
            .distinct()
            .count();
        if (antallArbeidsgivere == 0) {
            antallArbeidsgivere = (int) inntektsmeldinger.stream().map(BrevGrunnlagDto.Inntektsmelding::arbeidsgiverReferanse).distinct().count();
        }
        return antallArbeidsgivere;
    }

    private static String getArbeidsgiverNavn(Svangerskapspenger.UttakArbeidsforhold arbeidsforhold,
                                              String arbeidsgiverReferanse,
                                              UnaryOperator<String> hentNavn) {
        return arbeidsgiverReferanse != null ? hentNavn.apply(arbeidsgiverReferanse) : brukUttakArbeidType(arbeidsforhold);
    }

    private static String brukUttakArbeidType(Svangerskapspenger.UttakArbeidsforhold arbeidsforhold) {
        return switch (arbeidsforhold.arbeidType()) {
            case ORDINÆRT_ARBEID, ANNET -> null;
            case SELVSTENDIG_NÆRINGSDRIVENDE -> NÆRINGSDRIVENDE;
            case FRILANS -> FRILANSER;
        };
    }
}
