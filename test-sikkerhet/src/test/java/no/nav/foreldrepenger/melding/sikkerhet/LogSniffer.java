package no.nav.foreldrepenger.melding.sikkerhet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.LogManager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Testutility for å verifiser at meldinger logges, med mulighet å hindre at texten kommer i logfilen
 */
public class LogSniffer implements TestRule {

    static {
        initLogSystem();
    }

    private final Map<ILoggingEvent, Boolean> logbackAppender = new LinkedHashMap<>();
    private final Level minimumLevel;
    private final boolean denyOthersWhenMatched;

    public LogSniffer() {
        this(Level.INFO);
    }

    public LogSniffer(Level minimumLevel) {
        this(minimumLevel, true);
    }

    public LogSniffer(Level minimumLevel, boolean denyOthersWhenMatched) {
        this.minimumLevel = minimumLevel;
        this.denyOthersWhenMatched = denyOthersWhenMatched;
        installTurboFilter(getLoggerContext());
    }

    private static void initLogSystem() {
        // java.util.logging demands a little extra for SLF4J to pick it up
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("global").setLevel(java.util.logging.Level.FINEST);
    }

    private LoggerContext getLoggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    private void installTurboFilter(LoggerContext lc) {
        lc.addTurboFilter(new TurboFilter() {

            @Override
            public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] argArray, Throwable t) {
                if (format != null && level != null && level.isGreaterOrEqual(minimumLevel)) {
                    LoggingEvent loggingEvent = new LoggingEvent(Logger.FQCN, logger, level, format, t, argArray);
                    logbackAppender.put(loggingEvent, Boolean.FALSE);
                    return denyOthersWhenMatched ? FilterReply.DENY : FilterReply.NEUTRAL;
                }
                return FilterReply.NEUTRAL;
            }
        });
    }

    public void assertHasErrorMessage(String substring) {
        if (!hasLogEntry(substring, null, Level.ERROR)) {
            throw new AssertionError(String.format("Could not find log message matching [%s], with level ERROR.  Has [%s]", substring, this));
        }
        markEntryAsserted(substring, null, Level.ERROR);
    }

    public void assertHasErrorMessage(String substring, Class<? extends Throwable> t) {
        if (!hasLogEntry(substring, t, Level.ERROR)) {
            throw new AssertionError(String.format("Could not find log message matching [%s], for exception [%s], with level ERROR.  Has [%s]", substring, t, this));
        }
        markEntryAsserted(substring, t, Level.ERROR);
    }

    public void assertHasWarnMessage(String substring) {
        if (!hasLogEntry(substring, null, Level.WARN)) {
            throw new AssertionError(String.format("Could not find log message matching [%s], with level WARN.  Has [%s]", substring, this));
        }
        markEntryAsserted(substring, null, Level.WARN);
    }

    public void assertHasWarnMessage(String substring, Class<? extends Throwable> t) {
        if (!hasLogEntry(substring, t, Level.WARN)) {
            throw new AssertionError(String.format("Could not find log message matching [%s], for exception [%s], with level WARN.  Has [%s]", substring, t, this));
        }
        markEntryAsserted(substring, t, Level.WARN);
    }

    public void assertHasInfoMessage(String substring) {
        if (!hasLogEntry(substring, null, Level.INFO)) {
            throw new AssertionError(String.format("Could not find log message matching [%s], with level INFO.  Has [%s]", substring, this));
        }
        markEntryAsserted(substring, null, Level.INFO);
    }

    public void assertNoErrors() {
        if (countErrors() > 0) {
            throw new AssertionError("No errors expected, but has " + this);
        }
    }

    public void assertNoWarnings() {
        if (countWarnings() > 0) {
            throw new AssertionError("No warnings expected, but has " + this);
        }
    }

    public void assertNoErrorsOrWarnings() {
        assertNoErrors();
        assertNoWarnings();
    }

    public void assertNoLogEntries() {
        if (!logbackAppender.isEmpty()) {
            throw new AssertionError("No log entries expected, but has " + this);
        }
    }

    private void markEntryAsserted(String substring, Class<? extends Throwable> t, Level level) {
        for (ILoggingEvent loggingEvent : new ArrayList<>(logbackAppender.keySet())) {
            if (eventMatches(loggingEvent, substring, t, level)) {
                logbackAppender.put(loggingEvent, Boolean.TRUE);
            }
        }
    }

    private boolean eventMatches(ILoggingEvent loggingEvent, String substring, Class<? extends Throwable> t, Level level) {
        if (substring != null && !loggingEvent.getFormattedMessage().contains(substring)) {
            return false;
        }
        if (t != null
                && (loggingEvent.getThrowableProxy() == null
                || loggingEvent.getThrowableProxy().getClassName() == null
                || !loggingEvent.getThrowableProxy().getClassName().equals(t.getName()))) {
            return false;
        }
        return level == null || (level == loggingEvent.getLevel());
    }

    private int countErrors() {
        int logErrors = 0;
        for (Map.Entry<ILoggingEvent, Boolean> entry : logbackAppender.entrySet()) {
            if (entry.getKey().getLevel().equals(Level.ERROR) && entry.getValue() != Boolean.TRUE) {
                logErrors++;
            }
        }
        return logErrors;
    }

    private int countWarnings() {
        int logWarnings = 0;
        for (Map.Entry<ILoggingEvent, Boolean> entry : logbackAppender.entrySet()) {
            if (entry.getKey().getLevel().equals(Level.WARN) && entry.getValue() != Boolean.TRUE) {
                logWarnings++;
            }
        }
        return logWarnings;
    }

    public int countEntries(String substring) {
        return countLogbackEntries(substring, null, null);
    }

    private boolean hasLogEntry(String substring, Class<? extends Throwable> t, Level level) {
        return countLogbackEntries(substring, t, level) > 0;
    }

    private int countLogbackEntries(String substring, Class<? extends Throwable> t, Level level) {
        int count = 0;
        for (ILoggingEvent loggingEvent : logbackAppender.keySet()) {
            if (eventMatches(loggingEvent, substring, t, level)) {
                count++;
            }
        }
        return count;
    }

    public void clearLog() {
        this.logbackAppender.clear();
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            // throws Throwable is necessary as the base class is defined that way...
            // CHECKSTYLE:OFF
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                    assertNoErrorsOrWarnings();
                } finally {
                    getLoggerContext().resetTurboFilterList();
                }
            }
            // CHECKSTYLE:ON
        };
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (ILoggingEvent event : logbackAppender.keySet()) {
            buf.append(event.getLevel() + ":" + event.getFormattedMessage()).append('\n');
        }
        return buf.toString();
    }

}
