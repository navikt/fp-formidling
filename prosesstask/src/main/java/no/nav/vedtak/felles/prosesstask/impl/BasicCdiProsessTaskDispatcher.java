package no.nav.vedtak.felles.prosesstask.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;

import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskDispatcher;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskInfo;

/**
 * Implementerer dispatch vha. CDI scoped beans.
 */
public class BasicCdiProsessTaskDispatcher implements ProsessTaskDispatcher {
    @Override
    public void dispatch(ProsessTaskData task) {
        try (ProsessTaskHandlerRef prosessTaskHandler = findHandler(task)) {
            prosessTaskHandler.doTask(task);
        }
    }

    public ProsessTaskHandlerRef findHandler(ProsessTaskInfo task) {
        ProsessTaskHandler prosessTaskHandler = CDI.current()
                .select(ProsessTaskHandler.class, new ProsessTaskLiteral(task.getTaskType())).get();
        return new ProsessTaskHandlerRef(prosessTaskHandler);
    }

    /**
     * Referanse til en {@link ProsessTaskHandler}.
     */
    public static class ProsessTaskHandlerRef implements AutoCloseable {

        private ProsessTaskHandler bean;

        ProsessTaskHandlerRef(ProsessTaskHandler bean) {
            this.bean = bean;
        }

        @Override
        public void close() {
            if (bean == null) {
                return;
            }

            if (bean.getClass().isAnnotationPresent(Dependent.class)) {
                // må closes hvis @Dependent scoped siden vi slår opp. ApplicationScoped alltid ok. RequestScope også ok siden vi kjører med det.
                CDI.current().destroy(bean);
            }
        }

        public void doTask(ProsessTaskData prosessTaskData) {
            bean.doTask(prosessTaskData);
        }

        public ProsessTaskHandler getBean() {
            return bean;
        }

    }

    /**
     * Lookup Literal Referanse til en {@link ProsessTaskHandler} for CDI.
     */
    public static class ProsessTaskLiteral extends AnnotationLiteral<ProsessTask> implements ProsessTask {

        private String taskType;

        public ProsessTaskLiteral(String taskType) {
            this.taskType = taskType;
        }

        @Override
        public String value() {
            return taskType;
        }

    }
}
