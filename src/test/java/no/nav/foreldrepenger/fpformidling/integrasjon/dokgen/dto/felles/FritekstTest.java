package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fraFritekst;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.ivaretaLinjeskiftIFritekst;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.fritekst;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;

class FritekstTest {

    private static final String HENDELSE_FRITEKST = "HENDELSE_FRITEKST";
    private static final String BEHANDLING_FRITEKST = "BEHANDLING_FRITEKST";

    @Test
    void skal_velge_ingen_fritekst_når_ingen_finnes() {
        // Arrange
        var dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(fraFritekst(dokumentHendelse, null)).isNotPresent();
    }

    @Test
    void skal_prioritere_hendelse() {
        // Arrange
        var dokumentHendelse = standardHendelseBuilder().medFritekst(HENDELSE_FRITEKST).build();
        var fritekst = friTekst(BEHANDLING_FRITEKST);

        // Act + Assert
        assertThat(fraFritekst(dokumentHendelse, fritekst).get().getFritekst()).isEqualTo(HENDELSE_FRITEKST);
    }

    @Test
    void skal_ta_fritekst_fra_behandling_når_mangler_i_hendelse() {
        // Arrange
        var dokumentHendelse = standardHendelseBuilder().build();
        var fritekst = friTekst(BEHANDLING_FRITEKST);

        // Act + Assert
        assertThat(fraFritekst(dokumentHendelse, fritekst).get().getFritekst()).isEqualTo(BEHANDLING_FRITEKST);
    }

    private static BrevGrunnlagDto.Behandlingsresultat.@NotNull Fritekst friTekst(String behandlingFritekst) {
        return fritekst().avslagsarsakFritekst(behandlingFritekst).build();
    }

    @Test
    void skal_bytte_ut_dokprod_formatering_med_dokgen_i_fritekst() {
        // Arrange
        var fritekst = friTekst("Tekst\n_Overskrift\nMer tekst\n- Punkt 1\n- Punkt 2\n_Ny overskrift\nTekst-med-bindestrek_og_underscore");
        var dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(fraFritekst(dokumentHendelse, fritekst).get().getFritekst()).isEqualTo(
            "Tekst\n##### Overskrift\nMer tekst\n- Punkt 1\n- Punkt 2\n##### Ny overskrift\nTekst-med-bindestrek_og_underscore");
    }

    @Test
    void skal_lage_linker_av_nav_no() {
        // Arrange
        var fritekst = friTekst("Les mer om dette på nav.no/foreldrepenger.\nDu finner mer informasjon på nav.no/klage og nav.no/familie.");
        var dokumentHendelse = standardHendelseBuilder().build();

        // Act + Assert
        assertThat(fraFritekst(dokumentHendelse, fritekst).get().getFritekst()).isEqualTo(
            "Les mer om dette på [nav.no/foreldrepenger](https://nav.no/foreldrepenger).\\\nDu finner mer informasjon på [nav.no/klage](https://nav.no/klage) og [nav.no/familie](https://nav.no/familie).");
    }

    @ParameterizedTest
    @CsvSource({"'Tekst 1\n- Vedlegg 1\n- Vedlegg 2\nTekst 2.\nTekst 3\n- Vedlegg 3\nTekst 4', 'Tekst 1\n- Vedlegg 1\n- Vedlegg 2\n\nTekst 2.\\\nTekst 3\n- Vedlegg 3\n\nTekst 4'", "'Tekst 1.', 'Tekst 1.'", "'Dette er en setning\nmed et linjeskift midt i.\nNy setning.' , 'Dette er en setning\\\nmed et linjeskift midt i.\\\nNy setning.'", "'Dette er en setning.\n\nNy setning som skal ha ''luft''.', 'Dette er en setning.\n\n\nNy setning som skal ha ''luft''.'", "'- Vedlegg1', '- Vedlegg1'"})
    void testFritekstFormatering(String fritekst, String forventetTekst) {
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

    private DokumentHendelse.Builder standardHendelseBuilder() {
        return DokumentHendelse.builder()
            .medBehandlingUuid(UUID.randomUUID())
            .medBestillingUuid(UUID.randomUUID())
            .medDokumentMal(DokumentMal.FRITEKSTBREV);
    }
}
