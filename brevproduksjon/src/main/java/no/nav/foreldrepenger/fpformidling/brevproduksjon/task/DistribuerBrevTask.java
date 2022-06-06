package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
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
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        var bestillingId = Optional.ofNullable(prosessTaskData.getPropertyValue(BrevTaskProperties.BESTILLING_ID)).orElse(prosessTaskData.getPropertyValue(BrevTaskProperties.BESTILLING_UUID));
        dokdist.distribuerJournalpost(journalpostId, bestillingId);
    }
}