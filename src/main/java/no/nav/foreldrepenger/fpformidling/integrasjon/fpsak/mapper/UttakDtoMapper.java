package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import static no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak.GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR;
import static no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak.PERIODE_ÅRSAK_DISCRIMINATOR;

import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.UttakResultatPeriodeAktivitetDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.UttakResultatPeriodeDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;

public class UttakDtoMapper {

    private UttakDtoMapper() {
    }

    public static ForeldrepengerUttak mapUttaksresultatPerioderFraDto(UttakResultatPerioderDto resultatPerioderDto, UnaryOperator<String> hentNavn) {
        var uttakResultatPerioder = getUttakResultatPerioder(resultatPerioderDto.perioderSøker(), hentNavn);
        var perioderAnnenPart = getUttakResultatPerioder(resultatPerioderDto.perioderAnnenpart(), hentNavn);
        var aleneomsorg = resultatPerioderDto.aleneomsorg();
        var annenForelderHarRett = resultatPerioderDto.annenForelderHarRett();
        var annenForelderRettEØS = resultatPerioderDto.annenForelderRettEØS();
        var oppgittAnnenForelderRettEØS = resultatPerioderDto.oppgittAnnenForelderRettEØS();
        return new ForeldrepengerUttak(uttakResultatPerioder, perioderAnnenPart, aleneomsorg, annenForelderHarRett, annenForelderRettEØS,
            oppgittAnnenForelderRettEØS);
    }

    private static List<UttakResultatPeriode> getUttakResultatPerioder(List<UttakResultatPeriodeDto> resultatPerioderDto,
                                                                       UnaryOperator<String> hentNavn) {
        if (resultatPerioderDto == null) {
            return List.of();
        }
        return resultatPerioderDto.stream().map(p -> periodeFraDto(p, hentNavn)).sorted(PeriodeComparator.UTTAKRESULTAT).toList();
    }

    private static UttakResultatPeriode periodeFraDto(UttakResultatPeriodeDto dto, UnaryOperator<String> hentNavn) {
        var aktiviteter = dto.getAktiviteter().stream().map(a -> aktivitetFraDto(a, hentNavn)).toList();
        var mappetPeriode = UttakResultatPeriode.ny()
            .medGraderingAvslagÅrsak(velgGraderingsavslagÅrsak(dto))
            .medPeriodeResultatType(dto.getPeriodeResultatType())
            .medPeriodeResultatÅrsak(velgPerioderesultatÅrsak(dto))
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
            .medAktiviteter(aktiviteter)
            .medTidligstMottattDato(dto.getTidligstMottattDato())
            .medErUtbetalingRedusertTilMorsStillingsprosent(dto.erUtbetalingRedusertTilMorsStillingsprosent())
            .build();
        aktiviteter.forEach(aktivitet -> aktivitet.leggTilPeriode(mappetPeriode));
        return mappetPeriode;
    }

    private static PeriodeResultatÅrsak velgPerioderesultatÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getPeriodeResultatÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getPeriodeResultatÅrsak(),
            PERIODE_ÅRSAK_DISCRIMINATOR, dto.getPeriodeResultatÅrsakLovhjemmel());
    }

    private static PeriodeResultatÅrsak velgGraderingsavslagÅrsak(UttakResultatPeriodeDto dto) {
        return dto.getGraderingAvslagÅrsak() == null ? PeriodeResultatÅrsak.UKJENT : new PeriodeResultatÅrsak(dto.getGraderingAvslagÅrsak(),
            GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR, dto.getGraderingsAvslagÅrsakLovhjemmel());
    }

    private static UttakResultatPeriodeAktivitet aktivitetFraDto(UttakResultatPeriodeAktivitetDto dto, UnaryOperator<String> hentNavn) {
        return UttakResultatPeriodeAktivitet.ny()
            .medArbeidsprosent(dto.getProsentArbeid())
            .medTrekkdager(dto.getTrekkdagerDesimaler())
            .medUtbetalingsprosent(dto.getUtbetalingsgrad())
            .medGraderingInnvilget(dto.isGradering())
            .medTrekkonto(dto.getStønadskontoType())
            .medUttakAktivitet(UttakAktivitet.ny()
                .medArbeidsforholdRef(dto.getArbeidsforholdId() != null && !dto.getArbeidsforholdId().isEmpty() ? ArbeidsforholdRef.ref(
                    dto.getArbeidsforholdId()) : null)
                .medArbeidsgiver(mapArbeidsgiver(dto.getArbeidsgiverReferanse(), hentNavn))
                .medUttakArbeidType(dto.getUttakArbeidType())
                .build())
            .build();
    }

    static Arbeidsgiver mapArbeidsgiver(String arbeidsgiverReferanse, UnaryOperator<String> hentNavn) {
        return arbeidsgiverReferanse != null ? new Arbeidsgiver(arbeidsgiverReferanse, hentNavn.apply(arbeidsgiverReferanse)) : null;
    }
}
