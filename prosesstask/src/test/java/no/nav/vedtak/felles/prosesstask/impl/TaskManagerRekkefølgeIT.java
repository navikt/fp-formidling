package no.nav.vedtak.felles.prosesstask.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.dbstoette.UnittestRepositoryRule;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;

@RunWith(CdiRunner.class)
public class TaskManagerRekkefølgeIT {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    @Inject
    private ProsessTaskRepositoryImpl repo;

    @Inject
    private TaskManagerRepositoryImpl taskManagerRepo;

    private LocalDateTime now = LocalDateTime.now();

    @Before
    public void setupTestData() throws Exception {
        TestProsessTaskTestData testData = new TestProsessTaskTestData(repoRule.getEntityManager());
        for (int i = 1; i <= 10; i++) {
            testData.opprettTaskType("mytask" + i);
        }
    }

    @Test
    public void skal_finne_sql_for_polling() throws Exception {
        assertThat(taskManagerRepo.getSqlForPolling()).isNotNull();
    }

    @Test
    public void skal_polle_tasker_i_enkel_sekvens() throws Exception {
        ProsessTaskData pt1 = nyTask("mytask1", -10);
        ProsessTaskData pt2 = nyTask("mytask2", -10);
        ProsessTaskData pt3 = nyTask("mytask3", -10);
        ProsessTaskGruppe sammensatt = new ProsessTaskGruppe();
        sammensatt
                .addNesteSekvensiell(pt1)
                .addNesteSekvensiell(pt2)
                .addNesteSekvensiell(pt3);

        // Act
        repo.lagre(sammensatt);

        pollEnRundeVerifiserOgFerdigstill(pt1);

        pollEnRundeVerifiserOgFerdigstill(pt2);

        pollEnRundeVerifiserOgFerdigstill(pt3);

        List<ProsessTaskData> sekvensiellRunde04 = taskManagerRepo.pollNeste(now);
        assertThat(sekvensiellRunde04).isEmpty();

    }

    @Test
    public void skal_ikke_polle_ny_når_får_feil_på_første() throws Exception {
        ProsessTaskData pt1 = nyTask("mytask1", -10);
        ProsessTaskData pt2 = nyTask("mytask2", -10);
        ProsessTaskGruppe sammensatt = new ProsessTaskGruppe();
        sammensatt
                .addNesteSekvensiell(pt1)
                .addNesteSekvensiell(pt2);

        // Act
        repo.lagre(sammensatt);

        pollEnRundeVerifiserOgMarkerFeil(pt1);

        List<ProsessTaskData> sekvensiellRunde02 = taskManagerRepo.pollNeste(now);
        assertThat(sekvensiellRunde02).isEmpty();
    }

    @Test
    public void skal_polle_tasker_i_sekvens_med_feil_i_midten() throws Exception {
        ProsessTaskData pt1 = nyTask("mytask1", -10);
        ProsessTaskData pt2 = nyTask("mytask2", -10);
        ProsessTaskData pt3 = nyTask("mytask3", -10);
        ProsessTaskGruppe sammensatt = new ProsessTaskGruppe();
        sammensatt
                .addNesteSekvensiell(pt1)
                .addNesteSekvensiell(pt2)
                .addNesteSekvensiell(pt3);

        // Act
        repo.lagre(sammensatt);

        pollEnRundeVerifiserOgFerdigstill(pt1);

        pollEnRundeVerifiserOgMarkerFeil(pt2);

        List<ProsessTaskData> sekvensiellRunde03 = taskManagerRepo.pollNeste(now);
        assertThat(sekvensiellRunde03).isEmpty();

    }

    private void pollEnRundeVerifiserOgMarkerFeil(ProsessTaskData pt) {
        List<ProsessTaskData> neste = taskManagerRepo.pollNeste(now);
        assertThat(neste).containsExactly(pt);
        pt.setStatus(ProsessTaskStatus.FEILET);
        pt.setAntallFeiledeForsøk(1);
        pt.setNesteKjøringEtter(null);
        pt.setSistKjørt(LocalDateTime.now());

        repo.lagre(pt);
        repo.flushAndClear();
    }

    private void pollEnRundeVerifiserOgFerdigstill(ProsessTaskData... tasks) {
        List<ProsessTaskData> neste = taskManagerRepo.pollNeste(now);
        assertThat(neste).hasSize(tasks.length);
        assertThat(neste).containsExactly(tasks);

        neste.forEach(pt -> {
            pt.setStatus(ProsessTaskStatus.FERDIG);
            repo.lagre(pt);
        });

        repo.flushAndClear();
    }

    @Test
    public void skal_polle_3_tasker_i_parallell_deretter_1_sekvensielt_for_avslutning() throws Exception {

        // Arrange
        ProsessTaskData pt1 = nyTask("mytask1", -10);
        ProsessTaskData pt2 = nyTask("mytask2", -10);
        ProsessTaskData pt3 = nyTask("mytask3", -10);
        ProsessTaskData pt4 = nyTask("mytask4", -10);

        ProsessTaskGruppe sammensatt = new ProsessTaskGruppe();
        sammensatt
                .addNesteParallell(pt1, pt2, pt3)
                .addNesteSekvensiell(pt4);

        repo.lagre(sammensatt);
        repo.flushAndClear();

        // Act and Assert
        pollEnRundeVerifiserOgFerdigstill(pt1, pt2, pt3);

        pollEnRundeVerifiserOgFerdigstill(pt4);

    }

    @Test
    public void skal_polle_tasker_i_sekvens_så_parallell() throws Exception {

        ProsessTaskData pt1 = nyTask("mytask1", -10);
        ProsessTaskData pt2 = nyTask("mytask2", -10);
        ProsessTaskData pt3 = nyTask("mytask3", -10);
        ProsessTaskGruppe sammensatt = new ProsessTaskGruppe();
        sammensatt
                .addNesteSekvensiell(pt1)
                .addNesteParallell(pt2, pt3);

        // Act
        repo.lagre(sammensatt);
        repo.flushAndClear();

        pollEnRundeVerifiserOgFerdigstill(pt1);

        pollEnRundeVerifiserOgFerdigstill(pt2, pt3);

    }

    private ProsessTaskData nyTask(String taskNavn, int nesteKjøringRelativt) {
        ProsessTaskData task = new ProsessTaskData(taskNavn);
        task.setNesteKjøringEtter(now.plusSeconds(nesteKjøringRelativt));
        return task;
    }
}
