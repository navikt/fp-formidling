package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Utbetalingsperiode;

public class UtbetalingsperiodeMapperTest {

    @Test
    public void skal_mappe_og_slå_sammen_sammenhengende_perioder_med_samme_dagsats_og_beløp_til_søker() {
        // Arrange
        List<BeregningsresultatPeriode> beregningsperioder = of(
                BeregningsresultatPeriode.ny()
                        .medDagsats(500L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().minusDays(10), LocalDate.now().minusDays(1)))
                        .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                .medBrukerErMottaker(true)
                                .medTilSoker(500)
                                .build()))
                        .build(),
                BeregningsresultatPeriode.ny() // Slås sammen med foregående periode
                        .medDagsats(500L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(3)))
                        .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                .medBrukerErMottaker(true)
                                .medTilSoker(500)
                                .build()))
                        .build(),
                BeregningsresultatPeriode.ny() // Slås ikke sammen pga dato
                        .medDagsats(500L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(8), LocalDate.now().plusDays(10)))
                        .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                .medBrukerErMottaker(true)
                                .medTilSoker(500)
                                .build()))
                        .build(),
                BeregningsresultatPeriode.ny() // Slås ikke sammen pga dagsats
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(11), LocalDate.now().plusDays(12)))
                        .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                .medBrukerErMottaker(true)
                                .medTilSoker(500)
                                .build()))
                        .build(),
                BeregningsresultatPeriode.ny() // Slås ikke sammen fordi beløp til søker er forskjellig
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(13), LocalDate.now().plusDays(14)))
                        .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                .medBrukerErMottaker(true)
                                .medTilSoker(400)
                                .build()))
                        .build(),
                BeregningsresultatPeriode.ny() // Slås ikke sammen fordi bruker ikke er mottaker
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(15), LocalDate.now().plusDays(16)))
                        .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                .medBrukerErMottaker(false)
                                .medTilSoker(400)
                                .build()))
                        .build(),
                BeregningsresultatPeriode.ny() // Slås sammen med foregående periode
                        .medDagsats(1000L)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(17), LocalDate.now().plusDays(18)))
                        .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                .medBrukerErMottaker(false)
                                .medTilSoker(400)
                                .build()))
                        .build()
        );

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsperioder, Språkkode.NB);

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
