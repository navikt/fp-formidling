package no.nav.foreldrepenger.fpsak.mapper;

import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.PERIODE_ÅRSAK_DISCRIMINATOR;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
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

    private static final Logger LOG = LoggerFactory.getLogger(UttakDtoMapper.class);

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
        return dto.getPeriodeUtfallÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getPeriodeUtfallÅrsak().getKode(), PERIODE_ÅRSAK_DISCRIMINATOR, dto.getPeriodeResultatÅrsakLovhjemmel());
    }

    private static PeriodeResultatÅrsak velgGraderingsavslagÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getGraderingAvslagÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getGraderingAvslagÅrsak().getKode(), GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR, dto.getGraderingsAvslagÅrsakLovhjemmel());
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
