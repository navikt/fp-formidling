package no.nav.foreldrepenger.melding.dtomapper;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.uttak.ArbeidsgiverDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeAktivitetDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.melding.dtomapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.UttakUtsettelseType;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
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


    public UttakResultatPerioder mapUttaksresultatPerioderFraDto(UttakResultatPerioderDto resultatPerioderDto) {
        List<UttakResultatPeriode> uttakResultatPerioder = emptyIfNull(resultatPerioderDto.getPerioderSøker()).stream()
                .map(this::periodeFraDto)
                .sorted(PeriodeComparator.UTTAKRESULTAT)
                .collect(Collectors.toList());
        List<UttakResultatPeriode> perioderAnnenPart = emptyIfNull(resultatPerioderDto.getPerioderAnnenpart()).stream()
                .map(this::periodeFraDto)
                .sorted(PeriodeComparator.UTTAKRESULTAT)
                .collect(Collectors.toList());
        return UttakResultatPerioder.ny()
                .medPerioder(uttakResultatPerioder)
                .medPerioderAnnenPart(perioderAnnenPart)
                .medAleneomsorg(resultatPerioderDto.isAleneomsorg())
                .medAnnenForelderHarRett(resultatPerioderDto.isAnnenForelderHarRett())
                .build();
    }

    public UttakResultatPeriode periodeFraDto(UttakResultatPeriodeDto dto) {
        List<UttakResultatPeriodeAktivitet> aktiviteter = dto.getAktiviteter().stream().map(this::aktivitetFraDto).collect(Collectors.toList());
        UttakResultatPeriode mappetPeriode = UttakResultatPeriode.ny()
                .medGraderingAvslagÅrsak(velgGraderingsavslagÅrsak(dto))
                .medPeriodeResultatType(kodeverkRepository.finn(PeriodeResultatType.class, dto.getPeriodeResultatType().getKode()))
                .medPeriodeResultatÅrsak(velgPerioderesultatÅrsak(dto))
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medUttakUtsettelseType(kodeverkRepository.finn(UttakUtsettelseType.class, dto.getUtsettelseType().getKode()))
                .medAktiviteter(aktiviteter)
                .build();
        aktiviteter.forEach(aktivitet -> aktivitet.leggTilPeriode(mappetPeriode));
        return mappetPeriode;
    }

    private PeriodeResultatÅrsak velgPerioderesultatÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getPeriodeResultatÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getPeriodeResultatÅrsak().getKode(), dto.getPeriodeResultatÅrsak().getKodeverk(), dto.getPeriodeResultatÅrsakLovhjemmel());
    }

    private PeriodeResultatÅrsak velgGraderingsavslagÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getGraderingAvslagÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getGraderingAvslagÅrsak().getKode(), dto.getGraderingAvslagÅrsak().getKodeverk(), dto.getGraderingsAvslagÅrsakLovhjemmel());
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
                        .medArbeidsgiver(mapArbeidsgiver(dto.getArbeidsgiver()))
                        .medUttakArbeidType(kodeverkRepository.finn(UttakArbeidType.class, dto.getUttakArbeidType().getKode()))
                        .build())
                .build();
    }

    static  Arbeidsgiver mapArbeidsgiver(ArbeidsgiverDto arbeidsgiverDto) {
        if (arbeidsgiverDto == null || (arbeidsgiverDto.getIdentifikator() == null && arbeidsgiverDto.getAktørId() == null)) {
            return null;
        }
        return ArbeidsgiverMapper.finnArbeidsgiver(arbeidsgiverDto.getNavn(),
                arbeidsgiverDto.getAktørId() != null ? arbeidsgiverDto.getAktørId()
                        : arbeidsgiverDto.getIdentifikator());
    }
}
