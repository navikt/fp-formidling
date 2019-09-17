package no.nav.foreldrepenger.melding.datamapper.domene.svp;


import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;

public class SvpUtledHjemmelForBeregningTest {

    private Hjemmel hjemmel = Mockito.mock(Hjemmel.class);

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
                .medhHjemmel(hjemmel)
                .build();

        when(hjemmel.getNavn()).thenReturn("folketrygdloven §§ 14-7 og 8-30");

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
                .medhHjemmel(hjemmel)
                .build();

        when(hjemmel.getNavn()).thenReturn("folketrygdloven §§ 14-7 og 8-49");

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
                .medhHjemmel(hjemmel)
                .build();

        when(hjemmel.getNavn()).thenReturn("folketrygdloven §§ 14-7");

        // Act
        String hjemmel = SvpUtledHjemmelForBeregning.utled(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4");

    }

    @Test
    public void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_4() {

        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();

        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();

        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medhHjemmel(hjemmel)
                .build();

        when(hjemmel.getNavn()).thenReturn("folketrygdloven §§ 14-4");

        // Act
        String hjemmel = SvpUtledHjemmelForBeregning.utled(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4");

    }

}
