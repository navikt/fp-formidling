package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.JOURNALPOST_ID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.OppgaverTjeneste;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.opprettOppgave", maxFailedRuns = 1)
public class OpprettOppgaveTask implements ProsessTaskHandler {

    private static final String OPPGAVEBESKRIVELSE = "Bruker har ukjent adresse. Kunne ikke sende brev. Send brev iht rutine.";

    private final OppgaverTjeneste oppgaverTjeneste;
    private final DomeneobjektProvider domeneobjektProvider;

    @Inject
    public OpprettOppgaveTask(OppgaverTjeneste oppgaverTjeneste,
                              DomeneobjektProvider domeneobjektProvider) {
        this.oppgaverTjeneste = oppgaverTjeneste;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(JOURNALPOST_ID));
        var behandlingUuid = prosessTaskData.getBehandlingUuid();

        var behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        domeneobjektProvider.hentFagsakBackend(behandling);

        oppgaverTjeneste.opprettOppgave(behandling, journalpostId, OPPGAVEBESKRIVELSE);
    }
}
