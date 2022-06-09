package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.BESTILLING_ID;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.DISTRIBUSJONSTYPE;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.JOURNALPOST_ID;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.distribuerBrev", maxFailedRuns = 2)
public class DistribuerBrevTask implements ProsessTaskHandler {

    private Dokdist dokdist;

    @Inject
    public DistribuerBrevTask(Dokdist dokdist) {
        this.dokdist = dokdist;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(JOURNALPOST_ID));
        var bestillingId = prosessTaskData.getPropertyValue(BESTILLING_ID);
        var distribusjonstype = prosessTaskData.getPropertyValue(DISTRIBUSJONSTYPE);
        dokdist.distribuerJournalpost(journalpostId, bestillingId, Distribusjonstype.valueOf(distribusjonstype));
    }
}