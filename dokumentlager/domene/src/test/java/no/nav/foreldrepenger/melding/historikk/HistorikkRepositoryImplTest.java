package no.nav.foreldrepenger.melding.historikk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

public class HistorikkRepositoryImplTest {
    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private HistorikkRepository historikkRepository;
    private HendelseRepository hendelseRepository;

    @Before
    public void setup() {
        historikkRepository = new HistorikkRepository(repositoryRule.getEntityManager());
        hendelseRepository = new HendelseRepository(repositoryRule.getEntityManager());
    }

    @Test
    public void skalLagreOgHenteOppIgjen() {
        UUID behandlingUuid = UUID.randomUUID();
        DokumentHendelse hendelse = DokumentHendelse.builder()
                .medBehandlingUuid(behandlingUuid)
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
        hendelseRepository.lagre(hendelse);
        DokumentHistorikkinnslag historikkInnslag = DokumentHistorikkinnslag.builder()
                .medBehandlingUuid(behandlingUuid)
                .medHistorikkUuid(UUID.randomUUID())
                .medHendelseId(hendelse.getId())
                .medJournalpostId(new JournalpostId(123L))
                .medDokumentId("123")
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .medDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK)
                .medHistorikkAktør(HistorikkAktør.SAKSBEHANDLER)
                .build();
        historikkRepository.lagre(historikkInnslag);

        List<DokumentHistorikkinnslag> hendelseListe = historikkRepository.hentInnslagForBehandling(behandlingUuid);

        assertThat(hendelseListe).hasSize(1);
    }
}
