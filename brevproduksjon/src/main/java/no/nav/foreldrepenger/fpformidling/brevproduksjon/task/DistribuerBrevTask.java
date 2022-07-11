package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.BESTILLING_ID;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.DISTRIBUSJONSTYPE;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.JOURNALPOST_ID;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstidspunkt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ApplicationScoped
@ProsessTask(value = "formidling.distribuerBrev", maxFailedRuns = 2)
public class DistribuerBrevTask implements ProsessTaskHandler {

    private final Dokdist dokdist;
    private ProsessTaskTjeneste taskTjeneste;

    @Inject
    public DistribuerBrevTask(@JavaClient Dokdist dokdist,
                              ProsessTaskTjeneste taskTjeneste) {
        this.dokdist = dokdist;
        this.taskTjeneste = taskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(JOURNALPOST_ID));
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
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(), bestillingId, Fagsystem.FPSAK.getOffisiellKode(),
                Fagsystem.FPSAK.getKode(), distribusjonstype, Distribusjonstidspunkt.KJERNETID);
    }

    private void opprettGosysOppgaveTask(JournalpostId journalpostId, UUID behandlingUuId, String saksnummer) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(OpprettOppgaveTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(behandlingUuId));
        prosessTaskData.setProperty(BrevTaskProperties.SAKSNUMMER, saksnummer);
        prosessTaskData.setCallIdFraEksisterende();
        taskTjeneste.lagre(prosessTaskData);
    }
}
