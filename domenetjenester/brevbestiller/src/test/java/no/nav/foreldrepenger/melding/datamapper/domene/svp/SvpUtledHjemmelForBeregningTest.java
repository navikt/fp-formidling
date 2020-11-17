package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;

public class SvpUtledHjemmelForBeregningTest {


    @Test
    public void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7_og_8_30() {

        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();

        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();

        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medhHjemmel(Hjemmel.F_14_7_8_30)
                .build();

        // Act
        String hjemmel = SvpUtledHjemmelForBeregning.utled(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4 og 8-30");

    }

    @Test
    public void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7_og_8_49() {

        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();

        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();

        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medhHjemmel(Hjemmel.F_14_7_8_49)
                .build();

        // Act
        String hjemmel = SvpUtledHjemmelForBeregning.utled(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4 og 8-49");

    }

    @Test
    public void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7() {

        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();

        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();

        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medhHjemmel(Hjemmel.F_14_7)
                .build();

        // Act
        String hjemmel = SvpUtledHjemmelForBeregning.utled(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4");

    }

}
