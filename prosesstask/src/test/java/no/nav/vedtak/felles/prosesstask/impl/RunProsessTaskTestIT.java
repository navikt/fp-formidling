package no.nav.vedtak.felles.prosesstask.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskDispatcher;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.dbstoette.UnittestRepositoryRule;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;

@RunWith(CdiRunner.class)
public class RunProsessTaskTestIT {

    private static final String TASK1 = "mytask1";
    private static final String TASK2 = "mytask2";
    private static final String TASK3 = "mytask3";

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule(false);

    private static LastResult getBean() {
        return CDI.current().select(LastResult.class).get();
    }

    @Before
    public void setupTestData() throws Exception {
        repoRule.doInTransaction(this::testData);
    }

    @After
    public void tearDown() throws Exception {
        getTaskManager().stop();
        repoRule.doInTransaction(this::slettTestData);
    }

    private TaskManager getTaskManager() {
        return CDI.current().select(TaskManager.class).get();
    }

    @Test
    public void skal_starte_TaskManager_polle_og_finne_tasks() throws Exception {
        TaskManager taskManager = getTaskManager();

        taskManager.configureTaskThreads(1, 1);
        taskManager.startTaskThreads();

        EntityManager entityManager = taskManager.getTransactionManagerRepository().getEntityManager();
        List<IdentRunnable> tasksPolled = repoRule.doInTransaction(entityManager, (em) -> taskManager.pollForAvailableTasks());
        assertThat(tasksPolled).hasSize(1);
    }

    @Test
    public void skal_starte_TaskManager_polle_og_kjøre_tasks_i_egne_tråder() throws Exception {
        AtomicBoolean kjørt = new AtomicBoolean();
        ProsessTaskDispatcher taskDispatcher = (task) -> kjørt.set(true);

        testEnTask(taskDispatcher);

        assertThat(kjørt.get()).isTrue();
    }

    @Test
    public void skal_starte_TaskManager_polle_og_kjøre_en_task_som_får_egen_transaksjon() throws Exception {
        ProsessTaskDispatcher taskDispatcher = new BasicCdiProsessTaskDispatcher();

        // Act
        testEnTask(taskDispatcher);

        // Assert
        assertThat(getBean().getLastHandler()).isInstanceOf(DummyProsessTask.class);
        ProsessTaskData last = getBean().getLastData();
        assertThat(last).isNotNull();
        assertThat(last.getId()).isNotNull();
        assertThat(last.getTaskType()).isEqualTo(TASK3);

        ProsessTaskEntitet lagret = repoRule.doInTransaction((em) -> em.find(ProsessTaskEntitet.class, last.getId()));
        assertThat(lagret.getTaskName()).isEqualTo(TASK3);

    }

    private Object slettTestData(EntityManager em) throws SQLException {
        TestProsessTaskTestData data = new TestProsessTaskTestData(em);
        data.slettAlleProssessTask();
        data.slettUtvalgtProssessTaskType(TASK1);
        data.slettUtvalgtProssessTaskType(TASK2);
        data.slettUtvalgtProssessTaskType(TASK3);
        return null;
    }

    private Object testData(EntityManager em) throws SQLException {
        TestProsessTaskTestData testData = new TestProsessTaskTestData(em);
        testData.slettAlleProssessTask();
        LocalDateTime kjørEtter = LocalDateTime.now().minusSeconds(50);
        testData.opprettTaskType(TASK1)
                .opprettTaskType(TASK2)
                .opprettTaskType(TASK3)
                .opprettTask(new ProsessTaskData(TASK1).medNesteKjøringEtter(kjørEtter).medSekvens("a"))
                .opprettTask(new ProsessTaskData(TASK2).medNesteKjøringEtter(kjørEtter).medSekvens("a"));

        return null;
    }

    private void testEnTask(ProsessTaskDispatcher taskDispatcher) throws InterruptedException {
        TaskManager taskManager = getTaskManager();
        taskManager.setProsessTaskDispatcher(taskDispatcher);

        taskManager.configureTaskThreads(1, 1);
        taskManager.startTaskThreads();

        int tasksPolled = taskManager.doSinglePolling();
        assertThat(tasksPolled).isEqualTo(1);

        CountDownLatch latch = new CountDownLatch(1);
        taskManager.getRunTaskService().submit(new IdentRunnableTask(1L, latch::countDown));

        assertThat(latch.await(120, TimeUnit.SECONDS)).isTrue();
    }

    @ProsessTask(TASK1)
    static class DummyProsessTask implements ProsessTaskHandler {

        @Inject
        ProsessTaskRepository repo;

        @Override
        public void doTask(ProsessTaskData data) {
            ProsessTaskData nyProsessTask = new ProsessTaskData(TASK3);
            repo.lagre(nyProsessTask);

            getBean().invoked(this, nyProsessTask);
        }
    }

    @ApplicationScoped
    static class LastResult {

        private ProsessTaskData data;
        private ProsessTaskHandler handler;

        ProsessTaskData getLastData() {
            return data;
        }

        ProsessTaskHandler getLastHandler() {
            return handler;
        }

        void invoked(ProsessTaskHandler bean, ProsessTaskData data) {
            this.handler = bean;
            this.data = data;
        }

    }

}
