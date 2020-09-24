package no.nav.foreldrepenger.melding.web.app.startupinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.jboss.resteasy.annotations.Query;
import org.jboss.weld.util.reflection.Formats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

import no.nav.foreldrepenger.melding.web.app.selftest.SelftestResultat;
import no.nav.foreldrepenger.melding.web.app.selftest.Selftests;
import no.nav.foreldrepenger.melding.web.app.selftest.checks.ExtHealthCheck;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.log.util.LoggerUtils;

/**
 * Dependent scope siden vi lukker denne når vi er ferdig.
 */
@Dependent
class AppStartupInfoLogger {

    private static final Logger logger = LoggerFactory.getLogger(AppStartupInfoLogger.class);
    private static final String OPPSTARTSINFO = "OPPSTARTSINFO";
    private static final String HILITE_SLUTT = "********";
    private static final String HILITE_START = HILITE_SLUTT;
    private static final String SELFTEST = "Selftest";
    private static final String APPLIKASJONENS_STATUS = "Applikasjonens status";
    private static final String START = "start:";
    private static final String SLUTT = "slutt.";
    private Selftests selftests;
    /**
     * Samler opp all logging og outputter til slutt.
     */
    private List<Runnable> logStatements = new ArrayList<>();

    AppStartupInfoLogger() {
        // for CDI proxy
    }

    @Inject
    AppStartupInfoLogger(Selftests selftests) {
        this.selftests = selftests;
    }

    void logAppStartupInfo() {
        log(HILITE_START + " " + OPPSTARTSINFO + " " + START + " " + HILITE_SLUTT);
        logSelftest();
        log(HILITE_START + " " + OPPSTARTSINFO + " " + SLUTT + " " + HILITE_SLUTT);

        writeLog();
    }

    private void writeLog() {
        logStatements.forEach(r -> r.run());
    }

    private void logSelftest() {
        log(SELFTEST + " " + START);

        // callId er påkrevd på utgående kall og må settes før selftest kjøres
        MDCOperations.putCallId();
        SelftestResultat samletResultat = selftests.run();
        MDCOperations.removeCallId();

        for (HealthCheck.Result result : samletResultat.getAlleResultater()) {
            log(result);
        }

        log(APPLIKASJONENS_STATUS + ": {}", samletResultat.getAggregateResult());

        log(SELFTEST + " " + SLUTT);
    }

    private void log(String msg, Object... args) {
        if (args == null || args.length == 0) {
            // skiller ut ellers logger logback ekstra paranteser og fnutter for tomme args
            logStatements.add(() -> logger.info(msg));
        } else {
            logStatements.add(() -> logger.info(msg, args));
        }
    }

    private void log(HealthCheck.Result result) {
        Feil feil;
        if (result.getDetails() != null) {
            feil = OppstartFeil.FACTORY.selftestStatus(
                    getStatus(result.isHealthy()),
                    (String) result.getDetails().get(ExtHealthCheck.DETAIL_DESCRIPTION),
                    (String) result.getDetails().get(ExtHealthCheck.DETAIL_ENDPOINT),
                    (String) result.getDetails().get(ExtHealthCheck.DETAIL_RESPONSE_TIME),
                    result.getMessage());
        } else {
            feil = OppstartFeil.FACTORY.selftestStatus(
                    getStatus(result.isHealthy()),
                    null,
                    null,
                    null,
                    result.getMessage());
        }

        logStatements.add(() -> feil.log(logger));
    }

    private String getStatus(boolean isHealthy) {
        return isHealthy ? "OK" : "ERROR";
    }
}
