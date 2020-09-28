package no.nav.foreldrepenger.melding.dtomapper;

import static no.nav.foreldrepenger.melding.dtomapper.UttakDtoMapper.mapArbeidsgiver;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatArbeidsforholdDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;


@ApplicationScoped
public class UttakSvpDtoMapper {

    private KodeverkRepository kodeverkRepository;

    public UttakSvpDtoMapper() {
        //CDI
    }

    @Inject
    public UttakSvpDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public SvpUttaksresultat mapSvpUttaksresultatFraDto(SvangerskapspengerUttakResultatDto svpUttaksresultatresultatDto) {
        final var svpUttaksresultatBuilder = SvpUttaksresultat.Builder.ny();
        final var uttaksResultatArbeidsforhold = svpUttaksresultatresultatDto.getUttaksResultatArbeidsforhold();
        emptyIfNull(uttaksResultatArbeidsforhold).forEach(arbeidsforhold -> {
            final var uttakResultatArbeidsforholdBuild = SvpUttakResultatArbeidsforhold.Builder.ny();
            final var arbeidsforholdIkkeOppfyltÅrsak = kodeverkRepository.finn(ArbeidsforholdIkkeOppfyltÅrsak.class, arbeidsforhold.getArbeidsforholdIkkeOppfyltÅrsak().getKode());
            final var uttakArbeidType =UttakArbeidType.fraKode(arbeidsforhold.getArbeidType().getKode());
            uttakResultatArbeidsforholdBuild.medArbeidsgiver(mapArbeidsgiver(arbeidsforhold.getArbeidsgiver()));
            uttakResultatArbeidsforholdBuild.medUttakArbeidType(uttakArbeidType);
            uttakResultatArbeidsforholdBuild.medArbeidsforholdIkkeOppfyltÅrsak(arbeidsforholdIkkeOppfyltÅrsak);
            uttakResultatArbeidsforholdBuild.leggTilPerioder(utledSvpUttakResultatPeriode(arbeidsforhold));
            svpUttaksresultatBuilder.medUttakResultatArbeidsforhold(uttakResultatArbeidsforholdBuild.build());
        });
        return svpUttaksresultatBuilder.build();
    }

    private List<SvpUttakResultatPeriode> utledSvpUttakResultatPeriode(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold) {
        return emptyIfNull(arbeidsforhold.getPerioder()).stream()
                .map(periodeDto -> SvpUttakResultatPeriode.Builder.ny()
                    .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(periodeDto.getFom(), periodeDto.getTom()))
                    .medUtbetalingsgrad(periodeDto.getUtbetalingsgrad().longValue())
                    .medPeriodeResultatType(periodeDto.getPeriodeResultatType())
                    .medPeriodeIkkeOppfyltÅrsak(periodeDto.getPeriodeIkkeOppfyltÅrsak())
                    .medArbeidsgiverNavn(getArbeidsgiverNavn(arbeidsforhold))
                    .build())
                .collect(Collectors.toList());
    }

    private String getArbeidsgiverNavn(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold) {
        return arbeidsforhold.getArbeidsgiver() != null
                ? arbeidsforhold.getArbeidsgiver().getNavn()
                : brukUttakArbeidType(arbeidsforhold);
    }

    private String brukUttakArbeidType(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold) {
        if (UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE.getKode().equals(arbeidsforhold.getArbeidType().getKode())) {
            return "næringsdrivende";
        }
        if (UttakArbeidType.FRILANS.getKode().equals(arbeidsforhold.getArbeidType().getKode())) {
            return "frilanser";
        }
        return null;
    }
}
