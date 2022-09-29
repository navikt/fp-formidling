package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatArbeidsforholdDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatDto;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;


public class UttakSvpDtoMapper {

    public static final String NÆRINGSDRIVENDE = "næringsdrivende";
    public static final String FRILANSER = "frilanser";

    public static SvangerskapspengerUttak mapSvpUttaksresultatFraDto(SvangerskapspengerUttakResultatDto svpUttaksresultatresultatDto, UnaryOperator<String> hentNavn) {
        final var svpUttaksresultatBuilder = SvangerskapspengerUttak.Builder.ny();
        final var uttaksResultatArbeidsforhold = svpUttaksresultatresultatDto.uttaksResultatArbeidsforhold();
        if (uttaksResultatArbeidsforhold != null) {
            uttaksResultatArbeidsforhold.forEach(arbeidsforhold -> {
                final var uttakResultatArbeidsforholdBuild = SvpUttakResultatArbeidsforhold.Builder.ny();
                final var arbeidsforholdIkkeOppfyltÅrsak = arbeidsforhold.arbeidsforholdIkkeOppfyltÅrsak();
                final var uttakArbeidType = arbeidsforhold.arbeidType();
                final var arbeidsgiver = UttakDtoMapper.mapArbeidsgiver(arbeidsforhold.arbeidsgiverReferanse(), hentNavn);
                uttakResultatArbeidsforholdBuild.medArbeidsgiver(arbeidsgiver);
                uttakResultatArbeidsforholdBuild.medUttakArbeidType(uttakArbeidType);
                uttakResultatArbeidsforholdBuild.medArbeidsforholdIkkeOppfyltÅrsak(arbeidsforholdIkkeOppfyltÅrsak);
                uttakResultatArbeidsforholdBuild.leggTilPerioder(utledSvpUttakResultatPeriode(arbeidsforhold, arbeidsgiver));
                svpUttaksresultatBuilder.leggTilUttakResultatArbeidsforhold(uttakResultatArbeidsforholdBuild.build());
            });
        }
        return svpUttaksresultatBuilder.build();
    }

    private static List<SvpUttakResultatPeriode> utledSvpUttakResultatPeriode(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold, Arbeidsgiver arbeidsgiver) {
        if (arbeidsforhold.perioder() == null) {
            return List.of();
        }
        return arbeidsforhold.perioder().stream()
                .map(periodeDto -> SvpUttakResultatPeriode.Builder.ny()
                        .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(periodeDto.fom(), periodeDto.tom()))
                        .medUtbetalingsgrad(periodeDto.utbetalingsgrad().longValue())
                        .medPeriodeResultatType(periodeDto.periodeResultatType())
                        .medPeriodeIkkeOppfyltÅrsak(periodeDto.periodeIkkeOppfyltÅrsak())
                        .medArbeidsgiverNavn(getArbeidsgiverNavn(arbeidsforhold, arbeidsgiver))
                        .build())
                .toList();
    }

    private static String getArbeidsgiverNavn(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold, Arbeidsgiver arbeidsgiver) {
        return arbeidsgiver != null ? arbeidsgiver.navn() : brukUttakArbeidType(arbeidsforhold);
    }

    private static String brukUttakArbeidType(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold) {
        if (UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE.getKode().equals(arbeidsforhold.arbeidType().getKode())) {
            return NÆRINGSDRIVENDE;
        }
        if (UttakArbeidType.FRILANS.getKode().equals(arbeidsforhold.arbeidType().getKode())) {
            return FRILANSER;
        }
        return null;
    }
}
