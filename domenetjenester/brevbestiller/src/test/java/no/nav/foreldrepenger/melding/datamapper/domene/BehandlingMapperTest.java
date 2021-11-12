package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

public class BehandlingMapperTest {

    private static final String HENDELSE_FRITEKST = "HENDELSE_FRITEKST";
    private static final String BEHANDLING_FRITEKST = "BEHANDLING_FRITEKST";
    private static final UUID BEHANDLING_ID = UUID.randomUUID();
    Behandling behandling;
    DokumentHendelse dokumentHendelse;

    @Test
    public void skal_velge_ingen_fritekst_når_ingen_finnes() {
        behandling = standardBehandlingBuilder()
                .build();
        dokumentHendelse = standardHendelseBuilder().build();
        assertThat(BehandlingMapper.avklarFritekst(dokumentHendelse, behandling)).isNotPresent();
    }

    @Test
    public void skal_prioritere_hendelse() {
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst(BEHANDLING_FRITEKST)
                        .build())
                .build();
        dokumentHendelse = standardHendelseBuilder()
                .medFritekst(HENDELSE_FRITEKST)
                .build();
        assertThat(BehandlingMapper.avklarFritekst(dokumentHendelse, behandling).get()).isEqualTo(HENDELSE_FRITEKST);
    }

    @Test
    public void skal_ta_fritekst_fra_behandling_når_mangler_i_hendelse() {
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst(BEHANDLING_FRITEKST)
                        .build())
                .build();
        DokumentHendelse dokumentHendelse = standardHendelseBuilder()
                .build();
        assertThat(BehandlingMapper.avklarFritekst(dokumentHendelse, behandling).get()).isEqualTo(BEHANDLING_FRITEKST);
    }

    @Test
    public void skal_bytte_ut_dokprod_formatering_med_dokgen_i_fritekst() {
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst("Tekst\n_Overskrift\nMer tekst\n- Punkt1\n- Punkt2\n_Ny overskrift\nTekst-med-bindestrek_og_underscore")
                        .build())
                .build();
        DokumentHendelse dokumentHendelse = standardHendelseBuilder()
                .build();
        assertThat(BehandlingMapper.avklarFritekst(dokumentHendelse, behandling).get())
                .isEqualTo("Tekst\n##### Overskrift\nMer tekst\n* Punkt1\n* Punkt2\n##### Ny overskrift\nTekst-med-bindestrek_og_underscore");
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
