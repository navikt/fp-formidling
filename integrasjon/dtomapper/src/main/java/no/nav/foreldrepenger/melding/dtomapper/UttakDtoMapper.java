package no.nav.foreldrepenger.melding.dtomapper;

import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeAktivitetDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakUtsettelseType;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class UttakDtoMapper {

    private KodeverkRepository kodeverkRepository;

    public UttakDtoMapper() {
        //CDI
    }

    @Inject
    public UttakDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public UttakResultatPeriode periodeFraDto(UttakResultatPeriodeDto dto) {
        return UttakResultatPeriode.ny()
                .medGraderingAvslagÅrsak(kodeverkRepository.finn(GraderingAvslagÅrsak.class, dto.getGraderingAvslagÅrsak().getKode()))
                .medPeriodeResultatType(kodeverkRepository.finn(PeriodeResultatType.class, dto.getPeriodeResultatType().getKode()))
                .medPeriodeResultatÅrsak(kodeverkRepository.finn(PeriodeResultatÅrsak.class, dto.getPeriodeResultatÅrsak().getKode()))
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medUttakUtsettelseType(kodeverkRepository.finn(UttakUtsettelseType.class, dto.getUtsettelseType().getKode()))
                .medAktiviteter(dto.getAktiviteter().stream().map(this::aktivitetFraDto).collect(Collectors.toList()))
                .build();
    }

    UttakResultatPeriodeAktivitet aktivitetFraDto(UttakResultatPeriodeAktivitetDto dto) {
        return UttakResultatPeriodeAktivitet.ny()
                .medArbeidsprosent(dto.getProsentArbeid())
                .medTrekkdager(dto.getTrekkdager())
                .medUtbetalingsprosent(dto.getUtbetalingsgrad())
                .medGraderingInnvilget(dto.isGradering())
                .medTrekkonto(kodeverkRepository.finn(StønadskontoType.class, dto.getStønadskontoType().getKode()))
                .medUttakAktivitet(UttakAktivitet.ny()
                        .medArbeidsforholdRef(!StringUtils.nullOrEmpty(dto.getArbeidsforholdId()) ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null)
                        .medArbeidsgiver(ArbeidsgiverMapper.finnArbeidsgiver(dto.getArbeidsgiver().getNavn(),
                                dto.getArbeidsgiver().getAktørId() != null ? dto.getArbeidsgiver().getAktørId()
                                        : dto.getArbeidsgiver().getIdentifikator()))
                        .medUttakArbeidType(kodeverkRepository.finn(UttakArbeidType.class, dto.getUttakArbeidType().getKode()))
                        .build())
                .build();
    }
}
