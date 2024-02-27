package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.ivaretaLinjeskiftIFritekst;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseEntitet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FritekstTest {

    private static final String HENDELSE_FRITEKST = "HENDELSE_FRITEKST";
    private static final String BEHANDLING_FRITEKST = "BEHANDLING_FRITEKST";
    private static final UUID BEHANDLING_ID = UUID.randomUUID();
    private Behandling behandling;
    private DokumentHendelseEntitet dokumentHendelseEntitet;

    @Test
    void skal_velge_ingen_fritekst_når_ingen_finnes() {
        // Arrange
        behandling = standardBehandlingBuilder().build();
        dokumentHendelseEntitet = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(fra(dokumentHendelseEntitet, behandling)).isNotPresent();
    }

    @Test
    void skal_prioritere_hendelse() {
        // Arrange
        behandling = standardBehandlingBuilder().medBehandlingsresultat(
            Behandlingsresultat.builder().medAvslagarsakFritekst(BEHANDLING_FRITEKST).build()).build();
        dokumentHendelseEntitet = standardHendelseBuilder().medFritekst(HENDELSE_FRITEKST).build();

        // Act + Assert
        assertThat(fra(dokumentHendelseEntitet, behandling).get().getFritekst()).isEqualTo(HENDELSE_FRITEKST);
    }

    @Test
    void skal_ta_fritekst_fra_behandling_når_mangler_i_hendelse() {
        // Arrange
        behandling = standardBehandlingBuilder().medBehandlingsresultat(
            Behandlingsresultat.builder().medAvslagarsakFritekst(BEHANDLING_FRITEKST).build()).build();
        var dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(fra(dokumentHendelse, behandling).get().getFritekst()).isEqualTo(BEHANDLING_FRITEKST);
    }

    @Test
    void skal_bytte_ut_dokprod_formatering_med_dokgen_i_fritekst() {
        // Arrange
        behandling = standardBehandlingBuilder().medBehandlingsresultat(Behandlingsresultat.builder()
            .medAvslagarsakFritekst("Tekst\n_Overskrift\nMer tekst\n- Punkt 1\n- Punkt 2\n_Ny overskrift\nTekst-med-bindestrek_og_underscore")
            .build()).build();
        var dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(fra(dokumentHendelse, behandling).get().getFritekst()).isEqualTo(
            "Tekst\n##### Overskrift\nMer tekst\n- Punkt 1\n- Punkt 2\n##### Ny overskrift\nTekst-med-bindestrek_og_underscore");
    }

    @Test
    void skal_lage_linker_av_nav_no() {
        // Arrange
        behandling = standardBehandlingBuilder().medBehandlingsresultat(Behandlingsresultat.builder()
            .medAvslagarsakFritekst("Les mer om dette på nav.no/foreldrepenger.\nDu finner mer informasjon på nav.no/klage og nav.no/familie.")
            .build()).build();
        var dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(fra(dokumentHendelse, behandling).get().getFritekst()).isEqualTo(
            "Les mer om dette på [nav.no/foreldrepenger](https://nav.no/foreldrepenger).\\\nDu finner mer informasjon på [nav.no/klage](https://nav.no/klage) og [nav.no/familie](https://nav.no/familie).");
    }

    @ParameterizedTest
    @CsvSource({
        "'Tekst 1\n- Vedlegg 1\n- Vedlegg 2\nTekst 2.\nTekst 3\n- Vedlegg 3\nTekst 4', 'Tekst 1\n- Vedlegg 1\n- Vedlegg 2\n\nTekst 2.\\\nTekst 3\n- Vedlegg 3\n\nTekst 4'",
        "'Tekst 1.', 'Tekst 1.'",
        "'Dette er en setning\nmed et linjeskift midt i.\nNy setning.' , 'Dette er en setning\\\nmed et linjeskift midt i.\\\nNy setning.'",
        "'Dette er en setning.\n\nNy setning som skal ha 'luft'.', 'Dette er en setning.\n\n\nNy setning som skal ha 'luft'.'",
        "'- Vedlegg1', '- Vedlegg1'"
    })
    void testFritekstFormatering(String fritekst, String forventetTekst) {
        // Arrange
        //var fritekstInn = "Tekst 1\n- Vedlegg 1\n- Vedlegg 2\nTekst 2.\nTekst 3\n- Vedlegg 3\nTekst 4";
        //var fritekstUt = "Tekst 1\n- Vedlegg 1\n- Vedlegg 2\n\nTekst 2.\\\nTekst 3\n- Vedlegg 3\n\nTekst 4";

        // Act + Assert
        assertThat(fritekst).isNotNull();
        assertThat(ivaretaLinjeskiftIFritekst(fritekst)).isEqualTo(forventetTekst);
    }

    @Test
    void skal_ikke_sette_inn_ekstra_linjeskift_når_det_bare_er_en_linje_uten_punktliste() {
        // Arrange
        var fritekstInn = "Tekst 1.";
        var fritekstUt = "Tekst 1.";

        // Act + Assert
        assertThat(ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    void skal_gi_null_hvis_fritekst_er_null() {
        assertThat(fra(null)).isNull();
    }

    private DokumentHendelseEntitet.Builder standardHendelseBuilder() {
        return DokumentHendelseEntitet.builder()
            .medBehandlingUuid(UUID.randomUUID())
            .medBestillingUuid(UUID.randomUUID());
    }

    private Behandling.Builder standardBehandlingBuilder() {
        return Behandling.builder().medUuid(BEHANDLING_ID);
    }
}
