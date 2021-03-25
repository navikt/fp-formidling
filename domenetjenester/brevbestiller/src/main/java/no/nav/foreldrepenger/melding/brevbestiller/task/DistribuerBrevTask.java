package no.nav.foreldrepenger.melding.brevbestiller.task;

import no.nav.foreldrepenger.melding.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.melding.brevbestiller.task.DistribuerBrevTask.TASKTYPE;

@ApplicationScoped
@ProsessTask(TASKTYPE)
public class DistribuerBrevTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "formidling.distribuerBrev";

    private Dokdist dokDistRestClient;

    public DistribuerBrevTask() {
        //CDI
    }

    @Inject
    public DistribuerBrevTask(@Jersey Dokdist dokDistRestClient) { this.dokDistRestClient = dokDistRestClient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        dokDistRestClient.distribuerJournalpost(journalpostId);
    }
}