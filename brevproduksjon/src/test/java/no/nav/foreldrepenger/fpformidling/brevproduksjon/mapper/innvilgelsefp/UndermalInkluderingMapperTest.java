package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereAvslag;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereInnvilget;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;

class UndermalInkluderingMapperTest {

    @Test
    void skal_inkludere_innvilget_når_det_ikke_er_konsekvens_for_ytelse_endring_i_beregning_og_har_mer_enn_en_periode_der_minst_en_er_innvilget() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).medInnvilget(false).build();

        // Act
        var resultat = skalInkludereInnvilget(of(utbetalingsperiode1, utbetalingsperiode2),
            KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_ikke_inkludere_innvilget_når_det_er_konsekvens_for_ytelse_endring_i_beregning() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).medInnvilget(false).build();

        // Act
        var resultat = skalInkludereInnvilget(of(utbetalingsperiode1, utbetalingsperiode2),
            KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_ikke_inkludere_innvilget_når_det_er_ingen_innvilgede_perioder() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(false).build();
        var utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2011")).medInnvilget(false).build();

        // Act
        var resultat = skalInkludereInnvilget(of(utbetalingsperiode1, utbetalingsperiode2),
            KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode());

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void skal_inkludere_innvilget_når_det_er_en_periode_uten_gitte_årsaker_med_endring_i_uttak() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereInnvilget(of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }


    @Test
    void skal_inkludere_innvilget_når_det_er_bare_en_periode_med_gitte_årsaker() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build();
        var behandling = Behandling.builder().medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("2010")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereInnvilget(of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_inkludere_innvilget_når_det_er_bare_en_periode_uten_gitte_årsaker_hvis_det_er_revurdering_med_endring() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET).build();
        var behandling = Behandling.builder().medBehandlingType(BehandlingType.REVURDERING).medBehandlingsresultat(behandlingsresultat).build();

        var utbetalingsperiode = Utbetalingsperiode.ny().medÅrsak(Årsak.of("1234")).medInnvilget(true).build();

        // Act
        var resultat = skalInkludereInnvilget(of(utbetalingsperiode), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode());

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
}
