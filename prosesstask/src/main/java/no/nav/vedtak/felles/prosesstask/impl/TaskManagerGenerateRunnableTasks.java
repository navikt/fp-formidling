package no.nav.vedtak.felles.prosesstask.impl;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskDispatcher;
import no.nav.vedtak.felles.prosesstask.impl.TaskManager.ReadTaskFunksjon;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.util.MdcExtendedLogContext;

/**
 * Poller for tilgjengelige tasks og omsetter disse til Runnable som kan kjøres på andre tråder.
 */
public class TaskManagerGenerateRunnableTasks {
    private static final Logger log = LoggerFactory.getLogger(TaskManagerGenerateRunnableTasks.class);
    private static final MdcExtendedLogContext LOG_CONTEXT = MdcExtendedLogContext.getContext("prosess"); //$NON-NLS-1$
    private static final CDI<Object> CURRENT = CDI.current();

    private final BiFunction<Integer, ReadTaskFunksjon, List<IdentRunnable>> availableTasksFunc;
    private final ProsessTaskDispatcher taskDispatcher;
    private Consumer<IdentRunnable> fatalErrorSubmitFunc;

    /**
     * Constructor
     *
     * @param taskDispatcher     - dispatcher som skal velge implementasjon og kjøre en spesifikk task
     * @param availableTasksFunc - funksjon for å polle tilgenglige tasks
     */
    TaskManagerGenerateRunnableTasks(
            ProsessTaskDispatcher taskDispatcher,
            BiFunction<Integer, ReadTaskFunksjon, List<IdentRunnable>> availableTasksFunc,
            Consumer<IdentRunnable> errorSubmitFunc) {
        this.taskDispatcher = taskDispatcher;
        this.availableTasksFunc = availableTasksFunc;
        this.fatalErrorSubmitFunc = errorSubmitFunc;
    }

    public List<IdentRunnable> execute(int numberOfTasksToPoll) {
        return availableTasksFunc.apply(numberOfTasksToPoll, this::readTask);
    }

    IdentRunnable readTask(ProsessTaskEntitet pte) {
        final RunTaskInfo taskInfo = new RunTaskInfo(taskDispatcher, pte.tilProsessTask());
        final String callId = pte.getPropertyValue(MDCOperations.MDC_CALL_ID);
        String taskName = pte.getTaskName();
        Runnable r = () -> {
            RunTask runSingleTask = newRunTaskInstance();
            IdentRunnable errorCallback = null;
            try {
                initLogContext(callId, taskName);

                runSingleTask.doRun(taskInfo);

            } catch (PersistenceException fatal) {
                // transaksjonen er rullet tilbake, markert for rollback eller inaktiv nå. Submitter derfor en oppdatering som en separat oppgave (gjennom
                // en callback).
                errorCallback = lagErrorCallback(taskInfo, callId, fatal);
            } catch (Exception e) {
                errorCallback = lagErrorCallback(taskInfo, callId, e);
            } catch (Throwable t) { // NOSONAR
                errorCallback = lagErrorCallback(taskInfo, callId, t);
            } finally {
                clearLogContext();
                // dispose CDI etter bruk
                CURRENT.destroy(runSingleTask);

                // kjør etter at runTask er destroyed og logcontext renset
                handleErrorCallback(errorCallback);
            }
        };


        return new IdentRunnableTask(pte.getId(), r);
    }

    void handleErrorCallback(IdentRunnable errorCallback) {
        if (errorCallback != null) {
            // NB - kjøres i annen transaksjon enn opprinnelig
            fatalErrorSubmitFunc.accept(errorCallback);
        }
    }

    IdentRunnable lagErrorCallback(final RunTaskInfo taskInfo, final String callId, final Throwable fatal) {
        Runnable errorCallback;
        errorCallback = () -> {
            final FatalErrorTask errorTask = CURRENT.select(FatalErrorTask.class).get();
            try {
                initLogContext(callId, taskInfo.getTaskType());
                errorTask.doRun(taskInfo, fatal);
            } catch (Throwable t) {  // NOSONAR
                // logg at vi ikke klarte å registrer feilen i db
                Feil feil = TaskManagerFeil.FACTORY.kunneIkkeLoggeUventetFeil(taskInfo.getId(), taskInfo.getTaskType(), t);
                feil.log(log);
            } finally {
                clearLogContext();
                CURRENT.destroy(errorTask);
            }
        };

        // logg at vi kommer til å skrive dette i ny transaksjon pga fatal feil.
        TaskManagerFeil.FACTORY.kritiskFeilKunneIkkeProsessereTaskPgaFatalFeil(taskInfo.getId(), taskInfo.getTaskType(), fatal)
                .log(log);

        return new IdentRunnableTask(taskInfo.getId(), errorCallback);
    }

    void clearLogContext() {
        LOG_CONTEXT.clear();
        MDCOperations.removeCallId();
    }

    void initLogContext(final String callId, String taskName) {
        if (callId != null) {
            MDCOperations.putCallId(callId);
        } else {
            MDCOperations.putCallId();
        }
        LOG_CONTEXT.add("task", taskName); //$NON-NLS-1$
    }

    RunTask newRunTaskInstance() {
        return CURRENT.select(RunTask.class).get();
    }

}
