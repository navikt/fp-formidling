package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.tilkjentytelse.TilkjentYtelseAndelDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.tilkjentytelse.TilkjentYtelseEngangsstønadDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.tilkjentytelse.TilkjentYtelseMedUttaksplanDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.tilkjentytelse.TilkjentYtelsePeriodeDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class TilkjentYtelseDtoMapper {

    private TilkjentYtelseDtoMapper() {
        //CDI
    }

    public static TilkjentYtelseEngangsstønad mapTilkjentYtelseESFraDto(TilkjentYtelseEngangsstønadDto dto) {
        return new TilkjentYtelseEngangsstønad(dto.beregnetTilkjentYtelse());
    }

    public static TilkjentYtelseForeldrepenger mapTilkjentYtelseFPFraDto(TilkjentYtelseMedUttaksplanDto dto, UnaryOperator<String> hentNavn) {
        var tilkjentYtelsePerioder = Arrays.stream(dto.getPerioder())
            .map(p -> mapPeriodeFraDto(p, hentNavn))
            .sorted(PeriodeComparator.TILKJENTYTELSERESULTAT)
            .toList();
        return TilkjentYtelseForeldrepenger.ny().leggTilPerioder(tilkjentYtelsePerioder).build();
    }


    private static TilkjentYtelsePeriode mapPeriodeFraDto(TilkjentYtelsePeriodeDto dto, UnaryOperator<String> hentNavn) {
        List<TilkjentYtelseAndel> andelListe = new ArrayList<>();
        for (var tilkjentYtelseAndelDto : dto.getAndeler()) {
            andelListe.add(mapAndelFraDto(tilkjentYtelseAndelDto, hentNavn));
        }
        return TilkjentYtelsePeriode.ny()
            .medDagsats((long) dto.getDagsats())
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
            .medAndeler(andelListe)
            .build();
    }

    //Fpsak slår sammen andeler i dto, så vi må eventuelt splitte dem opp igjen
    private static TilkjentYtelseAndel mapAndelFraDto(TilkjentYtelseAndelDto dto, UnaryOperator<String> hentNavn) {
        return TilkjentYtelseAndel.ny()
            .medAktivitetStatus(dto.aktivitetStatus())
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

    private static Arbeidsgiver mapArbeidsgiverFraDto(TilkjentYtelseAndelDto dto, UnaryOperator<String> hentNavn) {
        if (!AktivitetStatus.ARBEIDSTAKER.equals(dto.aktivitetStatus()) || dto.arbeidsgiverReferanse() == null) {
            return null;
        }
        return new Arbeidsgiver(dto.arbeidsgiverReferanse(), hentNavn.apply(dto.arbeidsgiverReferanse()));
    }

    private static int summerDagsats(TilkjentYtelseAndelDto dto) {
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
