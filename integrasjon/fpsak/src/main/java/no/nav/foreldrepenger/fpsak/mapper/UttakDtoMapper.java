package no.nav.foreldrepenger.fpsak.mapper;

import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.PERIODE_ÅRSAK_DISCRIMINATOR;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeAktivitetDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.fpsak.mapper.sortering.PeriodeComparator;

public class UttakDtoMapper {

    public static UttakResultatPerioder mapUttaksresultatPerioderFraDto(UttakResultatPerioderDto resultatPerioderDto, UnaryOperator<String> hentNavn) {
        var uttakResultatPerioder = getUttakResultatPerioder(resultatPerioderDto.getPerioderSøker(), hentNavn);
        var perioderAnnenPart = getUttakResultatPerioder(resultatPerioderDto.getPerioderAnnenpart(), hentNavn);
        return UttakResultatPerioder.ny().medPerioder(uttakResultatPerioder).medPerioderAnnenPart(perioderAnnenPart).medAleneomsorg(resultatPerioderDto.isAleneomsorg()).medAnnenForelderHarRett(resultatPerioderDto.isAnnenForelderHarRett()).build();
    }

    private static List<UttakResultatPeriode> getUttakResultatPerioder(List<UttakResultatPeriodeDto> resultatPerioderDto, UnaryOperator<String> hentNavn) {
        if (resultatPerioderDto == null) {
            return List.of();
        }
        return resultatPerioderDto.stream()
                .map(p -> periodeFraDto(p, hentNavn))
                .sorted(PeriodeComparator.UTTAKRESULTAT)
                .toList();
    }

    public static UttakResultatPeriode periodeFraDto(UttakResultatPeriodeDto dto, UnaryOperator<String> hentNavn) {
        List<UttakResultatPeriodeAktivitet> aktiviteter = dto.getAktiviteter().stream().map(a -> aktivitetFraDto(a, hentNavn)).collect(Collectors.toList());
        UttakResultatPeriode mappetPeriode = UttakResultatPeriode.ny().medGraderingAvslagÅrsak(velgGraderingsavslagÅrsak(dto)).medPeriodeResultatType(dto.getPeriodeResultatType()).medPeriodeResultatÅrsak(velgPerioderesultatÅrsak(dto)).medTidsperiode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom())).medAktiviteter(aktiviteter).build();
        aktiviteter.forEach(aktivitet -> aktivitet.leggTilPeriode(mappetPeriode));
        return mappetPeriode;
    }

    private static PeriodeResultatÅrsak velgPerioderesultatÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getPeriodeResultatÅrsak() == null ? PeriodeResultatÅrsak.UKJENT
                : new PeriodeResultatÅrsak(dto.getPeriodeResultatÅrsak(), PERIODE_ÅRSAK_DISCRIMINATOR, dto.getPeriodeResultatÅrsakLovhjemmel());
    }

    private static PeriodeResultatÅrsak velgGraderingsavslagÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getGraderingAvslagÅrsak() == null ? PeriodeResultatÅrsak.UKJENT
                : new PeriodeResultatÅrsak(dto.getGraderingAvslagÅrsak(), GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR, dto.getGraderingsAvslagÅrsakLovhjemmel());
    }

    static UttakResultatPeriodeAktivitet aktivitetFraDto(UttakResultatPeriodeAktivitetDto dto, UnaryOperator<String> hentNavn) {
        return UttakResultatPeriodeAktivitet.ny().medArbeidsprosent(dto.getProsentArbeid()).medTrekkdager(dto.getTrekkdagerDesimaler()).medUtbetalingsprosent(dto.getUtbetalingsgrad()).medGraderingInnvilget(dto.isGradering()).medTrekkonto(dto.getStønadskontoType()).medUttakAktivitet(UttakAktivitet.ny().medArbeidsforholdRef(dto.getArbeidsforholdId() != null && !dto.getArbeidsforholdId().isEmpty() ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null).medArbeidsgiver(mapArbeidsgiver(dto.getArbeidsgiverReferanse(), hentNavn)).medUttakArbeidType(dto.getUttakArbeidType()).build()).build();
    }

    static Arbeidsgiver mapArbeidsgiver(String arbeidsgiverReferanse, UnaryOperator<String> hentNavn) {
        return arbeidsgiverReferanse != null ? new Arbeidsgiver(arbeidsgiverReferanse, hentNavn.apply(arbeidsgiverReferanse)) : null;
    }
}
