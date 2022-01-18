package no.nav.foreldrepenger.fpsak.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpsak.dto.tilkjentytelse.TilkjentYtelseAndelDto;
import no.nav.foreldrepenger.fpsak.dto.tilkjentytelse.TilkjentYtelseEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.tilkjentytelse.TilkjentYtelseMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.tilkjentytelse.TilkjentYtelsePeriodeDto;
import no.nav.foreldrepenger.fpsak.mapper.sortering.PeriodeComparator;

public class TilkjentYtelseDtoMapper {

    public TilkjentYtelseDtoMapper() {
        //CDI
    }

    public static TilkjentYtelseEngangsstønad mapTilkjentYtelseESFraDto(TilkjentYtelseEngangsstønadDto dto) {
        return new TilkjentYtelseEngangsstønad(dto.getBeregnetTilkjentYtelse());
    }

    public static TilkjentYtelseForeldrepenger mapTilkjentYtelseFPFraDto(TilkjentYtelseMedUttaksplanDto dto, UnaryOperator<String> hentNavn) {
        List<TilkjentYtelsePeriode> tilkjentYtelsePerioder = Arrays.stream(dto.getPerioder())
                .map(p -> mapPeriodeFraDto(p, hentNavn))
                .sorted(PeriodeComparator.TILKJENTYTELSERESULTAT)
                .collect(Collectors.toList());
        return TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(tilkjentYtelsePerioder)
                .build();
    }


    private static TilkjentYtelsePeriode mapPeriodeFraDto(TilkjentYtelsePeriodeDto dto, UnaryOperator<String> hentNavn) {
        List<TilkjentYtelseAndel> andelListe = new ArrayList<>();
        for (TilkjentYtelseAndelDto tilkjentYtelseAndelDto : dto.getAndeler()) {
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
                .medAktivitetStatus(AktivitetStatus.fraKode(dto.getAktivitetStatus().getKode()))
                .medArbeidsforholdRef(dto.getArbeidsforholdId() != null && !dto.getArbeidsforholdId().isEmpty() ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null)
                .medArbeidsgiver(mapArbeidsgiverFraDto(dto, hentNavn))
                .medStillingsprosent(dto.getStillingsprosent())
                .medErBrukerMottaker(dto.getTilSoker() != null && dto.getTilSoker() != 0)
                .medErArbeidsgiverMottaker(dto.getRefusjon() != null && dto.getRefusjon() != 0)
                .medDagsats(summerDagsats(dto))
                .medUtbetalesTilBruker(dto.getTilSoker())
                .build();
    }

    private static Arbeidsgiver mapArbeidsgiverFraDto(TilkjentYtelseAndelDto dto, UnaryOperator<String> hentNavn) {
        if (!AktivitetStatus.ARBEIDSTAKER.getKode().equals(dto.getAktivitetStatus().getKode())
                || dto.getArbeidsgiverReferanse() == null) {
            return null;
        }
        return new Arbeidsgiver(dto.getArbeidsgiverReferanse(), hentNavn.apply(dto.getArbeidsgiverReferanse()));
    }

    private static int summerDagsats(TilkjentYtelseAndelDto dto) {
        int sum = 0;
        if (dto.getTilSoker() != null) {
            sum += dto.getTilSoker();
        }
        if (dto.getRefusjon() != null) {
            sum += dto.getRefusjon();
        }
        return sum;
    }

}
