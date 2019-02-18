package no.nav.foreldrepenger.melding.feed.poller;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.feed.RequestContextHandler;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.felles.jpa.TransactionHandler;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;


public class Poller implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Poller.class);
    private static final int[] BACKOFF_INTERVALL_IN_SEC = new int[]{1, 2, 5, 5, 10, 10, 10, 10, 30};
    private final AtomicInteger backoffRound = new AtomicInteger();
    private FeedPoller feedPoller;
    private EntityManager entityManager;

    Poller(@VLPersistenceUnit EntityManager entityManager, FeedPoller feedPoller) {
        this.entityManager = entityManager;
        this.feedPoller = feedPoller;
    }

    Poller() {
    }


    @Override
    public void run() {
        try {
            RequestContextHandler.doWithRequestContext(this::doPollingWithEntityManager);
        } catch (Exception e) {
            logger.error("Uventet feil", e);
        }
    }

    private Void doPollingWithEntityManager() {
        try {
            if (backoffRound.get() > 0) {
                Thread.sleep(BACKOFF_INTERVALL_IN_SEC[Math.min(backoffRound.get(), BACKOFF_INTERVALL_IN_SEC.length) - 1] * 1000L);
            }
            new PollInNewTransaction().doWork();

            backoffRound.set(0);

            return null;
        } catch (Exception e) { // NOSONAR
            backoffRound.incrementAndGet();
            Feilene.FACTORY.kunneIkkePolleDatabase(backoffRound.get(), e.getClass(), e.getMessage(), e)
                    .log(logger);
        }
        return null;
    }


    interface Feilene extends DeklarerteFeil {
        Feilene FACTORY = FeilFactory.create(Feilene.class);

        @TekniskFeil(feilkode = "FPMELDING-862", feilmelding = "Kunne ikke polle database, venter til neste runde(runde=%s): %s: %s", logLevel = LogLevel.WARN)
        Feil kunneIkkePolleDatabase(Integer round, Class<?> exceptionClass, String exceptionMessage, Exception cause);
    }

    private final class PollInNewTransaction extends TransactionHandler<Void> {

        Void doWork() throws Exception {
            try {
                return super.apply(entityManager);
            } finally {
                CDI.current().destroy(entityManager);
            }
        }

        private Void doPoll() {
            try {
                feedPoller.poll();
                return null;
            } finally {
                CDI.current().destroy(entityManager);
            }
        }

        @Override
        protected Void doWork(EntityManager entityManager) {
            return doPoll();
        }
    }
}
