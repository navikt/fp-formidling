package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

@ExtendWith(MockitoExtension.class)
class DistribuerBrevTaskTest {

    @Mock
    private Dokdist dokdist;

    @Test
    void testKlientKallMedDokdisttype() {
        var journalpostId = "12345";
        var bestillingsid = "456789";
        var distribusjonstype = Distribusjonstype.VEDTAK;

        var prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId);
        prosessTaskData.setProperty(BrevTaskProperties.BESTILLING_ID, bestillingsid);
        prosessTaskData.setProperty(BrevTaskProperties.DISTRIBUSJONSTYPE, distribusjonstype.name());

        new DistribuerBrevTask(dokdist).doTask(prosessTaskData);

        verify(dokdist).distribuerJournalpost(new JournalpostId(journalpostId), bestillingsid, distribusjonstype);
    }

    @Test
    void testKlientKallUtenDokdisttype() {
        var journalpostId = "12345";
        var bestillingsid = "456789";

        var prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId);
        prosessTaskData.setProperty(BrevTaskProperties.BESTILLING_ID, bestillingsid);

        new DistribuerBrevTask(dokdist).doTask(prosessTaskData);
        verify(dokdist).distribuerJournalpost(eq(new JournalpostId(journalpostId)), eq(bestillingsid), isNull());
    }
}