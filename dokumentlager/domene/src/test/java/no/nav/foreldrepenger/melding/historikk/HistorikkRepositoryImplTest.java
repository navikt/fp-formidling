package no.nav.foreldrepenger.melding.historikk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.melding.dbstoette.JpaExtension;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

@ExtendWith(JpaExtension.class)
public class HistorikkRepositoryImplTest {

    private HistorikkRepository historikkRepository;
    private HendelseRepository hendelseRepository;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        historikkRepository = new HistorikkRepository(entityManager);
        hendelseRepository = new HendelseRepository(entityManager);
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
