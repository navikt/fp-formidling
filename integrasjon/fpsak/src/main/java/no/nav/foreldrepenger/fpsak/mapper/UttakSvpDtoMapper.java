package no.nav.foreldrepenger.fpsak.mapper;

import static no.nav.foreldrepenger.fpsak.mapper.UttakDtoMapper.mapArbeidsgiver;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatArbeidsforholdDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatDto;


public class UttakSvpDtoMapper {

    public static final String NÆRINGSDRIVENDE = "næringsdrivende";
    public static final String FRILANSER = "frilanser";

    public static SvpUttaksresultat mapSvpUttaksresultatFraDto(SvangerskapspengerUttakResultatDto svpUttaksresultatresultatDto, UnaryOperator<String> hentNavn) {
        final var svpUttaksresultatBuilder = SvpUttaksresultat.Builder.ny();
        final var uttaksResultatArbeidsforhold = svpUttaksresultatresultatDto.getUttaksResultatArbeidsforhold();
        emptyIfNull(uttaksResultatArbeidsforhold).forEach(arbeidsforhold -> {
            final var uttakResultatArbeidsforholdBuild = SvpUttakResultatArbeidsforhold.Builder.ny();
            final var arbeidsforholdIkkeOppfyltÅrsak = arbeidsforhold.getArbeidsforholdIkkeOppfyltÅrsak();
            final var uttakArbeidType = arbeidsforhold.getArbeidType();
            final var arbeidsgiver = mapArbeidsgiver(arbeidsforhold.getArbeidsgiverReferanse(), hentNavn);
            uttakResultatArbeidsforholdBuild.medArbeidsgiver(arbeidsgiver);
            uttakResultatArbeidsforholdBuild.medUttakArbeidType(uttakArbeidType);
            uttakResultatArbeidsforholdBuild.medArbeidsforholdIkkeOppfyltÅrsak(arbeidsforholdIkkeOppfyltÅrsak);
            uttakResultatArbeidsforholdBuild.leggTilPerioder(utledSvpUttakResultatPeriode(arbeidsforhold, arbeidsgiver));
            svpUttaksresultatBuilder.leggTilUttakResultatArbeidsforhold(uttakResultatArbeidsforholdBuild.build());
        });
        return svpUttaksresultatBuilder.build();
    }

    private static  List<SvpUttakResultatPeriode> utledSvpUttakResultatPeriode(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold, Arbeidsgiver arbeidsgiver) {
        return emptyIfNull(arbeidsforhold.getPerioder()).stream()
                .map(periodeDto -> SvpUttakResultatPeriode.Builder.ny()
                    .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(periodeDto.getFom(), periodeDto.getTom()))
                    .medUtbetalingsgrad(periodeDto.getUtbetalingsgrad().longValue())
                    .medPeriodeResultatType(periodeDto.getPeriodeResultatType())
                    .medPeriodeIkkeOppfyltÅrsak(periodeDto.getPeriodeIkkeOppfyltÅrsak())
                    .medArbeidsgiverNavn(getArbeidsgiverNavn(arbeidsforhold, arbeidsgiver))
                    .build())
                .collect(Collectors.toList());
    }

    private static String getArbeidsgiverNavn(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold, Arbeidsgiver arbeidsgiver) {
        return arbeidsgiver != null ? arbeidsgiver.navn() : brukUttakArbeidType(arbeidsforhold);
    }

    private static String brukUttakArbeidType(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold) {
        if (UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE.getKode().equals(arbeidsforhold.getArbeidType().getKode())) {
            return NÆRINGSDRIVENDE;
        }
        if (UttakArbeidType.FRILANS.getKode().equals(arbeidsforhold.getArbeidType().getKode())) {
            return FRILANSER;
        }
        return null;
    }
}
