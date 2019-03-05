package no.nav.foreldrepenger.melding.kafkatjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentHendelse;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse.JsonHendelseHandler;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepositoryImpl;

public class JsonHendelseHandlerTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private DokumentRepository dokumentRepository;
    private KodeverkRepository kodeverkRepository;
    private DokumentHistorikkTjeneste dokumentHistorikkTjeneste;

    private JsonHendelseHandler jsonHendelseHandler;

    private DokumentHendelseDto dokumentHendelse;

    private long behandlingId = 123l;

    @Before
    public void setup() {

        EntityManager em = repositoryRule.getEntityManager();
        dokumentRepository = new DokumentRepositoryImpl(em);
        kodeverkRepository = new KodeverkRepositoryImpl(em);
        dokumentHistorikkTjeneste = Mockito.mock(DokumentHistorikkTjeneste.class);
        jsonHendelseHandler = new JsonHendelseHandler(dokumentRepository, kodeverkRepository, dokumentHistorikkTjeneste);
        dokumentHendelse = new DokumentHendelseDto();
    }

    @Test
    public void tomHendelse_skalGiNullpointer() {
        assertThatNullPointerException()
                .isThrownBy(() -> jsonHendelseHandler.prosesser(dokumentHendelse));
    }

    @Test
    public void hendelseUtenBehandlingsId_skalGiNullPointer() {
        dokumentHendelse.setBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD.getKode());
        dokumentHendelse.setFritekst("fritekst");
        assertThatNullPointerException()
                .isThrownBy(() -> jsonHendelseHandler.prosesser(dokumentHendelse));
    }

    @Test
    public void hendelseMedBareBehandlingId_skalLagres() {
        dokumentHendelse.setBehandlingId(behandlingId);
        dokumentHendelse.setFritekst("fritekst");
        jsonHendelseHandler.prosesser(dokumentHendelse);

        List<DokumentHendelse> hendelser = dokumentRepository.hentDokumentHendelserForBehandling(behandlingId);
        assertThat(hendelser).hasSize(1);
        assertThat(hendelser.get(0).getFritekst()).isEqualToIgnoringCase("fritekst");
    }

}