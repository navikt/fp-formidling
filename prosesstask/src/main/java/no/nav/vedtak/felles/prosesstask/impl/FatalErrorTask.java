package no.nav.vedtak.felles.prosesstask.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.jboss.weld.interceptor.util.proxy.TargetInstanceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.AktiverContextOgTransaksjon;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;

/**
 * Kjører en task. Flere JVM'er kan kjøre tasks i parallell
 * <p>
 * Kun en task kjøres, i sin egen transaksjon.
 * <p>
 * Denne tasken logger kun error til databasen i separat transaksjon. Brukes når {@link RunTask} totalhavarerer og transaksjon den kjørte i
 * blir rullet tilbake helt.
 */
@Dependent
@AktiverContextOgTransaksjon
public class FatalErrorTask {
    private static final Logger log = LoggerFactory.getLogger(FatalErrorTask.class);
    private TaskManagerRepositoryImpl taskManagerRepository;

    public FatalErrorTask() {
    }

    @Inject
    public FatalErrorTask(TaskManagerRepositoryImpl taskManagerRepo) {
        Objects.requireNonNull(taskManagerRepo, "taskManagerRepo"); //$NON-NLS-1$

        this.taskManagerRepository = taskManagerRepo;
    }

    public void doRun(RunTaskInfo taskInfo, Throwable t) {
        new PickAndRunErrorTask(taskInfo).runTask(t);
    }

    private EntityManager getEntityManager() {
        return taskManagerRepository.getEntityManager();
    }

    /**
     * Denne klassen enkapsulerer plukk og kjør en task, og tilhørende bokføring av status og tidsstempler
     * på kjøringen.
     */
    class PickAndRunErrorTask {

        private final RunTaskInfo taskInfo;

        PickAndRunErrorTask(RunTaskInfo taskInfo) {
            this.taskInfo = taskInfo;
        }

        RunTaskInfo getTaskInfo() {
            return taskInfo;
        }

        @SuppressWarnings("rawtypes")
        void runTask(Throwable t) {

            /* Bruker SQL+JDBC, unngår hibernate cache. */
            class PullSingleTask implements Work {
                @Override
                public void execute(Connection conn) throws SQLException {
                    Optional<ProsessTaskEntitet> opt = taskManagerRepository.finnOgLås(taskInfo);
                    if (opt.isPresent()) {
                        ProsessTaskEntitet pte = opt.get();

                        // NB: her fyrer p.t. ikke events hvis feilet. Logger derfor bare her til logg og database. Antar alle feil fanget her er fatale.
                        int feiledeForsøk = pte.getFeiledeForsøk() + 1;
                        String taskName = pte.getTaskName();
                        Long taskId = pte.getId();
                        Feil feil = TaskManagerFeil.FACTORY.kunneIkkeProsessereTaskPgaFatalFeilVilIkkePrøveIgjen(taskId, taskName, feiledeForsøk, t);
                        String feilMelding = RunTaskFeilOgStatusEventHåndterer.getFeiltekstOgLoggHvisFørstegang(pte, feil, t);

                        // TODO: denne bør harmoniseres med RunTaskFeilOgStatusEventHåndterer#handleTaskFeil?
                        taskManagerRepository.oppdaterStatusOgNesteKjøring(pte.getId(), ProsessTaskStatus.FEILET, null, feil.getKode(), feilMelding,
                                feiledeForsøk);
                    } else {
                        TaskManagerFeil.FACTORY.kritiskFeilKanIkkeSkriveFeilTilbakeTilDatabaseFikkIkkeLås(taskInfo.getId(), taskInfo.getTaskType())
                                .log(log);
                    }
                }

            }

            PullSingleTask pullSingleTask = new PullSingleTask();
            EntityManager em = getEntityManager();
            // workaround for hibernate issue HHH-11020
            if (em instanceof TargetInstanceProxy) {
                em = (EntityManager) ((TargetInstanceProxy) em).weld_getTargetInstance();
            }

            @SuppressWarnings("resource") // skal ikke lukke session her
                    Session session = em.unwrap(Session.class);

            session.doWork(pullSingleTask);

        }

    }

}
