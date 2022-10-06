package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereAvslag;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereInnvilget;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereNyeOpplysningerUtbet;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereUtbetNårGradering;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereUtbetaling;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;

public class UndermalInkluderingMapperTest {

    @Test
    void skal_inkludere__utbetaling_når_det_er_innvilget_resultat_og_mer_enn_en_periode() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).build();

        // Act
        var resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludere_utbetaling_når_det_er_avslått_resultat() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).build();

        // Act
        var resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_næring_og_uten_gradering_og_uten_en_av_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medNæring(Næring.ny().medGradering(false).build()).build();

        // Act
        var resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_uten_gradering_med_en_av_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medNæring(Næring.ny().medGradering(false).build()).build();

        // Act
        var resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_gradering_uten_en_av_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medNæring(Næring.ny().medGradering(true).build()).build();

        // Act
        var resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_arbeidsforhold_og_uten_gradering_og_uten_en_av_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medArbeidsforhold(of(Arbeidsforhold.ny().medGradering(false).build())).build();

        // Act
        var resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_inkludere_utbetaling_når_det_er_innvilget_resultat_og_en_periode_med_annen_aktivitet_og_uten_gradering_og_uten_en_av_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medAnnenAktivitet(of(AnnenAktivitet.ny().medGradering(false).build())).build();

        // Act
        var resultat = skalInkludereUtbetaling(behandling, of(utbetalingsperiode1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_med_gitt_årsak() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).build();

        // Act
        var resultat = skalInkludereUtbetNårGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_uten_gitt_årsak() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).build();

        // Act
        var resultat = skalInkludereUtbetNårGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_med_gradering() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medAnnenAktivitet(of(AnnenAktivitet.ny().medGradering(true).build())).build();

        // Act
        var resultat = skalInkludereUtbetNårGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludere_gradering_når_det_er_innvilget_resultat_og_nøyaktig_en_periode_uten_gradering() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medAnnenAktivitet(of(AnnenAktivitet.ny().medGradering(false).build())).build();

        // Act
        var resultat = skalInkludereUtbetNårGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_gradering_når_det_er_innvilget_resultat_og_mer_enn_en_periode() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).build();

        // Act
        var resultat = skalInkludereUtbetNårGradering(behandling, of(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_gradering_når_det_er_avslått_resultat() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).build();

        // Act
        var resultat = skalInkludereUtbetNårGradering(behandling, of(utbetalingsperiode));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_inkludere_innvilget_når_det_ikke_er_konsekvens_for_ytelse_endring_i_beregning_og_har_mer_enn_en_periode_der_minst_en_er_innvilget() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).medInnvilget(false).build();

        // Act
        var resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludere_innvilget_når_det_er_konsekvens_for_ytelse_endring_i_beregning() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).medInnvilget(false).build();

        // Act
        var resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_innvilget_når_det_er_ingen_innvilgede_perioder() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(false).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).medInnvilget(false).build();

        // Act
        var resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_innvilget_når_det_er_bare_en_periode_uten_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }


    @Test
    void skal_inkludere_innvilget_når_det_er_bare_en_periode_med_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_inkludere_innvilget_når_det_er_bare_en_periode_uten_gitte_årsaker_hvis_det_er_revurdering_med_endring() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET)
                .build();
        var behandling = Behandling.builder().medBehandlingType(BehandlingType.REVURDERING).medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereInnvilget(behandling, of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_inkludere_avslag_når_det_er_minst_en_avslått_periode_og_konsekvens_for_ytelse_ikke_er_endring_i_beregning() {
        // Arrange
        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(false).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludere_avslag_når_det_ikke_er_avslåtte_perioder() {
        // Arrange
        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_avslag_når_det_er_konsekvens_for_ytelse_endring_i_beregning() {
        // Arrange
        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(false).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_avslag_når_det_er_konsekvens_for_ytelse_er_null() {
        // Arrange
        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(false).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereAvslag(of(utbetalingsperiode1, utbetalingsperiode2), null);

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_inkludere_skalInkludereNyeOpplysningerUtbet_når_det_er_en_periode_med_gradering() {
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medAnnenAktivitet(of(AnnenAktivitet.ny().medGradering(true).build())).build();

        // Act
        var resultat = skalInkludereNyeOpplysningerUtbet(behandling, of(utbetalingsperiode), 123);

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skalInkludereNyeOpplysningerUtbet_når_en_periode_og_gyldig_utstettelse_årsak() {
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereNyeOpplysningerUtbet(behandling, of(utbetalingsperiode), 123);

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludereNyeOpplysningerUtbet_når_flere_perioder() {
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereNyeOpplysningerUtbet(behandling, of(utbetalingsperiode, utbetalingsperiode2), 123);

        // Assert
        assertThat(resultat).isFalse();
    }
}
