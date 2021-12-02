package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles;

import static no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst.fra;
import static no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst.ivaretaLinjeskiftIFritekst;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

public class FritekstTest {

    private static final String HENDELSE_FRITEKST = "HENDELSE_FRITEKST";
    private static final String BEHANDLING_FRITEKST = "BEHANDLING_FRITEKST";
    private static final UUID BEHANDLING_ID = UUID.randomUUID();
    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;

    @Test
    public void skal_velge_ingen_fritekst_når_ingen_finnes() {
        // Arrange
        behandling = standardBehandlingBuilder().build();
        dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(Fritekst.fra(dokumentHendelse, behandling)).isNotPresent();
    }

    @Test
    public void skal_prioritere_hendelse() {
        // Arrange
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst(BEHANDLING_FRITEKST)
                        .build())
                .build();
        dokumentHendelse = standardHendelseBuilder()
                .medFritekst(HENDELSE_FRITEKST)
                .build();

        // Act + Assert
        assertThat(Fritekst.fra(dokumentHendelse, behandling).get().getFritekst()).isEqualTo(HENDELSE_FRITEKST);
    }

    @Test
    public void skal_ta_fritekst_fra_behandling_når_mangler_i_hendelse() {
        // Arrange
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst(BEHANDLING_FRITEKST)
                        .build())
                .build();
        DokumentHendelse dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(Fritekst.fra(dokumentHendelse, behandling).get().getFritekst()).isEqualTo(BEHANDLING_FRITEKST);
    }

    @Test
    public void skal_bytte_ut_dokprod_formatering_med_dokgen_i_fritekst() {
        // Arrange
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst("Tekst\n_Overskrift\nMer tekst\n- Punkt 1\n- Punkt 2\n_Ny overskrift\nTekst-med-bindestrek_og_underscore")
                        .build())
                .build();
        DokumentHendelse dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(Fritekst.fra(dokumentHendelse, behandling).get().getFritekst())
                .isEqualTo("Tekst\n##### Overskrift\nMer tekst\n- Punkt 1\n- Punkt 2\n\n##### Ny overskrift\nTekst-med-bindestrek_og_underscore\n");
    }

    @Test
    public void skal_sette_inn_ekstra_linjeskift_i_fritekst_der_det_ikke_er_punktliste() {
        // Arrange
        String fritekstInn = "Tekst 1\n- Vedlegg 1\n- Vedlegg 2\nTekst 2.\nTekst 3\n- Vedlegg 3";
        String fritekstUt = "Tekst 1\n- Vedlegg 1\n- Vedlegg 2\n\nTekst 2.\n\nTekst 3\n- Vedlegg 3";

        // Act + Assert
        assertThat(ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_sette_inn_ekstra_linjeskift_i_fritekst_tilslutt_hvis_siste_ikke_var_punktliste() {
        // Arrange
        String fritekstInn = "Tekst 1\n- Vedlegg 1\n- Vedlegg 2\nTekst 2.\nTekst 3\n- Vedlegg 3\nTekst 4";
        String fritekstUt = "Tekst 1\n- Vedlegg 1\n- Vedlegg 2\n\nTekst 2.\n\nTekst 3\n- Vedlegg 3\n\nTekst 4\n";

        // Act + Assert
        assertThat(ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_ikke_sette_inn_ekstra_linjeskift_når_det_bare_er_en_linje_uten_punktliste() {
        // Arrange
        String fritekstInn = "Tekst 1.";
        String fritekstUt = "Tekst 1.";

        // Act + Assert
        assertThat(ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_ikke_sette_inn_ekstra_linjeskift_midt_i_setninger() {
        // Arrange
        String fritekstInn = "Dette er en setning\nmed et linjeskift midt i.\nNy setning.";
        String fritekstUt = "Dette er en setning\nmed et linjeskift midt i.\n\nNy setning.\n";

        // Act + Assert
        assertThat(ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_ikke_sette_inn_ekstra_linjeskift_når_det_bare_er_en_linje_med_punktliste() {
        // Arrange
        String fritekstInn = "- Vedlegg1";
        String fritekstUt = "- Vedlegg1";

        // Act + Assert
        assertThat(ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_håndtere_fritekst_på_flere_linjer_uten_punktliste() {
        // Arrange
        String fritekstInn = "Tekst 1.\nTekst 2.";
        String fritekstUt = "Tekst 1.\n\nTekst 2.\n";

        // Act + Assert
        assertThat(ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_gi_null_hvis_fritekst_er_null() {
        // Arrange
        String fritekstInn = null;
        String fritekstUt = null;

        // Act + Assert
        assertThat(fra(fritekstInn)).isEqualTo(fritekstUt);
    }

    private DokumentHendelse.Builder standardHendelseBuilder() {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }

    private Behandling.Builder standardBehandlingBuilder() {
        return Behandling.builder().medUuid(BEHANDLING_ID);
    }
}
