package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.dtomapper.DokumentHendelseDtoMapper;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepositoryImpl;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepositoryImpl;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;
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
    private DokumentHendelseDtoMapper dtoTilDomeneobjektMapper;

    private DokumentbestillingV1 dokDto;

    private UUID behandlingId = UUID.randomUUID();

    @Before
    public void setup() {

        EntityManager em = repositoryRule.getEntityManager();
        dokumentRepository = new DokumentRepositoryImpl(em);
        kodeverkRepository = new KodeverkRepositoryImpl(em);
        hendelseRepository = new HendelseRepositoryImpl(em);
        dtoTilDomeneobjektMapper = new DokumentHendelseDtoMapper(kodeverkRepository, dokumentRepository);
        prosessTaskRepository = new ProsessTaskRepositoryImpl(em, null);
        jsonHendelseHandler = new JsonHendelseHandler(hendelseRepository, prosessTaskRepository, dtoTilDomeneobjektMapper);
        dokDto = new DokumentbestillingV1();
    }

    @Test
    public void tomHendelse_skalGiNullpointer() {
        assertThatNullPointerException()
                .isThrownBy(() -> jsonHendelseHandler.prosesser(dokDto));
    }

    @Ignore
    @Test
    public void hendelseUtenBehandlingsId_skalGiNullPointer() {
        dokDto.setFritekst("fritekst");
        assertThatNullPointerException()
                .isThrownBy(() -> jsonHendelseHandler.prosesser(dokDto));
    }

    @Ignore
    @Test
    public void hendelseMedBareBehandlingIdOgYtelseType_skalLagres() {
        dokDto.setBehandlingUuid(behandlingId);
        dokDto.setYtelseType(no.nav.vedtak.felles.dokumentbestilling.kodeverk.FagsakYtelseType.FORELDREPENGER);
        jsonHendelseHandler.prosesser(dokDto);

        List<DokumentHendelse> hendelser = hendelseRepository.hentDokumentHendelserForBehandling(behandlingId);
        assertThat(hendelser).hasSize(1);
        assertThat(hendelser.get(0).getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
    }

}
