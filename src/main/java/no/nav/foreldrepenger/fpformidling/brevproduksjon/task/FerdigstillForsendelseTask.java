package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.integrasjon.journal.DokArkivKlient;
import no.nav.foreldrepenger.fpformidling.domene.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.dokarkiv.DokArkiv;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.ferdigstillForsendelse", maxFailedRuns = 2)
public class FerdigstillForsendelseTask implements ProsessTaskHandler {

    private final DokArkiv dokarkivKlient;

    @Inject
    public FerdigstillForsendelseTask(DokArkiv dokarkivKlient) {
        this.dokarkivKlient = dokarkivKlient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        dokarkivKlient.ferdigstillJournalpost(journalpostId.getVerdi(), DokArkivKlient.AUTOMATISK_JOURNALFØRENDE_ENHET);
    }
}
