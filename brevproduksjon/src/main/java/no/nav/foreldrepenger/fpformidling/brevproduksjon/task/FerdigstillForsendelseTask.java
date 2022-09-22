package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.integrasjon.journal.Journalpost;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.ferdigstillForsendelse", maxFailedRuns = 2)
public class FerdigstillForsendelseTask implements ProsessTaskHandler {

    Journalpost journalpostRestKlient;

    @Inject
    public FerdigstillForsendelseTask(@NativeClient Journalpost journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        journalpostRestKlient.ferdigstillJournalpost(journalpostId);
    }
}
