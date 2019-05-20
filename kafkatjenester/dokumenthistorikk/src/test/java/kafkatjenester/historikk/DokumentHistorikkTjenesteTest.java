package kafkatjenester.historikk;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepositoryImpl;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkinnslagProducer;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

public class DokumentHistorikkTjenesteTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    private DokumentHistorikkTjeneste historikkTjeneste;
    @Spy
    private DokumentHistorikkinnslagProducer historikkMeldingProducer;
    private DokumentRepository dokumentRepository;
    private HendelseRepository hendelseRepository;

    @Before
    public void setup() {
        dokumentRepository = new DokumentRepositoryImpl(repositoryRule.getEntityManager());
        hendelseRepository = new HendelseRepositoryImpl(repositoryRule.getEntityManager());
        historikkTjeneste = new DokumentHistorikkTjeneste(historikkMeldingProducer);
        doAnswer((i) -> null).when(historikkMeldingProducer).sendJson(Mockito.any());
    }

    @Test
    public void publiserHistorikk() {
        DokumentHendelse hendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
        hendelseRepository.lagre(hendelse);
        DokumentHistorikkinnslag historikk = DokumentHistorikkinnslag.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medHendelseId(hendelse.getId())
                .medDokumentMalType(dokumentRepository.hentDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK))
                .medJournalpostId(new JournalpostId("123"))
                .medHistorikkAktør(HistorikkAktør.SAKSBEHANDLER)
                .medDokumentId("123")
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();
        historikkTjeneste.publiserHistorikk(historikk);
        verify(historikkMeldingProducer, times(1)).sendJson(Mockito.anyString());
    }
}
