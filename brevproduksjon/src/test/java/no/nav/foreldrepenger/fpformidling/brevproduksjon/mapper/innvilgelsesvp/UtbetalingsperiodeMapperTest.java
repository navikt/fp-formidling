package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;

public class UtbetalingsperiodeMapperTest {

    @Test
    void skal_mappe_og_slå_sammen_sammenhengende_perioder_med_samme_dagsats_og_beløp_til_søker() {
        // Arrange
        var beregningsperioder = of(
                TilkjentYtelsePeriode.ny()
                        .medDagsats(500L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().minusDays(10), LocalDate.now().minusDays(1)))
                        .medAndeler(of(TilkjentYtelseAndel.ny()
                                .medErBrukerMottaker(true)
                                .medUtbetalesTilBruker(500)
                                .build()))
                        .build(),
                TilkjentYtelsePeriode.ny() // Slås sammen med foregående periode
                        .medDagsats(500L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(3)))
                        .medAndeler(of(TilkjentYtelseAndel.ny()
                                .medErBrukerMottaker(true)
                                .medUtbetalesTilBruker(500)
                                .build()))
                        .build(),
                TilkjentYtelsePeriode.ny() // Slås ikke sammen pga dato
                        .medDagsats(500L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(8), LocalDate.now().plusDays(10)))
                        .medAndeler(of(TilkjentYtelseAndel.ny()
                                .medErBrukerMottaker(true)
                                .medUtbetalesTilBruker(500)
                                .build()))
                        .build(),
                TilkjentYtelsePeriode.ny() // Slås ikke sammen pga dagsats
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(11), LocalDate.now().plusDays(12)))
                        .medAndeler(of(TilkjentYtelseAndel.ny()
                                .medErBrukerMottaker(true)
                                .medUtbetalesTilBruker(500)
                                .build()))
                        .build(),
                TilkjentYtelsePeriode.ny() // Slås ikke sammen fordi beløp til søker er forskjellig
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(13), LocalDate.now().plusDays(14)))
                        .medAndeler(of(TilkjentYtelseAndel.ny()
                                .medErBrukerMottaker(true)
                                .medUtbetalesTilBruker(400)
                                .build()))
                        .build(),
                TilkjentYtelsePeriode.ny() // Slås ikke sammen fordi bruker ikke er mottaker
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(15), LocalDate.now().plusDays(16)))
                        .medAndeler(of(TilkjentYtelseAndel.ny()
                                .medErBrukerMottaker(false)
                                .medUtbetalesTilBruker(400)
                                .build()))
                        .build(),
                TilkjentYtelsePeriode.ny() // Slås sammen med foregående periode
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(17), LocalDate.now().plusDays(18)))
                        .medAndeler(of(TilkjentYtelseAndel.ny()
                                .medErBrukerMottaker(false)
                                .medUtbetalesTilBruker(400)
                                .build()))
                        .build()
        );

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsperioder, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(5);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(LocalDate.now().plusDays(3));
        assertThat(resultat.get(0).getPeriodeDagsats()).isEqualTo(500);
        assertThat(resultat.get(0).getUtbetaltTilSøker()).isEqualTo(500);
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(LocalDate.now().plusDays(8));
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(resultat.get(1).getPeriodeDagsats()).isEqualTo(500);
        assertThat(resultat.get(1).getUtbetaltTilSøker()).isEqualTo(500);
        assertThat(resultat.get(2).getPeriodeFom()).isEqualTo(LocalDate.now().plusDays(11));
        assertThat(resultat.get(2).getPeriodeTom()).isEqualTo(LocalDate.now().plusDays(12));
        assertThat(resultat.get(2).getPeriodeDagsats()).isEqualTo(1000);
        assertThat(resultat.get(2).getUtbetaltTilSøker()).isEqualTo(500);
        assertThat(resultat.get(3).getPeriodeFom()).isEqualTo(LocalDate.now().plusDays(13));
        assertThat(resultat.get(3).getPeriodeTom()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(resultat.get(3).getPeriodeDagsats()).isEqualTo(1000);
        assertThat(resultat.get(3).getUtbetaltTilSøker()).isEqualTo(400);
        assertThat(resultat.get(4).getPeriodeFom()).isEqualTo(LocalDate.now().plusDays(15));
        assertThat(resultat.get(4).getPeriodeTom()).isEqualTo(LocalDate.now().plusDays(18));
        assertThat(resultat.get(4).getPeriodeDagsats()).isEqualTo(1000);
        assertThat(resultat.get(4).getUtbetaltTilSøker()).isEqualTo(0);
    }
}
