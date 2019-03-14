package no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepositoryImpl;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepositoryImpl;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskRepositoryImpl;

public class JsonHendelseHandlerTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Mock
    private DokumentRepository dokumentRepository;
    private HendelseRepository hendelseRepository;
    private KodeverkRepository kodeverkRepository;
    private ProsessTaskRepository prosessTaskRepository;
    private JsonHendelseHandler jsonHendelseHandler;

    private DokumentHendelseDto dokumentHendelse;

    private long behandlingId = 123l;

    @Before
    public void setup() {

        EntityManager em = repositoryRule.getEntityManager();
        dokumentRepository = new DokumentRepositoryImpl(em);
        kodeverkRepository = new KodeverkRepositoryImpl(em);
        hendelseRepository = new HendelseRepositoryImpl(em);
        prosessTaskRepository = new ProsessTaskRepositoryImpl(em, null);
        jsonHendelseHandler = new JsonHendelseHandler(hendelseRepository, dokumentRepository, kodeverkRepository, prosessTaskRepository);
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
    public void hendelseMedBareBehandlingIdOgYtelseType_skalLagres() {
        dokumentHendelse.setBehandlingId(behandlingId);
        dokumentHendelse.setYtelseType("FP");
        jsonHendelseHandler.prosesser(dokumentHendelse);

        List<DokumentHendelse> hendelser = hendelseRepository.hentDokumentHendelserForBehandling(behandlingId);
        assertThat(hendelser).hasSize(1);
        assertThat(hendelser.get(0).getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
    }

}