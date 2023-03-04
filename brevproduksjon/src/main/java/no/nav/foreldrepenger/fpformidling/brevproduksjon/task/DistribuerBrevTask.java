package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstidspunkt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.UUID;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.*;

@ApplicationScoped
@ProsessTask(value = "formidling.distribuerBrev", maxFailedRuns = 2)
public class DistribuerBrevTask implements ProsessTaskHandler {

    private final Dokdist dokdist;
    private final ProsessTaskTjeneste taskTjeneste;

    @Inject
    public DistribuerBrevTask(Dokdist dokdist, ProsessTaskTjeneste taskTjeneste) {
        this.dokdist = dokdist;
        this.taskTjeneste = taskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(JOURNALPOST_ID));
        var bestillingId = prosessTaskData.getPropertyValue(BESTILLING_ID);
        var distribusjonstype = prosessTaskData.getPropertyValue(DISTRIBUSJONSTYPE);
        var resultat = dokdist.distribuerJournalpost(lagRequest(journalpostId, bestillingId, Distribusjonstype.valueOf(distribusjonstype)));

        if (Dokdist.Resultat.MANGLER_ADRESSE.equals(resultat)) {
            var behandlingUuid = prosessTaskData.getBehandlingUuid();
            var saksnummer = prosessTaskData.getSaksnummer();
            opprettGosysOppgaveTask(journalpostId, behandlingUuid, saksnummer);
        }
    }

    private DistribuerJournalpostRequest lagRequest(JournalpostId journalpostId, String bestillingId, Distribusjonstype distribusjonstype) {
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(), bestillingId, Fagsystem.FPSAK.getOffisiellKode(), Fagsystem.FPSAK.getKode(),
            distribusjonstype, Distribusjonstidspunkt.KJERNETID);
    }

    private void opprettGosysOppgaveTask(JournalpostId journalpostId, UUID behandlingUuId, String saksnummer) {
        var prosessTaskData = ProsessTaskData.forProsessTask(OpprettOppgaveTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(behandlingUuId));
        prosessTaskData.setProperty(BrevTaskProperties.SAKSNUMMER, saksnummer);
        prosessTaskData.setCallIdFraEksisterende();
        taskTjeneste.lagre(prosessTaskData);
    }
}
