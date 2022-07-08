package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.OppgaverTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2.Oppgaver;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

@ExtendWith(MockitoExtension.class)
class OpprettOppgaveTaskTest {

    @Mock
    private OppgaverTjeneste tjeneste;

    @Mock
    private DomeneobjektProvider provider;

    @Test
    void doTask() {
        var journalpostId = new JournalpostId("12345");
        var behandlingUuId = UUID.randomUUID();
        var saksnummer = "23424354353";

        var behandling = Behandling.builder().medBehandlendeEnhetId("1233").build();
        when(provider.hentBehandling(behandlingUuId)).thenReturn(behandling);
        when(provider.hentFagsakBackend(behandling)).thenReturn(FagsakBackend.ny().medSaksnummer(saksnummer).build());

        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(OpprettOppgaveTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(behandlingUuId));
        prosessTaskData.setProperty(BrevTaskProperties.SAKSNUMMER, saksnummer);

        new OpprettOppgaveTask(tjeneste, provider).doTask(prosessTaskData);

        verify(tjeneste).opprettOppgave(eq(behandling), eq(journalpostId), any(String.class));
        verify(provider, times(1)).hentBehandling(behandlingUuId);
        verify(provider, times(1)).hentFagsakBackend(eq(behandling));
    }
}
