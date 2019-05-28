package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.OpphavTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.YtelseTypeKode;

public class HenleggBehandlingBrevMapperTest {
    private static final long ID = 123L;
    private HenleggBehandlingBrevMapper mapper = new HenleggBehandlingBrevMapper();
    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;

    @Before
    public void setup() {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
    }

    @Test
    public void test_map_fagtype() {
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FØRSTEGANGSSØKNAD);
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagType.getOpphavType()).isEqualTo(OpphavTypeKode.FAMPEN);
    }
}
