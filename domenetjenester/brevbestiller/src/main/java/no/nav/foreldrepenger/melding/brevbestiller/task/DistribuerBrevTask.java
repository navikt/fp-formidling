package no.nav.foreldrepenger.melding.brevbestiller.task;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
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
        dokdist.distribuerJournalpost(journalpostId);
    }
}