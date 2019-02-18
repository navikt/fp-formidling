package no.nav.foreldrepenger.melding.dbstoette;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.lokal.dbstoette.DBConnectionProperties;
import no.nav.vedtak.felles.lokal.dbstoette.DatabaseStøtte;

/**
 * Initielt skjemaoppsett + migrering av unittest-skjemaer
 */
public final class Databaseskjemainitialisering {

    private static final Logger log = LoggerFactory.getLogger(Databaseskjemainitialisering.class);
    private static final Pattern placeholderPattern = Pattern.compile("\\$\\{(.*)\\}");

    private static final AtomicBoolean GUARD_SKJEMAER = new AtomicBoolean();
    private static final AtomicBoolean GUARD_UNIT_TEST_SKJEMAER = new AtomicBoolean();

    public static void main(String[] args) {
        migrerUnittestSkjemaer();
    }

    public static void settOppSkjemaer() {
        if (GUARD_SKJEMAER.compareAndSet(false, true)) {
            try {
                settSchemaPlaceholder(DatasourceConfiguration.UNIT_TEST.getRaw());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void migrerUnittestSkjemaer() {
        settOppSkjemaer();

        if (GUARD_UNIT_TEST_SKJEMAER.compareAndSet(false, true)) {
            try {
                DatabaseStøtte.kjørMigreringFor(DatasourceConfiguration.UNIT_TEST.get());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void settPlaceholdereOgJdniOppslag() {
        try {
            Databaseskjemainitialisering.settSchemaPlaceholder(DatasourceConfiguration.UNIT_TEST.getRaw());
            DatabaseStøtte.settOppJndiForDefaultDataSource(DatasourceConfiguration.UNIT_TEST.get());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void settSchemaPlaceholder(List<DBConnectionProperties> connectionProperties) throws FileNotFoundException {
        for (DBConnectionProperties dbcp : connectionProperties) {
            Matcher matcher = placeholderPattern.matcher(dbcp.getSchema());
            if (matcher.matches()) {
                String placeholder = matcher.group(1);
                if (System.getProperty(placeholder) == null) {
                    System.setProperty(placeholder, dbcp.getDefaultSchema());
                }
            }
        }
    }
}
