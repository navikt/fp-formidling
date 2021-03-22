package no.nav.foreldrepenger.melding.datamapper.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BrevMapperUtilTest {

    @Test
    public void skal_sette_inn_ekstra_linjeskift_i_fritekst_der_det_ikke_er_punktliste() {
        // Arrange
        String fritekstInn = "Tekst1\n- Vedlegg1\n- Vedlegg2\nTekst2\nTekst3\n- Vedlegg3";
        String fritekstUt = "Tekst1\n- Vedlegg1\n- Vedlegg2\n\nTekst2\n\nTekst3\n- Vedlegg3";

        // Act + Assert
        assertThat(BrevMapperUtil.ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_sette_inn_ekstra_linjeskift_i_fritekst_tilslutt_hvis_siste_ikke_var_punktliste() {
        // Arrange
        String fritekstInn = "Tekst1\n- Vedlegg1\n- Vedlegg2\nTekst2\nTekst3\n- Vedlegg3\nTekst4";
        String fritekstUt = "Tekst1\n- Vedlegg1\n- Vedlegg2\n\nTekst2\n\nTekst3\n- Vedlegg3\n\nTekst4\n";

        // Act + Assert
        assertThat(BrevMapperUtil.ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_sette_inn_ekstra_linjeskift_når_det_bare_er_en_linje_uten_punktliste() {
        // Arrange
        String fritekstInn = "Tekst1";
        String fritekstUt = "Tekst1\n";

        // Act + Assert
        assertThat(BrevMapperUtil.ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_ikke_sette_inn_ekstra_linjeskift_når_det_bare_er_en_linje_med_punktliste() {
        // Arrange
        String fritekstInn = "- Vedlegg1";
        String fritekstUt = "- Vedlegg1";

        // Act + Assert
        assertThat(BrevMapperUtil.ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }

    @Test
    public void skal_håndtere_fritekst_på_flere_linjer_uten_punktliste() {
        // Arrange
        String fritekstInn = "Tekst1\nTekst2";
        String fritekstUt = "Tekst1\n\nTekst2\n";

        // Act + Assert
        assertThat(BrevMapperUtil.ivaretaLinjeskiftIFritekst(fritekstInn)).isEqualTo(fritekstUt);
    }
}