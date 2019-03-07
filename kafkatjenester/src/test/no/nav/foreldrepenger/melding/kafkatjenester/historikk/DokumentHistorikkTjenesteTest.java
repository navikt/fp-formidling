package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepositoryImpl;

public class DokumentHistorikkTjenesteTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    private DokumentHistorikkTjeneste historikkTjeneste;
    @Spy
    private DokumentHistorikkMeldingProducer historikkMeldingProducer;
    private HistorikkRepository historikkRepository;
    private DokumentRepository dokumentRepository;

    @Before
    public void setup() {
        historikkRepository = new HistorikkRepositoryImpl(repositoryRule.getEntityManager());
        dokumentRepository = new DokumentRepositoryImpl(repositoryRule.getEntityManager());
        historikkTjeneste = new DokumentHistorikkTjeneste(historikkMeldingProducer, historikkRepository);
        doAnswer((i) -> {
            return null;
        }).when(historikkMeldingProducer).sendJson(Mockito.any());
    }

    @Test
    public void publiserHistorikk() {
        DokumentHendelse hendelse = DokumentHendelse.builder()
                .medBehandlingId(1l)
                .medDokumentMalType(dokumentRepository.hentDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK))
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medHistorikkAktør(HistorikkAktør.SAKSBEHANDLER)
                .build();
        historikkTjeneste.lagreOgPubliserHistorikk(hendelse);
        assertThat(historikkRepository.hentInnslagForBehandling(1l).size()).isEqualTo(1);
        verify(historikkMeldingProducer, times(1)).sendJson(Mockito.anyString());
    }
}