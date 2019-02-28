package no.nav.foreldrepenger.melding.kafkatjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.kafkatjenester.jsondokumenthendelse.JsonDokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.BehandlingType;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepositoryImpl;

public class JsonHendelseHandlerTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private DokumentRepository dokumentRepository;
    private KodeverkRepository kodeverkRepository;

    private JsonHendelseHandler jsonHendelseHandler;

    private JsonDokumentHendelse dokumentHendelse;

    private long behandlingId = 123l;

    @Before
    public void setup() {

        EntityManager em = repositoryRule.getEntityManager();
        dokumentRepository = new DokumentRepositoryImpl(em);
        kodeverkRepository = new KodeverkRepositoryImpl(em);
        jsonHendelseHandler = new JsonHendelseHandler(dokumentRepository, kodeverkRepository);
        dokumentHendelse = new JsonDokumentHendelse();
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
        jsonHendelseHandler.prosesser(dokumentHendelse);

        assertThat(dokumentRepository.hentDokumentHendelserForBehandling(behandlingId)).hasSize(1);
    }

}