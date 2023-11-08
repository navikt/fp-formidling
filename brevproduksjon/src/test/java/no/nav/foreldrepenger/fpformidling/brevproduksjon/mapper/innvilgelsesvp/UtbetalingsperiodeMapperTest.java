package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.UttakAktivitetMedPerioder;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;

class UtbetalingsperiodeMapperTest {

    private static final String ARBEIDSGIVER_1 = "Arbeidsgiver1";
    private static final String ARBEIDSGIVER_2 = "Arbeidsgiver2";
    private static final String ORG_NR_1 = "97525987";
    private static final String ORG_NR_2 = "97525988";

    private static final LocalDate START = LocalDate.now();

    @Test
    void skal_mappe_2_arbeidsforhold_og_3_Perioder() {
        // Arrange
        var tilkjentPerioder = of(TilkjentYtelsePeriode.ny()
            .medDagsats(584L)
            .medPeriode(fraOgMedTilOgMed(START, START.plusDays(3)))
            .medAndeler(of(TilkjentYtelseAndel.ny()
                .medErBrukerMottaker(false)
                .medUtbetalesTilBruker(0)
                .medArbeidsgiver(new Arbeidsgiver(ORG_NR_1, ARBEIDSGIVER_1))
                .medDagsats(584)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .medStillingsprosent(BigDecimal.valueOf(80))
                .medUtbetalingsgrad(BigDecimal.valueOf(100))
                .build()))
            .build(), TilkjentYtelsePeriode.ny()
            .medDagsats(1596L)
            .medPeriode(fraOgMedTilOgMed(START.plusDays(4), START.plusDays(7)))
            .medAndeler(of(TilkjentYtelseAndel.ny()
                .medErBrukerMottaker(false)
                .medUtbetalesTilBruker(0)
                .medArbeidsgiver(new Arbeidsgiver(ORG_NR_2, ARBEIDSGIVER_2))
                .medDagsats(1012)
                .medStillingsprosent(BigDecimal.valueOf(80))
                .medUtbetalingsgrad(BigDecimal.valueOf(20))
                .build(), TilkjentYtelseAndel.ny()
                .medErBrukerMottaker(true)
                .medUtbetalesTilBruker(584)
                .medArbeidsgiver(new Arbeidsgiver(ORG_NR_1, ARBEIDSGIVER_1))
                .medDagsats(584)
                .medStillingsprosent(BigDecimal.valueOf(20))
                .medUtbetalingsgrad(BigDecimal.valueOf(100))
                .build()))
            .build());

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUttakAktiviterMedUtbetPerioder(tilkjentPerioder, Språkkode.NB);

        resultat.sort(Comparator.comparing(UttakAktivitetMedPerioder::beskrivelse));
        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).beskrivelse()).isEqualTo(ARBEIDSGIVER_1);
        assertThat(resultat.get(0).utbetalingsperioder()).hasSize(2);
        //Utbet periode 1
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(3));
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));
        //Utbet periode 2
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getUtbetaltTilSøker()).isEqualTo(584);
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));

        assertThat(resultat.get(1).beskrivelse()).isEqualTo(ARBEIDSGIVER_2);
        assertThat(resultat.get(1).utbetalingsperioder()).hasSize(1);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getDagsats()).isEqualTo(1012);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(20));

    }

    @Test
    void skal_mappe_1_arbeidsforhold_og_SN_og_3_Perioder() {
        // Arrange
        var tilkjentPerioder = of(TilkjentYtelsePeriode.ny()
            .medDagsats(584L)
            .medPeriode(fraOgMedTilOgMed(START, START.plusDays(3)))
            .medAndeler(of(TilkjentYtelseAndel.ny()
                .medErBrukerMottaker(false)
                .medUtbetalesTilBruker(0)
                .medDagsats(584)
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_SN)
                .medStillingsprosent(BigDecimal.valueOf(80))
                .medUtbetalingsgrad(BigDecimal.valueOf(100))
                .build()))
            .build(), TilkjentYtelsePeriode.ny()
            .medDagsats(1596L)
            .medPeriode(fraOgMedTilOgMed(START.plusDays(4), START.plusDays(7)))
            .medAndeler(of(TilkjentYtelseAndel.ny()
                .medErBrukerMottaker(false)
                .medUtbetalesTilBruker(0)
                .medArbeidsgiver(new Arbeidsgiver(ORG_NR_2, ARBEIDSGIVER_2))
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .medDagsats(1012)
                .medStillingsprosent(BigDecimal.valueOf(80))
                .medUtbetalingsgrad(BigDecimal.valueOf(20))
                .build(), TilkjentYtelseAndel.ny()
                .medErBrukerMottaker(true)
                .medUtbetalesTilBruker(584)
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_SN)
                .medDagsats(584)
                .medStillingsprosent(BigDecimal.valueOf(20))
                .medUtbetalingsgrad(BigDecimal.valueOf(100))
                .build()))
            .build());

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUttakAktiviterMedUtbetPerioder(tilkjentPerioder, Språkkode.NB);

        resultat.sort(Comparator.comparing(UttakAktivitetMedPerioder::beskrivelse));
        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.get(0).beskrivelse()).isEqualTo(ARBEIDSGIVER_2);
        assertThat(resultat.get(0).utbetalingsperioder()).hasSize(1);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getDagsats()).isEqualTo(1012);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(20));

        assertThat(resultat.get(1).beskrivelse()).isEqualTo("Som næringsdrivende");
        assertThat(resultat.get(1).utbetalingsperioder()).hasSize(2);
        //Utbet periode 1
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(3));
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));
        //Utbet periode 2
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getUtbetaltTilSøker()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));


    }
}
