package no.nav.vedtak.felles.prosesstask.impl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.nav.vedtak.felles.jpa.VLPersistenceUnit;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskDispatcher;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.dbstoette.UnittestRepositoryRule;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;

@Ignore("FIXME (u139158): PK-41761 ContainerLogin gjør eksterne kall, må under CDI så vi kan dytte inn en som ikke trenger det")
@RunWith(CdiRunner.class)
public class CdiProsessTaskDispatcherTest {

    static final String PROSESS_TASK = "dummyProsessTask";
    static final String ANNEN_PROSESS_TASK = "annenProsessTask";
    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    static LastResult getBean() {
        return CDI.current().select(LastResult.class).get();
    }

    AuthenticatedCdiProsessTaskDispatcher getTaskDispatcher() {
        return CDI.current().select(AuthenticatedCdiProsessTaskDispatcher.class).get();
    }

    @Test
    public void skal_dispatche_prosess_task_til_riktig_handler() throws Exception {

        ProsessTaskDispatcher prosessTaskDispatcher = getTaskDispatcher();

        ProsessTaskData prosessTaskData = new ProsessTaskData(PROSESS_TASK);
        prosessTaskDispatcher.dispatch(prosessTaskData);
        assertThat(getBean().getLastData()).isSameAs(prosessTaskData);
        assertThat(getBean().getLastHandler()).isInstanceOf(DummyProsessTask.class);

        ProsessTaskData annenProsessTaskData = new ProsessTaskData(ANNEN_PROSESS_TASK);
        prosessTaskDispatcher.dispatch(annenProsessTaskData);
        assertThat(getBean().getLastData()).isSameAs(annenProsessTaskData);
        assertThat(getBean().getLastHandler()).isInstanceOf(DummyAnnenProsessTask.class);
    }

    @ProsessTask(ANNEN_PROSESS_TASK)
    static class DummyAnnenProsessTask implements ProsessTaskHandler {
        @Override
        public void doTask(ProsessTaskData data) {
            getBean().invoked(this, data);
        }
    }

    @ProsessTask(PROSESS_TASK)
    static class DummyProsessTask implements ProsessTaskHandler {

        @Inject
        @VLPersistenceUnit
        private EntityManager em;

        @Override
        public void doTask(ProsessTaskData data) {
            assertThat(em).isNotNull();
            getBean().invoked(this, data);
        }

    }

    @ApplicationScoped
    static class LastResult {

        private ProsessTaskData data;
        private ProsessTaskHandler handler;

        public ProsessTaskData getLastData() {
            return data;
        }

        public ProsessTaskHandler getLastHandler() {
            return handler;
        }

        public void invoked(ProsessTaskHandler bean, ProsessTaskData data) {
            this.handler = bean;
            this.data = data;
        }

    }
}
