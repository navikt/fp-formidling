package no.nav.vedtak.felles.prosesstask.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.apptjeneste.AppServiceHandler;
import no.nav.vedtak.felles.jpa.TransactionHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskDispatcher;

/**
 * Main class handling polling tasks and dispatching these.
 */
@ApplicationScoped
public class TaskManager implements AppServiceHandler {

    public static final String TASK_MANAGER_POLLING_WAIT = "task.manager.polling.wait";
    public static final String TASK_MANAGER_POLLING_DELAY = "task.manager.polling.delay";
    public static final String TASK_MANAGER_POLLING_TASKS_SIZE = "task.manager.polling.tasks.size";
    public static final String TASK_MANAGER_RUNNER_THREADS = "task.manager.runner.threads";

    private static final Logger log = LoggerFactory.getLogger(TaskManager.class);
    /**
     * Prefix every thread in pool with given name.
     */
    private final String threadPoolNamePrefix = getClass().getSimpleName();
    private final AtomicLong pollerRoundNoCapacityRounds = new AtomicLong();
    private final AtomicReference<LocalDateTime> pollerRoundNoCapacitySince = new AtomicReference<>(LocalDateTime.now());
    private final AtomicReference<LocalDateTime> pollerRoundNoneFoundSince = new AtomicReference<>(LocalDateTime.now());
    private final AtomicReference<LocalDateTime> pollerRoundNoneLastReported = new AtomicReference<>(LocalDateTime.now());
    private TaskManagerRepositoryImpl taskManagerRepository;
    /**
     * Antall parallelle tråder for å plukke tasks.
     */
    private int numberOfTaskRunnerThreads = getSystemPropertyWithLowerBoundry(TASK_MANAGER_RUNNER_THREADS, 5, 0);
    /**
     * Delay between each interval of polling. (millis)
     */
    private long delayBetweenPollingMillis = getSystemPropertyWithLowerBoundry(TASK_MANAGER_POLLING_DELAY, 500L, 1L);
    /**
     * Max number of tasks that will be attempted to poll on every try.
     */
    private int maxNumberOfTasksToPoll = getSystemPropertyWithLowerBoundry(TASK_MANAGER_POLLING_TASKS_SIZE, numberOfTaskRunnerThreads * 2, 1);
    /**
     * Ventetid før neste polling forsøk (antar dersom task ikke plukkes raskt nok, kan en annen poller ta over).
     * (sekunder)
     */
    private long waitTimeBeforeNextPollingAttemptSecs = getSystemPropertyWithLowerBoundry(TASK_MANAGER_POLLING_WAIT, 30L, 1L);
    /**
     * Executor for å håndtere tråder for å kjøre tasks.
     */
    private IdentExecutorService runTaskService;
    /**
     * Single scheduled thread handling polling.
     */
    private ScheduledExecutorService pollingService;
    /**
     * Future for å kunne kansellere polling.
     */
    private ScheduledFuture<?> pollingServiceScheduledFuture;
    /**
     * Implementasjon av dispatcher for å faktisk kjøre tasks.
     */
    private ProsessTaskDispatcher taskDispatcher;

    public TaskManager() {
    }

    @Inject
    public TaskManager(TaskManagerRepositoryImpl taskManagerRepository) {
        Objects.requireNonNull(taskManagerRepository, "taskManagerRepository"); //$NON-NLS-1$
        this.taskManagerRepository = taskManagerRepository;
    }

    private static int getSystemPropertyWithLowerBoundry(String key, int defaultValue, int lowerBoundry) {
        final String property = System.getProperty(key, String.valueOf(defaultValue));
        final int systemPropertyValue = Integer.parseInt(property);
        if (systemPropertyValue < lowerBoundry) {
            return lowerBoundry;
        }
        return systemPropertyValue;
    }

    private static long getSystemPropertyWithLowerBoundry(String key, long defaultValue, long lowerBoundry) {
        final String property = System.getProperty(key, String.valueOf(defaultValue));
        final long systemPropertyValue = Long.parseLong(property);
        if (systemPropertyValue < lowerBoundry) {
            return lowerBoundry;
        }
        return systemPropertyValue;
    }

    @Inject
    public synchronized void setProsessTaskDispatcher(ProsessTaskDispatcher taskDispatcher) {
        Objects.requireNonNull(taskDispatcher, "taskDispatcher"); //$NON-NLS-1$
        this.taskDispatcher = taskDispatcher;
    }

    synchronized ProsessTaskDispatcher getTaskDispatcher() {
        return this.taskDispatcher;
    }

    public synchronized void setDelayBetweenPolling(long delayBetweenPolling) {
        if (delayBetweenPolling < 0) {
            throw new IllegalArgumentException("delayBetweenPolling < 0" + delayBetweenPolling);
        }
        this.delayBetweenPollingMillis = delayBetweenPolling;
    }

    public synchronized void configureTaskThreads(int numberOfTaskRunnerThreads, int maxNumberOfTasksToPoll) {
        this.numberOfTaskRunnerThreads = numberOfTaskRunnerThreads;

        if (maxNumberOfTasksToPoll <= 0) {
            throw TaskManagerFeil.FACTORY.ugyldigAntallTasks().toException();
        }
        this.maxNumberOfTasksToPoll = maxNumberOfTasksToPoll;
    }

    @Override
    public synchronized void start() {
        if (this.numberOfTaskRunnerThreads > 0) {
            startTaskThreads();
            startPollerThread();
        } else {
            log.info("Kan ikke starte {}, ingen tråder konfigurert, sjekk property [{}]", getClass().getSimpleName(), TASK_MANAGER_RUNNER_THREADS);
        }
    }

    @Override
    public synchronized void stop() {
        if (pollingServiceScheduledFuture != null) {
            pollingServiceScheduledFuture.cancel(true);
            pollingServiceScheduledFuture = null;
        }
        if (runTaskService != null) {
            runTaskService.stop();
            runTaskService = null;
        }
    }

    synchronized void startPollerThread() {
        if (pollingServiceScheduledFuture != null) {
            throw new IllegalStateException("Service allerede startet, stopp først");//$NON-NLS-1$
        }
        if (pollingService == null) {
            this.pollingService = Executors
                    .newSingleThreadScheduledExecutor(new Utils.NamedThreadFactory(threadPoolNamePrefix + "-poller", false)); //$NON-NLS-1$
        }
        this.pollingServiceScheduledFuture = pollingService.scheduleWithFixedDelay(new PollAvailableTasks(), delayBetweenPollingMillis / 2,
                delayBetweenPollingMillis, TimeUnit.MILLISECONDS); // NOSONAR
    }

    synchronized void startTaskThreads() {
        if (runTaskService != null) {
            throw new IllegalStateException("Service allerede startet, stopp først");//$NON-NLS-1$
        }
        this.runTaskService = new IdentExecutorService();
    }

    /**
     * Poller for tasks og logger jevnlig om det ikke er ledig kapasitet (i in-memory queue) eller ingen tasks funnet (i db).
     */
    protected synchronized List<IdentRunnable> pollForAvailableTasks() {
        LocalDateTime now = LocalDateTime.now();

        int capacity = getRunTaskService().remainingCapacity();
        if (reportRegularlyAndSkipIfNoAvailableCapacity(now, capacity)) {
            return Collections.emptyList();
        }

        int numberOfTasksToPoll = Math.min(capacity, maxNumberOfTasksToPoll);
        TaskManagerGenerateRunnableTasks pollDatabaseToRunnable = new TaskManagerGenerateRunnableTasks(getTaskDispatcher(), this::pollTasksFunksjon,
                this::submitTask);
        List<IdentRunnable> tasksFound = pollDatabaseToRunnable.execute(numberOfTasksToPoll);

        reportRegularlyIfNoTasksFound(now, tasksFound);
        return tasksFound;

    }

    private void reportRegularlyIfNoTasksFound(LocalDateTime now, List<IdentRunnable> tasksFound) {
        if (tasksFound.isEmpty()) {
            if (pollerRoundNoneLastReported.get().plusHours(1).isBefore(now)) {
                pollerRoundNoneLastReported.set(now);
                log.info("Ingen tasks funnet siden [{}].", pollerRoundNoneFoundSince.get());
            }
        } else {
            pollerRoundNoneFoundSince.set(now); // reset
            pollerRoundNoneLastReported.set(now); // reset
        }
    }

    private boolean reportRegularlyAndSkipIfNoAvailableCapacity(LocalDateTime now, int capacity) {
        if (capacity < 1) {
            long round = pollerRoundNoCapacityRounds.incrementAndGet();
            if (round % 60 == 0) {
                log.info(
                        "Ingen ledig kapasitet i siste polling runder siden [{}].  Sjekk eventuelt om tasks blir kjørt eller om de henger/er treghet under kjøring.",
                        pollerRoundNoCapacitySince.get());
            }
            // internal work queue already full, no point trying to push more
            return true;
        } else {
            pollerRoundNoCapacityRounds.set(0L); // reset
            pollerRoundNoCapacitySince.set(now); // reset
            return false;
        }
    }

    List<IdentRunnable> pollTasksFunksjon(int numberOfTasksToPoll, ReadTaskFunksjon readTaskFunksjon) {
        int numberOfTasksStillToGo = numberOfTasksToPoll;

        Set<Long> inmemoryTaskIds = getRunTaskService().getTaskIds();
        List<ProsessTaskEntitet> tasksEntiteter = taskManagerRepository
                .pollNesteScrollingUpdate(numberOfTasksStillToGo, waitTimeBeforeNextPollingAttemptSecs, inmemoryTaskIds);

        return tasksEntiteter.stream().map(readTaskFunksjon).collect(Collectors.toList());
    }

    synchronized TaskManagerRepositoryImpl getTransactionManagerRepository() {
        return taskManagerRepository;
    }

    Future<Boolean> submitTask(IdentRunnable task) {
        return getRunTaskService().submit(task);
    }

    /**
     * For testing. Kjørere synkront med kallende tråd
     */
    int doSinglePolling() {
        return new PollAvailableTasks().call();
    }

    /**
     * Kjører en polling runde (async)
     */
    void doSinglePollingAsync() {
        if (pollingService != null) {
            pollingService.submit(new PollAvailableTasks(), Boolean.TRUE);
        } // else - ignoreres hvis ikke startet
    }

    /**
     * For testing.
     */
    synchronized IdentExecutorService getRunTaskService() {
        return runTaskService;
    }

    interface ReadTaskFunksjon extends Function<ProsessTaskEntitet, IdentRunnable> {
    }

    interface Work<R> {
        R doWork(EntityManager em) throws Exception; // NOSONAR
    }

    static final class IdentFutureTask<T> extends FutureTask<T> implements IdentRunnable {

        private final Long id;

        IdentFutureTask(IdentRunnable runnable, T value) {
            super(runnable, value);
            this.id = runnable.getId();
        }

        @Override
        public Long getId() {
            return id;
        }
    }

    /**
     * Poll database table for tasks to run. Handled in a single thread.
     */
    protected class PollAvailableTasks implements Callable<Integer>, Runnable {

        /**
         * simple backoff interval in seconds per round to account for transient database errors.
         */
        private final int[] backoffInterval = new int[]{1, 2, 5, 5, 10, 10, 10, 10, 30};
        private final AtomicInteger backoffRound = new AtomicInteger();

        /**
         * @return number of tasks polled, -1 if errors logged
         */
        @Override
        public Integer call() {
            return RequestContextHandler.doWithRequestContext(this::doPollingWithEntityManager);
        }

        public Integer doPollingWithEntityManager() {
            try {
                if (backoffRound.get() > 0) {
                    Thread.sleep(getBackoffIntervalSeconds());
                }
                List<IdentRunnable> availableTasks = new PollInNewTransaction().doWork();

                // dispatch etter commit
                dispatchTasks(availableTasks);

                backoffRound.set(0);

                return availableTasks.size();
            } catch (InterruptedException e) {
                backoffRound.incrementAndGet();
                Thread.currentThread().interrupt();
            } catch (JDBCConnectionException e) { // NOSONAR
                backoffRound.incrementAndGet();
                TaskManagerFeil.FACTORY.midlertidigUtilgjengeligDatabase(backoffRound.get(), e.getClass(), e.getMessage())
                        .log(log);
            } catch (Exception e) { // NOSONAR
                backoffRound.set(backoffInterval.length - 1); // force max delay (skal kun havne her for Exception/RuntimeException)
                TaskManagerFeil.FACTORY.kunneIkkePolleDatabase(backoffRound.get(), e).log(log);
            } catch (Throwable t) { // NOSONAR
                backoffRound.set(backoffInterval.length - 1); // force max delay (skal kun havne her for Error)
                TaskManagerFeil.FACTORY.kritiskKunneIkkePolleDatabase(getBackoffIntervalSeconds(), t).log(log);
            }

            return -1;
        }

        private long getBackoffIntervalSeconds() {
            return backoffInterval[Math.min(backoffRound.get(), backoffInterval.length) - 1] * 1000L;
        }

        @Override
        public void run() {
            try {
                call();
            } catch (Throwable fatal) {
                /**
                 * skal aldri komme hit, men fange og håndtere exception før
                 *
                 * @see ScheduledExecutorService#scheduleWithFixedDelay
                 */
                log.error("Polling fatal feil, logger exception men tråden vil bli drept", fatal);
                throw fatal;
            }
        }

        private void dispatchTasks(List<IdentRunnable> availableTasks) {
            for (IdentRunnable task : availableTasks) {
                @SuppressWarnings("unused")
                Future<?> future = submitTask(task); // NOSONAR
                // lar futures ligge, feil fanges i task
            }
        }

        private final class PollInNewTransaction extends TransactionHandler<List<IdentRunnable>> {

            List<IdentRunnable> doWork() throws Exception {

                EntityManager entityManager = getTransactionManagerRepository().getEntityManager();
                try {
                    return super.apply(entityManager);
                } finally {
                    CDI.current().destroy(entityManager);
                }
            }

            @Override
            protected List<IdentRunnable> doWork(EntityManager entityManager) {
                return pollForAvailableTasks();
            }
        }

    }

    /**
     * Internal executor that also tracks ids of currently queue or running tasks.
     */
    class IdentExecutorService {

        private final ThreadPoolExecutor executor;

        IdentExecutorService() {
            executor = new ThreadPoolExecutor(numberOfTaskRunnerThreads, numberOfTaskRunnerThreads, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(maxNumberOfTasksToPoll),
                    new Utils.NamedThreadFactory(threadPoolNamePrefix + "-runtask", true)) { //$NON-NLS-1$

                @Override
                protected void afterExecute(Runnable r, Throwable t) {
                    if (getQueue().isEmpty()) {
                        // gi oss selv en head start ifht. neste polling runde
                        doSinglePollingAsync();
                    }
                }

                @Override
                protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                    throw new UnsupportedOperationException("Alle kall skal gå til andre #newTaskFor(Runnable, T value)");
                }

                @Override
                protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
                    return new IdentFutureTask<T>((IdentRunnable) runnable, value);
                }

            };
        }

        int remainingCapacity() {
            return executor.getQueue().remainingCapacity();
        }

        Future<Boolean> submit(IdentRunnable command) {
            return executor.submit(command, Boolean.TRUE);
        }

        Set<Long> getTaskIds() {
            return executor.getQueue().stream()
                    .map(IdentFutureTask.class::cast)
                    .map(IdentFutureTask::getId)
                    .collect(Collectors.toSet());
        }

        void stop() {
            executor.shutdownNow();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
