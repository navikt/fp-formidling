package no.nav.foreldrepenger.melding.brevbestiller.task;

import no.nav.foreldrepenger.melding.integrasjon.journal.JournalpostRestKlient;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.melding.brevbestiller.task.FerdigstillForsendelseTask.TASKTYPE;

@ApplicationScoped
@ProsessTask(TASKTYPE)
public class FerdigstillForsendelseTask implements ProsessTaskHandler {
    public static final String TASKTYPE = "formidling.ferdigstillForsendelse";

    JournalpostRestKlient journalpostRestKlient;

    @Inject
    public FerdigstillForsendelseTask(JournalpostRestKlient journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        journalpostRestKlient.ferdigstillJournalpost(journalpostId);
    }
}