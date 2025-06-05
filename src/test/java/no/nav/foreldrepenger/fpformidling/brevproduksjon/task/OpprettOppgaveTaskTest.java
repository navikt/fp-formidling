package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.OppgaverTjeneste;
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

        var behandling = Behandling.builder()
            .medBehandlendeEnhetId("1233")
            .medFagsak(FagsakBackend.ny().medSaksnummer(saksnummer).build())
            .build();
        when(provider.hentBehandling(behandlingUuId)).thenReturn(behandling);

        var prosessTaskData = ProsessTaskData.forProsessTask(OpprettOppgaveTask.class);
        prosessTaskData.setSaksnummer(saksnummer);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setBehandlingUUid(behandlingUuId);

        new OpprettOppgaveTask(tjeneste, provider).doTask(prosessTaskData);

        verify(tjeneste).opprettOppgave(eq(behandling), eq(journalpostId), any(String.class));
        verify(provider, times(1)).hentBehandling(behandlingUuId);
    }
}
