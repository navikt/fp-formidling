package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ExtendWith(MockitoExtension.class)
class DistribuerBrevTaskTest {

    @Mock
    private Dokdist dokdist;
    @Mock
    ProsessTaskTjeneste taskTjeneste;

    @Test
    void testKlientKallMedDokdisttype() {
        var journalpostId = "12345";
        var bestillingsid = "456789";
        var distribusjonstype = Distribusjonstype.VEDTAK;

        var prosessTaskData = opprettProsessTaskData(journalpostId, bestillingsid, distribusjonstype);

        new DistribuerBrevTask(dokdist, taskTjeneste).doTask(prosessTaskData);

        verify(dokdist).distribuerJournalpost(any(DistribuerJournalpostRequest.class));
        verify(taskTjeneste, never()).lagre(any(ProsessTaskData.class));
    }

    @Test
    void testKlientKallUtenDokdisttype() {
        var journalpostId = "12345";
        var bestillingsid = "456789";

        var prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId);
        prosessTaskData.setProperty(BrevTaskProperties.BESTILLING_ID, bestillingsid);

        var distribuerBrevTask = new DistribuerBrevTask(dokdist, taskTjeneste);

        Exception thrown = Assertions.assertThrows(NullPointerException.class, () -> distribuerBrevTask.doTask(prosessTaskData));

        Assertions.assertEquals("Name is null", thrown.getMessage());
        verify(taskTjeneste, never()).lagre(any(ProsessTaskData.class));
    }

    @Test
    void testKlientKallOpprettGosysOppgaveHvisAdresseMangler() {
        var journalpostId = "12345";
        var bestillingsid = "456789";
        var distribusjonstype = Distribusjonstype.VEDTAK;

        when(dokdist.distribuerJournalpost(any(DistribuerJournalpostRequest.class))).thenReturn(
                Dokdist.Resultat.MANGLER_ADRESSE);

        var prosessTaskData = opprettProsessTaskData(journalpostId, bestillingsid, distribusjonstype);

        new DistribuerBrevTask(dokdist, taskTjeneste).doTask(prosessTaskData);

        verify(taskTjeneste, times(1)).lagre(any(ProsessTaskData.class));
    }

    @Test
    void testKlientKallIkkeOpprettOppgaveHvisResultatetErOk() {
        var journalpostId = "12345";
        var bestillingsid = "456789";
        var distribusjonstype = Distribusjonstype.VEDTAK;

        when(dokdist.distribuerJournalpost(any(DistribuerJournalpostRequest.class))).thenReturn(
                Dokdist.Resultat.OK);

        var prosessTaskData = opprettProsessTaskData(journalpostId, bestillingsid, distribusjonstype);

        new DistribuerBrevTask(dokdist, taskTjeneste).doTask(prosessTaskData);

        verify(taskTjeneste, never()).lagre(any(ProsessTaskData.class));
    }

    @NotNull
    private ProsessTaskData opprettProsessTaskData(String journalpostId, String bestillingsid, Distribusjonstype distribusjonstype) {
        var prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId);
        prosessTaskData.setProperty(BrevTaskProperties.BESTILLING_ID, bestillingsid);
        prosessTaskData.setProperty(BrevTaskProperties.DISTRIBUSJONSTYPE, distribusjonstype.name());
        return prosessTaskData;
    }
}
