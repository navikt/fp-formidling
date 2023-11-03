package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseEngangsstønadDto;

public class TilkjentYtelseDtoMapper {

    private TilkjentYtelseDtoMapper() {
        //CDI
    }

    public static TilkjentYtelseEngangsstønad mapTilkjentYtelseESFraDto(TilkjentYtelseEngangsstønadDto dto) {
        return new TilkjentYtelseEngangsstønad(dto.beregnetTilkjentYtelse());
    }

    public static TilkjentYtelseForeldrepenger mapTilkjentYtelseDagytelseFraDto(TilkjentYtelseDagytelseDto dto, UnaryOperator<String> hentNavn) {
        var tilkjentYtelsePerioder = dto.perioder()
            .stream()
            .map(p -> mapPeriodeFraDto(p, hentNavn))
            .sorted(PeriodeComparator.TILKJENTYTELSERESULTAT)
            .toList();
        return TilkjentYtelseForeldrepenger.ny().leggTilPerioder(tilkjentYtelsePerioder).build();
    }


    private static TilkjentYtelsePeriode mapPeriodeFraDto(TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto dto, UnaryOperator<String> hentNavn) {
        List<TilkjentYtelseAndel> andelListe = new ArrayList<>();
        for (var tilkjentYtelseAndelDto : dto.andeler()) {
            andelListe.add(mapAndelFraDto(tilkjentYtelseAndelDto, hentNavn));
        }
        return TilkjentYtelsePeriode.ny()
            .medDagsats((long) dto.dagsats())
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(dto.fom(), dto.tom()))
            .medAndeler(andelListe)
            .build();
    }

    //Fpsak slår sammen andeler i dto, så vi må eventuelt splitte dem opp igjen
    private static TilkjentYtelseAndel mapAndelFraDto(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto dto, UnaryOperator<String> hentNavn) {
        return TilkjentYtelseAndel.ny()
            .medAktivitetStatus(mapAktivitetstatusFraKontrakt(dto.aktivitetstatus()))
            .medArbeidsforholdRef(
                dto.arbeidsforholdId() != null && !dto.arbeidsforholdId().isEmpty() ? ArbeidsforholdRef.ref(dto.arbeidsforholdId()) : null)
            .medArbeidsgiver(mapArbeidsgiverFraDto(dto, hentNavn))
            .medStillingsprosent(dto.stillingsprosent())
            .medErBrukerMottaker(dto.tilSoker() != null && dto.tilSoker() != 0)
            .medErArbeidsgiverMottaker(dto.refusjon() != null && dto.refusjon() != 0)
            .medDagsats(summerDagsats(dto))
            .medUtbetalesTilBruker(dto.tilSoker())
            .build();
    }

    private static AktivitetStatus mapAktivitetstatusFraKontrakt(TilkjentYtelseDagytelseDto.Aktivitetstatus aktivitetstatus) {
        return switch(aktivitetstatus) {
            case ARBEIDSAVKLARINGSPENGER -> AktivitetStatus.ARBEIDSAVKLARINGSPENGER;
            case ARBEIDSTAKER -> AktivitetStatus.ARBEIDSTAKER;
            case DAGPENGER -> AktivitetStatus.DAGPENGER;
            case FRILANSER -> AktivitetStatus.FRILANSER;
            case MILITÆR_ELLER_SIVIL -> AktivitetStatus.MILITÆR_ELLER_SIVIL;
            case SELVSTENDIG_NÆRINGSDRIVENDE -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE;
            case BRUKERS_ANDEL -> AktivitetStatus.BRUKERS_ANDEL;
            case KUN_YTELSE -> AktivitetStatus.KUN_YTELSE;
            case TTLSTØTENDE_YTELSE -> AktivitetStatus.TTLSTØTENDE_YTELSE;
            case VENTELØNN_VARTPENGER -> AktivitetStatus.VENTELØNN_VARTPENGER;
            case KOMBINERT_AT_FL, KOMBINERT_AT_SN, KOMBINERT_FL_SN,
                KOMBINERT_AT_FL_SN, UDEFINERT -> throw new IllegalStateException("Ugyldig aktivitetstatus for tilkjent ytelse andel: " + aktivitetstatus);
        };
    }


    private static Arbeidsgiver mapArbeidsgiverFraDto(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto dto, UnaryOperator<String> hentNavn) {
        if (!TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER.equals(dto.aktivitetstatus()) || dto.arbeidsgiverReferanse() == null) {
            return null;
        }
        return new Arbeidsgiver(dto.arbeidsgiverReferanse(), hentNavn.apply(dto.arbeidsgiverReferanse()));
    }

    private static int summerDagsats(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto dto) {
        var sum = 0;
        if (dto.tilSoker() != null) {
            sum += dto.tilSoker();
        }
        if (dto.refusjon() != null) {
            sum += dto.refusjon();
        }
        return sum;
    }
}
