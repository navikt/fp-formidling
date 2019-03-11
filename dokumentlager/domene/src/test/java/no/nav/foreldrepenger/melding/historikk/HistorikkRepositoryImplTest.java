package no.nav.foreldrepenger.melding.historikk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

public class HistorikkRepositoryImplTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private HistorikkRepository historikkRepository;
    private DokumentRepository dokumentRepository;

    private Long behandlingId = 123l;

    @Before
    public void setup() {
        dokumentRepository = new DokumentRepositoryImpl(repositoryRule.getEntityManager());
        historikkRepository = new HistorikkRepositoryImpl(repositoryRule.getEntityManager());
    }

    @Test
    public void skalLagreOgHenteOppIgjen() {
        DokumentHistorikkinnslag historikkInnslag = DokumentHistorikkinnslag.builder()
                .medBehandlingId(behandlingId)
                .medJournalpostId(new JournalpostId(123l))
                .medDokumentId("123")
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .medDokumentMalType(dokumentRepository.hentDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK))
                .medHistorikkAktør(HistorikkAktør.SAKSBEHANDLER)
                .medXml("<test/>")
                .build();
        historikkRepository.lagre(historikkInnslag);

        List<DokumentHistorikkinnslag> hendelseListe = historikkRepository.hentInnslagForBehandling(behandlingId);

        assertThat(hendelseListe).hasSize(1);
    }


}