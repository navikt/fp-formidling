package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties.JOURNALPOST_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Dokdist;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.felles.prosesstask.api.TaskType;

@ExtendWith(MockitoExtension.class)
class DistribuerBrevTaskTest {

    private static final String SAKSNUMMER = "9999";
    private static final UUID BEHANDLING_UUID = UUID.randomUUID();

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
        prosessTaskData.setProperty(JOURNALPOST_ID, journalpostId);
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

        when(dokdist.distribuerJournalpost(any(DistribuerJournalpostRequest.class))).thenReturn(Dokdist.Resultat.MANGLER_ADRESSE);

        // Act
        var prosessTaskData = opprettProsessTaskData(journalpostId, bestillingsid, distribusjonstype);
        new DistribuerBrevTask(dokdist, taskTjeneste).doTask(prosessTaskData);

        // Assert
        var prosessTaskDataCaptor = ArgumentCaptor.forClass(ProsessTaskData.class);
        verify(taskTjeneste).lagre(prosessTaskDataCaptor.capture());

        var taskData = prosessTaskDataCaptor.getValue();
        assertThat(taskData.taskType()).isEqualTo(TaskType.forProsessTask(OpprettOppgaveTask.class));
        assertThat(taskData.getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(taskData.getBehandlingUuid()).isEqualTo(BEHANDLING_UUID);
        assertThat(taskData.getPropertyValue(JOURNALPOST_ID)).isEqualTo(journalpostId);
    }

    @Test
    void testKlientKallIkkeOpprettOppgaveHvisResultatetErOk() {
        var journalpostId = "12345";
        var bestillingsid = "456789";
        var distribusjonstype = Distribusjonstype.VEDTAK;

        when(dokdist.distribuerJournalpost(any(DistribuerJournalpostRequest.class))).thenReturn(Dokdist.Resultat.OK);

        var prosessTaskData = opprettProsessTaskData(journalpostId, bestillingsid, distribusjonstype);

        new DistribuerBrevTask(dokdist, taskTjeneste).doTask(prosessTaskData);

        verify(taskTjeneste, never()).lagre(any(ProsessTaskData.class));
    }

    private ProsessTaskData opprettProsessTaskData(String journalpostId, String bestillingsid, Distribusjonstype distribusjonstype) {
        var prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setSaksnummer(SAKSNUMMER);
        prosessTaskData.setBehandlingUUid(BEHANDLING_UUID);
        prosessTaskData.setProperty(JOURNALPOST_ID, journalpostId);
        prosessTaskData.setProperty(BrevTaskProperties.BESTILLING_ID, bestillingsid);
        prosessTaskData.setProperty(BrevTaskProperties.DISTRIBUSJONSTYPE, distribusjonstype.name());
        return prosessTaskData;
    }
}
