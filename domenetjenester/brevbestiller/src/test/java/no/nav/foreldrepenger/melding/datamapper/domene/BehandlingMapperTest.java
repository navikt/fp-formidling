package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

public class BehandlingMapperTest {

    private static final String HENDELSE_FRITEKST = "HENDELSE_FRITEKST";
    private static final String BEHANDLING_FRITEKST = "BEHANDLING_FRITEKST";
    private static final long BEHANDLING_ID = 123L;
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

    private DokumentHendelse.Builder standardHendelseBuilder() {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }

    private Behandling.Builder standardBehandlingBuilder() {
        return Behandling.builder()
                .medId(BEHANDLING_ID);
    }

}
