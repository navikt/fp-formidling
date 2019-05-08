package no.nav.foreldrepenger.melding.dtomapper;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeAktivitetDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.melding.uttak.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.InnvilgetÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.UttakUtsettelseType;
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
        return UttakResultatPerioder.ny()
                .medPerioder(emptyIfNull(resultatPerioderDto.getPerioderSøker()).stream().map(this::periodeFraDto).collect(Collectors.toList()))
                .medPerioderAnnenPart(emptyIfNull(resultatPerioderDto.getPerioderAnnenpart()).stream().map(this::periodeFraDto).collect(Collectors.toList()))
                .build();
    }

    public UttakResultatPeriode periodeFraDto(UttakResultatPeriodeDto dto) {
        List<UttakResultatPeriodeAktivitet> aktiviteter = dto.getAktiviteter().stream().map(this::aktivitetFraDto).collect(Collectors.toList());
        UttakResultatPeriode mappetPeriode = UttakResultatPeriode.ny()
                .medGraderingAvslagÅrsak(kodeverkRepository.finn(GraderingAvslagÅrsak.class, dto.getGraderingAvslagÅrsak().getKode()))
                .medPeriodeResultatType(kodeverkRepository.finn(PeriodeResultatType.class, dto.getPeriodeResultatType().getKode()))
                .medPeriodeResultatÅrsak(kodeverkRepository.finn(finnKodeverk(dto.getPeriodeResultatÅrsak().getKodeverk()), dto.getPeriodeResultatÅrsak().getKode()))
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medUttakUtsettelseType(kodeverkRepository.finn(UttakUtsettelseType.class, dto.getUtsettelseType().getKode()))
                .medAktiviteter(aktiviteter)
                .build();
        aktiviteter.forEach(aktivitet -> aktivitet.leggTilPeriode(mappetPeriode));
        return mappetPeriode;
    }

    private Class<? extends PeriodeResultatÅrsak> finnKodeverk(String kodeverk) {
        if (InnvilgetÅrsak.DISCRIMINATOR.equals(kodeverk)) {
            return InnvilgetÅrsak.class;
        } else if (IkkeOppfyltÅrsak.DISCRIMINATOR.equals(kodeverk)) {
            return IkkeOppfyltÅrsak.class;
        }
        return PeriodeResultatÅrsak.class;
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
                        .medArbeidsgiver(mapArbeidsgiver(dto))
                        .medUttakArbeidType(kodeverkRepository.finn(UttakArbeidType.class, dto.getUttakArbeidType().getKode()))
                        .build())
                .build();
    }

    private Arbeidsgiver mapArbeidsgiver(UttakResultatPeriodeAktivitetDto dto) {
        if (dto.getArbeidsgiver() == null) {
            return null;
        }
        return ArbeidsgiverMapper.finnArbeidsgiver(dto.getArbeidsgiver().getNavn(),
                dto.getArbeidsgiver().getAktørId() != null ? dto.getArbeidsgiver().getAktørId()
                        : dto.getArbeidsgiver().getIdentifikator());
    }
}
