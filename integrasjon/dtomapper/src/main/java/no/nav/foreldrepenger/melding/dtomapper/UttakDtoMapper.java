package no.nav.foreldrepenger.melding.dtomapper;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeAktivitetDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.melding.dtomapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class UttakDtoMapper {

    public static UttakResultatPerioder mapUttaksresultatPerioderFraDto(UttakResultatPerioderDto resultatPerioderDto, UnaryOperator<String> hentNavn) {
        List<UttakResultatPeriode> uttakResultatPerioder = emptyIfNull(resultatPerioderDto.getPerioderSøker()).stream()
                .map(p -> periodeFraDto(p, hentNavn))
                .sorted(PeriodeComparator.UTTAKRESULTAT)
                .collect(Collectors.toList());
        List<UttakResultatPeriode> perioderAnnenPart = emptyIfNull(resultatPerioderDto.getPerioderAnnenpart()).stream()
                .map(p -> periodeFraDto(p, hentNavn))
                .sorted(PeriodeComparator.UTTAKRESULTAT)
                .collect(Collectors.toList());
        return UttakResultatPerioder.ny()
                .medPerioder(uttakResultatPerioder)
                .medPerioderAnnenPart(perioderAnnenPart)
                .medAleneomsorg(resultatPerioderDto.isAleneomsorg())
                .medAnnenForelderHarRett(resultatPerioderDto.isAnnenForelderHarRett())
                .build();
    }

    public static UttakResultatPeriode periodeFraDto(UttakResultatPeriodeDto dto, UnaryOperator<String> hentNavn) {
        List<UttakResultatPeriodeAktivitet> aktiviteter = dto.getAktiviteter().stream().map(a -> aktivitetFraDto(a, hentNavn)).collect(Collectors.toList());
        UttakResultatPeriode mappetPeriode = UttakResultatPeriode.ny()
                .medGraderingAvslagÅrsak(velgGraderingsavslagÅrsak(dto))
                .medPeriodeResultatType(PeriodeResultatType.fraKode(dto.getPeriodeResultatType().getKode()))
                .medPeriodeResultatÅrsak(velgPerioderesultatÅrsak(dto))
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medAktiviteter(aktiviteter)
                .build();
        aktiviteter.forEach(aktivitet -> aktivitet.leggTilPeriode(mappetPeriode));
        return mappetPeriode;
    }

    private static PeriodeResultatÅrsak velgPerioderesultatÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getPeriodeResultatÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getPeriodeResultatÅrsak().getKode(), dto.getPeriodeResultatÅrsak().getKodeverk(), dto.getPeriodeResultatÅrsakLovhjemmel());
    }

    private static PeriodeResultatÅrsak velgGraderingsavslagÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getGraderingAvslagÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getGraderingAvslagÅrsak().getKode(), dto.getGraderingAvslagÅrsak().getKodeverk(), dto.getGraderingsAvslagÅrsakLovhjemmel());
    }

    static UttakResultatPeriodeAktivitet aktivitetFraDto(UttakResultatPeriodeAktivitetDto dto, UnaryOperator<String> hentNavn) {
        return UttakResultatPeriodeAktivitet.ny()
                .medArbeidsprosent(dto.getProsentArbeid())
                .medTrekkdager(dto.getTrekkdagerDesimaler())
                .medUtbetalingsprosent(dto.getUtbetalingsgrad())
                .medGraderingInnvilget(dto.isGradering())
                .medTrekkonto(StønadskontoType.fraKode(dto.getStønadskontoType().getKode()))
                .medUttakAktivitet(UttakAktivitet.ny()
                        .medArbeidsforholdRef(dto.getArbeidsforholdId() != null && !dto.getArbeidsforholdId().isEmpty() ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null)
                        .medArbeidsgiver(mapArbeidsgiver(dto.getArbeidsgiverReferanse(), hentNavn))
                        .medUttakArbeidType(UttakArbeidType.fraKode(dto.getUttakArbeidType().getKode()))
                        .build())
                .build();
    }

    static  Arbeidsgiver mapArbeidsgiver(String arbeidsgiverReferanse, UnaryOperator<String> hentNavn) {
        return arbeidsgiverReferanse != null ? new Arbeidsgiver(arbeidsgiverReferanse, hentNavn.apply(arbeidsgiverReferanse)) : null;
    }
}
