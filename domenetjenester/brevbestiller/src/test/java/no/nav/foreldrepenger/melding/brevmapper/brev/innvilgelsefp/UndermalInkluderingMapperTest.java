package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereAvslag;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereGradering;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereInnvilget;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereUtbetaling;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;

public class UndermalInkluderingMapperTest {

    @Test
    public void skal_inkludere__utbetaling_når_det_er_innvilget_resultat_og_mer_enn_en_periode() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("").build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("").build();

        // Act
        boolean resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_ikke_inkludere_utbetaling_når_det_er_avslått_resultat() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("").build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("").build();

        // Act
        boolean resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_næring_og_uten_gradering_og_uten_en_av_gitte_årsaker() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("1234").medNæring(Næring.ny().medGradering(false).build()).build();

        // Act
        boolean resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_ikke_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_uten_gradering_med_en_av_gitte_årsaker() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medNæring(Næring.ny().medGradering(false).build()).build();

        // Act
        boolean resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_ikke_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_gradering_uten_en_av_gitte_årsaker() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("1234").medNæring(Næring.ny().medGradering(true).build()).build();

        // Act
        boolean resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_arbeidsforhold_og_uten_gradering_og_uten_en_av_gitte_årsaker() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("1234").medArbeidsforhold(of(Arbeidsforhold.ny().medGradering(false).build())).build();

        // Act
        boolean resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_annen_aktivitet_og_uten_gradering_og_uten_en_av_gitte_årsaker() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("1234").medAnnenAktivitet(of(AnnenAktivitet.ny().medGradering(false).build())).build();

        // Act
        boolean resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_med_gitt_årsak() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("2010").build();

        // Act
        boolean resultat = skalInkludereGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_ikke_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_uten_gitt_årsak() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("1234").build();

        // Act
        boolean resultat = skalInkludereGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_med_gradering() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("1234").medAnnenAktivitet(of(AnnenAktivitet.ny().medGradering(true).build())).build();

        // Act
        boolean resultat = skalInkludereGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_ikke_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_uten_gradering() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("1234").medAnnenAktivitet(of(AnnenAktivitet.ny().medGradering(false).build())).build();

        // Act
        boolean resultat = skalInkludereGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_ikke_inkludere_gradering_når_det_er_innvilget_resultat_og_mer_enn_en_periode() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2011").build();

        // Act
        boolean resultat = skalInkludereGradering(behandling, of(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_ikke_inkludere_gradering_når_det_er_avslått_resultat() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("2010").build();

        // Act
        boolean resultat = skalInkludereGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_inkludere_innvilget_når_det_ikke_er_konsekvens_for_ytelse_endring_i_beregning_og_har_mer_enn_en_periode_der_minst_en_er_innvilget() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2011").medInnvilget(false).build();

        // Act
        boolean resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_ikke_inkludere_innvilget_når_det_er_konsekvens_for_ytelse_endring_i_beregning() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2011").medInnvilget(false).build();

        // Act
        boolean resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_ikke_inkludere_innvilget_når_det_er_ingen_innvilgede_perioder() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(false).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2011").medInnvilget(false).build();

        // Act
        boolean resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_ikke_inkludere_innvilget_når_det_er_bare_en_periode_uten_gitte_årsaker() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("1234").medInnvilget(true).build();

        // Act
        boolean resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }


    @Test
    public void skal_inkludere_innvilget_når_det_er_bare_en_periode_med_gitte_årsaker() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();

        // Act
        boolean resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_inkludere_innvilget_når_det_er_bare_en_periode_uten_gitte_årsaker_hvis_det_er_revurdering_med_endring() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET)
                .build();
        Behandling behandling = Behandling.builder().medBehandlingType(BehandlingType.REVURDERING).medBehandlingsresultat(behandlingsresultat).build();

        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak("1234").medInnvilget(true).build();

        // Act
        boolean resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_inkludere_avslag_når_det_er_minst_en_avslått_periode_og_konsekvens_for_ytelse_ikke_er_endring_i_beregning() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(false).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();

        // Act
        boolean resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_ikke_inkludere_avslag_når_det_ikke_er_avslåtte_perioder() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();

        // Act
        boolean resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_ikke_inkludere_avslag_når_det_er_konsekvens_for_ytelse_endring_i_beregning() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(false).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();

        // Act
        boolean resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_ikke_inkludere_avslag_når_det_er_konsekvens_for_ytelse_er_null() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(false).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2010").medInnvilget(true).build();

        // Act
        boolean resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), null);

        // Assert
        assertThat(resultat).isTrue();
    }
}