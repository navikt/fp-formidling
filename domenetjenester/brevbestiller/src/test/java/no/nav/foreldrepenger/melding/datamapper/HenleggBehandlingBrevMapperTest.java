package no.nav.foreldrepenger.melding.datamapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.OpphavTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.YtelseTypeKode;

public class HenleggBehandlingBrevMapperTest {

    HenleggBehandlingBrevMapper mapper = new HenleggBehandlingBrevMapper();
    Behandling behandling;
    DokumentHendelseDto dokumentHendelseDto;

    @Before
    public void setup() {
        behandling = new Behandling(opprettBehandlingDto());
        dokumentHendelseDto = new DokumentHendelseDto();
        dokumentHendelseDto.setYtelseType(FagsakYtelseType.FORELDREPENGER.getKode());
    }

    @Test
    public void test_map_fagtype() {
        FagType fagType = mapper.mapFagType(dokumentHendelseDto, behandling);
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FØRSTEGANGSSØKNAD);
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagType.getOpphavType()).isEqualTo(OpphavTypeKode.FAMPEN);
    }

    private BehandlingDto opprettBehandlingDto() {
        BehandlingDto dto = new BehandlingDto();
        dto.setId(123l);
        dto.setType(new KodeDto("BEHANDLING_TYPE", "BT-002", "Førstegangsbehandling"));
        dto.setBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN);
        return dto;
    }

}