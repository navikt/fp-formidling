package no.nav.foreldrepenger.melding.dtomapper;

import static no.nav.foreldrepenger.melding.dtomapper.UttakDtoMapper.mapArbeidsgiver;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

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
        SvpUttaksresultat.Builder svpUttaksresultat = SvpUttaksresultat.ny();
        emptyIfNull(svpUttaksresultatresultatDto.getUttaksResultatArbeidsforhold()).stream()
                .forEach(arbeidsforhold -> {
                    SvpUttakResultatArbeidsforhold.Builder resultat = SvpUttakResultatArbeidsforhold.ny();
                    resultat.medArbeidsgiver(mapArbeidsgiver(arbeidsforhold.getArbeidsgiver()));
                    resultat.medUttakArbeidType(kodeverkRepository.finn(UttakArbeidType.class, arbeidsforhold.getArbeidType().getKode()));
                    resultat.medArbeidsforholdIkkeOppfyltÅrsak(kodeverkRepository.finn(ArbeidsforholdIkkeOppfyltÅrsak.class, arbeidsforhold.getArbeidsforholdIkkeOppfyltÅrsak().getKode()));
                    emptyIfNull(arbeidsforhold.getPerioder()).stream()
                            .map(periodeDto -> SvpUttakResultatPeriode.ny()
                                    .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(periodeDto.getFom(), periodeDto.getTom()))
                                    .medUtbetalingsgrad(periodeDto.getUtbetalingsgrad().longValue())
                                    .medPeriodeResultatType(periodeDto.getPeriodeResultatType())
                                    .medPeriodeIkkeOppfyltÅrsak(periodeDto.getPeriodeIkkeOppfyltÅrsak())
                                    .medArbeidsgiverNavn(getArbeidsgiverNavn(arbeidsforhold))
                                    .build())
                            .forEach(resultat::medPeriode);
                    svpUttaksresultat.medUttakResultatArbeidsforhold(resultat.build());
                });
        return svpUttaksresultat.build();
    }

    private String getArbeidsgiverNavn(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold) {
        return arbeidsforhold.getArbeidsgiver() != null ?
                arbeidsforhold.getArbeidsgiver().getNavn() : brukUttakArbeidType(arbeidsforhold);
    }

    private String brukUttakArbeidType(SvangerskapspengerUttakResultatArbeidsforholdDto arbeidsforhold) {
        return UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE.getKode().equals(arbeidsforhold.getArbeidType().getKode()) ?
                "næringsdrivende" : UttakArbeidType.FRILANS.getKode().equals(arbeidsforhold.getArbeidType().getKode()) ?
                "frilanser" : null;
    }
}
