package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag.SvangerskapspengerUttak;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag;

public final class SvpMapperUtil {

    private static final String NÆRINGSDRIVENDE = "næringsdrivende";
    private static final String FRILANSER = "frilanser";

    private SvpMapperUtil() {
    }

    public static List<SvangerskapspengerUttak.Periode> hentUttaksperioder(SvangerskapspengerUttak svpUttaksresultat) {
        return svpUttaksresultat.uttakArbeidsforhold().stream().flatMap(ura -> ura.perioder().stream()).toList();
    }

    public static String leggTilLovreferanse(Collection<VilkårType> vilkårFraBehandling, Avslagsårsak avslagsårsak) {
        return FellesMapper.lovhjemmelFraAvslagsårsak(FagsakYtelseType.SVANGERSKAPSPENGER, vilkårFraBehandling, avslagsårsak)
            .stream()
            .findFirst()
            .orElse("");
    }

    public static Optional<LocalDate> finnFørsteAvslåtteUttakDato(List<SvangerskapspengerUttak.Periode> uttaksperioder,
                                                                  BrevGrunnlag.Behandlingsresultat behandlingsresultat) {
        return finnMinsteDatoFraAvslåttUttak(uttaksperioder).or(() -> Optional.of(behandlingsresultat.skjæringstidspunkt().dato()));

    }

    public static Optional<LocalDate> finnMinsteDatoFraAvslåttUttak(List<SvangerskapspengerUttak.Periode> perioder) {
        return finnMinsteFraDatoAvslåttUttak(perioder).or(() -> finnMinsteFraDatoFraInnvilgetUttak(perioder));
    }

    private static Optional<LocalDate> finnMinsteFraDatoAvslåttUttak(List<SvangerskapspengerUttak.Periode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> BrevGrunnlag.PeriodeResultatType.AVSLÅTT.equals(p.periodeResultatType()))
            .map(SvangerskapspengerUttak.Periode::fom)
            .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnMinsteFraDatoFraInnvilgetUttak(List<SvangerskapspengerUttak.Periode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> BrevGrunnlag.PeriodeResultatType.INNVILGET.equals(p.periodeResultatType()))
            .map(SvangerskapspengerUttak.Periode::fom)
            .min(LocalDate::compareTo);
    }

    public static int finnAntallArbeidsgivere(List<SvangerskapspengerUttak.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                              List<BrevGrunnlag.Inntektsmelding> inntektsmeldinger,
                                              UnaryOperator<String> hentNavn) {
        var antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream().map(uttakArbeidsforhold -> {
            var arbeidsgiver = mapArbeidsgiver(uttakArbeidsforhold.arbeidsgiverReferanse(), hentNavn);
            return getArbeidsgiverNavn(uttakArbeidsforhold, arbeidsgiver);
        }).distinct().count();
        if (antallArbeidsgivere == 0) {
            antallArbeidsgivere = (int) inntektsmeldinger
                .stream()
                .map(BrevGrunnlag.Inntektsmelding::arbeidsgiverReferanse)
                .distinct()
                .count();
        }
        return antallArbeidsgivere;
    }

    private static Arbeidsgiver mapArbeidsgiver(String arbeidsgiverReferanse, UnaryOperator<String> hentNavn) {
        return arbeidsgiverReferanse != null ? new Arbeidsgiver(arbeidsgiverReferanse, hentNavn.apply(arbeidsgiverReferanse)) : null;
    }

    private static String getArbeidsgiverNavn(SvangerskapspengerUttak.UttakArbeidsforhold arbeidsforhold, Arbeidsgiver arbeidsgiver) {
        return arbeidsgiver != null ? arbeidsgiver.navn() : brukUttakArbeidType(arbeidsforhold);
    }

    private static String brukUttakArbeidType(SvangerskapspengerUttak.UttakArbeidsforhold arbeidsforhold) {
        return switch (arbeidsforhold.arbeidType()) {
            case ORDINÆRT_ARBEID, ANNET -> null;
            case SELVSTENDIG_NÆRINGSDRIVENDE -> NÆRINGSDRIVENDE;
            case FRILANS -> FRILANSER;
        };
    }
}
