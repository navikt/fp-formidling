package no.nav.vedtak.felles.prosesstask.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLRecoverableException;
import java.sql.SQLTransientException;
import java.sql.Savepoint;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.jdbc.Work;
import org.jboss.weld.interceptor.util.proxy.TargetInstanceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.AktiverContextOgTransaksjon;
import no.nav.vedtak.felles.jpa.savepoint.SavepointRolledbackException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.impl.cron.CronExpression;
import no.nav.vedtak.felles.prosesstask.spi.ProsessTaskFeilhåndteringAlgoritme;
import no.nav.vedtak.util.FPDateUtil;

/**
 * Kjører en task. Flere JVM'er kan kjøre tasks i parallell
 * <p>
 * Kun en task kjøres, i sin egen transaksjon.
 * <p>
 * Dersom en task kjøring feiler, benyttes spesifisert feilhåndteringsalgoritme til å avgjøre hvordan det skal håndteres, og evt. prøves på
 * nytt. (default algoritme prøver 3 ganger med mindre det er kritisk feil).
 */
@Dependent
@AktiverContextOgTransaksjon
public class RunTask {
    private static final Logger log = LoggerFactory.getLogger(RunTask.class);

    private ProsessTaskEventPubliserer eventPubliserer;
    private TaskManagerRepositoryImpl taskManagerRepository;
    private Instance<ProsessTaskFeilhåndteringAlgoritme> feilhåndteringalgoritmer;
    private RunTaskVetoHåndterer vetoHåndterer;

    public RunTask() {
    }

    @Inject
    public RunTask(TaskManagerRepositoryImpl taskManagerRepo, ProsessTaskEventPubliserer eventPubliserer,
                   @Any Instance<ProsessTaskFeilhåndteringAlgoritme> feilhåndteringsalgoritmer) {
        Objects.requireNonNull(taskManagerRepo, "taskManagerRepo"); //$NON-NLS-1$

        this.eventPubliserer = eventPubliserer;
        this.taskManagerRepository = taskManagerRepo;
        this.feilhåndteringalgoritmer = feilhåndteringsalgoritmer;
        this.vetoHåndterer = new RunTaskVetoHåndterer(eventPubliserer, getEntityManager());
    }

    public void doRun(RunTaskInfo taskInfo) {
        new PickAndRunTask(taskInfo).runTask();
    }

    /**
     * Markerer task for kjøring, tar savepoint og forsøker å kjøre task, inklusiv håndtering av forventede feil. Uventede feil som forårsaker
     * rollback av hele transaksjonen (eks {@link EntityNotFoundException} delegeres oppover). Gjelder også totalt transiente feil (eks.
     * JDBCConnectionException)
     *
     * @throws SQLException         - dersom ikke kan ta savepoint
     * @throws PersistenceException dersom transaksjoner er markert for total rollback (dvs. savepoint vil ikke virke)
     */
    protected void runTaskAndUpdateStatus(Connection conn, ProsessTaskEntitet pte, PickAndRunTask pickAndRun)
            throws SQLException {
        String name = pte.getTaskName();

        pickAndRun.markerTaskUnderArbeid(pte);

        // set up a savepoint to rollback to in case of failure
        Savepoint savepoint = conn.setSavepoint();

        try {
            if (vetoHåndterer.vetoRunTask(pte)) {
                return;
            }

            pickAndRun.dispatchWork(pte);

            // flush for å fange andre constraint feil etc før vi markerer ferdig
            getEntityManager().flush();

            if (ProsessTaskStatus.KLAR == pte.getStatus()) {
                ProsessTaskStatus sluttStatus = pickAndRun.markerTaskFerdig(pte);

                log.info("Prosesstask [{}], id={}, status={}", pte.getTaskName(), pte.getId(), sluttStatus);
                pickAndRun.planleggNesteKjøring(pte);
            }

        } catch (JDBCConnectionException
                | SQLTransientException
                | SQLNonTransientConnectionException
                | SQLRecoverableException e) {

            // vil kun logges
            pickAndRun.getFeilOgStatushåndterer().handleTransientAndRecoverableException(e);

        } catch (SavepointRolledbackException e) {

            if (erTransaksjonRollbackEllerInaktiv()) {
                // håndter likt som vanlige exceptions (under) med mindre transaksjonen er markert for rollback
                throw new PersistenceException(e);
            } else {
                // implementasjon av task har kastet en feil samtidig som det har rullet tilbake et egen-definert savepoint.
                // fanger opp dette og lagrer som feil på prosess task'en.
                // Transaksjonen vil fortsatt committes (men da uten den tilstand som er rullet tilbake til et savepoint).

                // NB: Task forventes å være robust nok til å kunne gjentas dersom feilhåndteringsalgortime er konfigurert til det.

                // allerede rullet tilbake, skal ikke rulle mer her
                // anta feil kan skrives tilbake til databasen
                pickAndRun.getFeilOgStatushåndterer().handleTaskFeil(pte, e);
                // NB: pt. har denne samme feilhåndtering som andre exceptions (se under) bortsett fra at savepoint her rulles ikke tilbake.
            }

        } catch (InjectionException e) {
            // Fatal feil, kan ikke kjøre denne på nytt uansett

            if (erTransaksjonRollbackEllerInaktiv()) {
                // håndter likt som vanlige exceptions (under) med mindre transaksjonen er markert for rollback
                throw new PersistenceException(e);
            } else {
                getEntityManager().clear(); // fjern mulig korrupt tilstand
                conn.rollback(savepoint); // rull tilbake til savepoint

                Feil feil = TaskManagerFeil.FACTORY.kunneIkkeProsessereTaskFeilKonfigurasjon(pickAndRun.getTaskInfo().getId(), name, e);
                pickAndRun.getFeilOgStatushåndterer().handleFatalTaskFeil(pte, feil, e);
            }
        } catch (Exception e) {

            if (erTransaksjonRollbackEllerInaktiv()) {
                // håndter likt som vanlige exceptions (under) med mindre transaksjonen er markert for rollback
                throw (e instanceof PersistenceException) ? (PersistenceException) e : new PersistenceException(e);
            } else {
                getEntityManager().clear(); // fjern mulig korrupt tilstand
                conn.rollback(savepoint); // rull tilbake til savepoint

                // anta feil kan skrives tilbake til databasen
                pickAndRun.getFeilOgStatushåndterer().handleTaskFeil(pte, e);
            }
        }
    }

    private boolean erTransaksjonRollbackEllerInaktiv() {
        return !getEntityManager().getTransaction().isActive() || getEntityManager().getTransaction().getRollbackOnly();
    }

    private EntityManager getEntityManager() { // NOSONAR
        return taskManagerRepository.getEntityManager();
    }

    /**
     * Denne klassen enkapsulerer plukk og kjør en task, og tilhørende bokføring av status og tidsstempler
     * på kjøringen.
     */
    class PickAndRunTask {

        private final RunTaskInfo taskInfo;
        private final RunTaskFeilOgStatusEventHåndterer feilOgStatushåndterer;

        PickAndRunTask(RunTaskInfo taskInfo) {
            this.feilOgStatushåndterer = new RunTaskFeilOgStatusEventHåndterer(taskInfo, eventPubliserer, taskManagerRepository, feilhåndteringalgoritmer);
            this.taskInfo = taskInfo;
        }

        RunTaskInfo getTaskInfo() {
            return taskInfo;
        }

        RunTaskFeilOgStatusEventHåndterer getFeilOgStatushåndterer() {
            return feilOgStatushåndterer;
        }

        private ProsessTaskEntitet refreshProsessTask(Long id) {
            return getEntityManager().find(ProsessTaskEntitet.class, id);
        }

        ProsessTaskStatus markerTaskFerdig(ProsessTaskEntitet pte) {
            ProsessTaskStatus nyStatus = ProsessTaskStatus.FERDIG;
            taskManagerRepository.oppdaterStatusOgTilFerdig(pte.getId(), nyStatus);

            pte = refreshProsessTask(pte.getId());
            feilOgStatushåndterer.publiserNyStatusEvent(pte.tilProsessTask(), ProsessTaskStatus.KLAR, nyStatus);

            // frigir veto etter at event handlere er fyrt
            taskManagerRepository.frigiVeto(pte);
            return nyStatus;
        }

        // markerer task som påbegynt (merk committer ikke før til slutt).
        void markerTaskUnderArbeid(ProsessTaskEntitet pte) {
            // mark row being processed with timestamp and server process id
            LocalDateTime now = FPDateUtil.nå();
            pte.setSisteKjøring(now);
            pte.setSisteKjøringServer(Utils.getJvmUniqueProcessName());
            getEntityManager().persist(pte);
            getEntityManager().flush();
        }

        // regner ut neste kjøretid for tasks som kan repeteres (har CronExpression) 
        void planleggNesteKjøring(ProsessTaskEntitet pte) throws SQLException {
            ProsessTaskType taskType = taskManagerRepository.getTaskType(getTaskInfo().getTaskType());
            if (taskType.getErGjentagende()) {
                ProsessTaskData data = new ProsessTaskData(pte.getTaskName());
                data.setStatus(ProsessTaskStatus.KLAR);
                LocalDateTime nesteKjøring = new CronExpression(taskType.getCronExpression()).neste(pte.getSisteKjøring());
                data.setNesteKjøringEtter(nesteKjøring);
                data.setGruppe(getUniktProsessTaskGruppeNavn());
                data.setSekvens(pte.getSekvens());
                data.setProperties(pte.getProperties());
                ProsessTaskEntitet nyPte = new ProsessTaskEntitet().kopierFra(data);

                getEntityManager().persist(nyPte);
                getEntityManager().flush();

                log.info("Oppretter ny prosesstask [{}], id={}, status={}, kjøretidspunktEtter={}",
                        nyPte.getTaskName(),
                        nyPte.getId(),
                        nyPte.getStatus(),
                        nyPte.getNesteKjøringEtter());
            }
        }

        /**
         * Lager enkelt unik gruppenavn basert på en sekvens.
         * Aller helst skulle vært UUID type 3?
         */
        private String getUniktProsessTaskGruppeNavn() throws SQLException {
            Query query = getEntityManager().createNativeQuery("SELECT nextval('seq_prosess_task_gruppe')"); //$NON-NLS-1$
            return String.valueOf(query.getSingleResult());
        }

        void dispatchWork(ProsessTaskEntitet pte) throws Exception { // NOSONAR
            ProsessTaskData taskData = pte.tilProsessTask();
            taskInfo.getTaskDispatcher().dispatch(taskData);
        }

        private EntityManager getEntityManager() {
            return taskManagerRepository.getEntityManager();
        }

        @SuppressWarnings("rawtypes")
        void runTask() {

            final PickAndRunTask pickAndRun = this;
            /* Bruker SQL+JDBC for å kunne benytte savepoints og inkrementell oppdatering i transaksjonen. */
            class PullSingleTask implements Work {
                @Override
                public void execute(Connection conn) throws SQLException {
                    try {
                        Optional<ProsessTaskEntitet> pte = taskManagerRepository.finnOgLås(taskInfo);
                        if (pte.isPresent()) {
                            runTaskAndUpdateStatus(conn, pte.get(), pickAndRun);
                        }
                    } catch (JDBCConnectionException
                            | SQLTransientException
                            | SQLNonTransientConnectionException
                            | SQLRecoverableException e) {

                        // vil kun logges
                        pickAndRun.getFeilOgStatushåndterer().handleTransientAndRecoverableException(e);
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
