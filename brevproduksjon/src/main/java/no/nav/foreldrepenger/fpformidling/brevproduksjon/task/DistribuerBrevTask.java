package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.Uuid;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.distribuerBrev", maxFailedRuns = 2)
public class DistribuerBrevTask implements ProsessTaskHandler {

    private Dokdist dokdist;

    public DistribuerBrevTask() {

    }

    @Inject
    public DistribuerBrevTask(/* @Jersey */Dokdist dokdist) {
        this.dokdist = dokdist;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        var propertyValue = prosessTaskData.getPropertyValue(BrevTaskProperties.BESTILLING_UUID);
        var bestillingUuid = UUID.fromString(propertyValue != null ? propertyValue : UUID.randomUUID().toString()); //TODO: UUID.randomUUID().toString() skal vekk etter taskene har gått gjennom
        dokdist.distribuerJournalpost(journalpostId, bestillingUuid);
    }
}