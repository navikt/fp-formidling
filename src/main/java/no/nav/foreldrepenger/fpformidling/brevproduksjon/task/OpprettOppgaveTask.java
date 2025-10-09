package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.JOURNALPOST_ID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.FpsakRestKlient;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.OppgaverTjeneste;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.opprettOppgave", prioritet = 2, maxFailedRuns = 2)
public class OpprettOppgaveTask implements ProsessTaskHandler {

    private static final String OPPGAVEBESKRIVELSE = "Bruker har ukjent adresse. Kunne ikke sende brev. Send brev iht rutine.";
    private final OppgaverTjeneste oppgaverTjeneste;
    private final FpsakRestKlient fpsakRestKlient;

    @Inject
    public OpprettOppgaveTask(OppgaverTjeneste oppgaverTjeneste, FpsakRestKlient fpsakRestKlient) {
        this.oppgaverTjeneste = oppgaverTjeneste;
        this.fpsakRestKlient = fpsakRestKlient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(JOURNALPOST_ID));
        var behandlingUuid = prosessTaskData.getBehandlingUuid();

        var behandling = fpsakRestKlient.hentBrevGrunnlag(behandlingUuid);

        oppgaverTjeneste.opprettOppgave(behandling, journalpostId, OPPGAVEBESKRIVELSE);
    }
}
