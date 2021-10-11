package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static com.google.common.collect.ImmutableList.of;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.ForMyeUtbetalt;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;

public class ForMyeUtbetaltMapperTest {

    @Test
    public void skal_returnere_for_mye_utbetalt_GENERELL_når_periode_har_gradering() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medArbeidsforhold(of(Arbeidsforhold.ny().medGradering(true).build()))
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.GENERELL);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_GENERELL_når_årsak_er_ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT.getKode()))
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.GENERELL);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_GENERELL_når_årsak_er_AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD.getKode()))
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.GENERELL);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_GENERELL_når_årsak_er_FOR_SEN_SØKNAD() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.FOR_SEN_SØKNAD.getKode()))
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.GENERELL);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_FERIE_når_årsak_er_UTSETTELSE_GYLDIG_PGA_FERIE_og_perioden_er_tilbake_i_tid() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.FERIE);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_FERIE_når_årsak_er_UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT_og_perioden_er_tilbake_i_tid() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.FERIE);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_FERIE_når_årsak_er_UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT_og_perioden_er_tilbake_i_tid_selv_om_det_også_finnes_periode_i_fremtiden() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().plusDays(50), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode1, utbetalingsperiode2), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.FERIE);
    }

    @Test
    public void skal_returnere_null_når_årsak_er_UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT_og_perioden_er_i_fremtiden() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().plusDays(50), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isNull();
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_JOBB_når_årsak_er_UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID_og_perioden_er_tilbake_i_tid() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.JOBB);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_JOBB_når_årsak_er_UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT_og_perioden_er_tilbake_i_tid() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.JOBB);
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_FERIE_når_årsak_er_UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT_og_perioden_er_tilbake_i_tid_selv_om_det_også_finnes_periode_i_fremtiden() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().plusDays(50), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode1, utbetalingsperiode2), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.JOBB);
    }

    @Test
    public void skal_returnere_null_når_årsak_er_UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT_og_perioden_er_i_fremtiden() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().plusDays(50), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isNull();
    }

    @Test
    public void skal_returnere_for_mye_utbetalt_GENERELL_når_årsak_er_både_FERIE_og_JOBB() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().minusDays(20), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode1, utbetalingsperiode2), behandling);

        // Assert
        assertThat(resultat).isEqualTo(ForMyeUtbetalt.GENERELL);
    }

    @Test
    public void skal_returnere_ingen_når_førstegangsbehandling() {
        // Arrange
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medAvsluttet(LocalDateTime.now()).build();
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode()))
                .medPeriodeFom(LocalDate.now().plusDays(50), Språkkode.NB)
                .build();

        // Act
        ForMyeUtbetalt resultat = ForMyeUtbetaltMapper.forMyeUtbetalt(of(utbetalingsperiode), behandling);

        // Assert
        assertThat(resultat).isNull();
    }
}