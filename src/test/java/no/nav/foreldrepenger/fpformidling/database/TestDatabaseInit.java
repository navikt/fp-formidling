package no.nav.foreldrepenger.fpformidling.database;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Initielt skjemaoppsett + migrering av unittest-skjemaer
 */
public final class TestDatabaseInit {
    private static final AtomicBoolean GUARD_UNIT_TEST_SKJEMAER = new AtomicBoolean();
    private static final String DB_SCRIPT_LOCATION = "/db/migration/defaultDS/";

    public static void settOppDatasourceOgMigrer(String jdbcUrl, String username, String password) {
        var ds = createDatasource(jdbcUrl, username, password);
        settJdniOppslag(ds);
        if (GUARD_UNIT_TEST_SKJEMAER.compareAndSet(false, true)) {
            var flyway = Flyway.configure()
                    .dataSource(ds)
                    .locations(getScriptLocation())
                    .baselineOnMigrate(true)
                    .cleanDisabled(false)
                    .load();
            try {
                flyway.migrate();
            } catch (FlywayException fwe) {
                try {
                    // prøver igjen
                    flyway.clean();
                    flyway.migrate();
                } catch (FlywayException fwe2) {
                    throw new IllegalStateException("Migrering feiler", fwe2);
                }
            }
        }
    }

    private static String getScriptLocation() {
        return fileScriptLocation();
    }

    private static String fileScriptLocation() {
        var relativePath = "src/main/resources" + DB_SCRIPT_LOCATION;
        var baseDir = new File(".").getAbsoluteFile();
        var location = new File(baseDir, relativePath);
        while (!location.exists()) {
            baseDir = baseDir.getParentFile();
            if (baseDir == null || !baseDir.isDirectory()) {
                throw new IllegalArgumentException("Klarte ikke finne : " + baseDir);
            }
            location = new File(baseDir, relativePath);
        }
        return "filesystem:" + location.getPath();
    }

    private static void settJdniOppslag(DataSource dataSource) {
        try {
            new EnvEntry("jdbc/defaultDS", dataSource); // NOSONAR
        } catch (NamingException e) {
            throw new IllegalStateException("Feil under registrering av JDNI-entry for default datasource", e); // NOSONAR
        }
    }

    private static HikariDataSource createDatasource(String jdbcUrl, String username, String password) {
        var cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(username);
        cfg.setPassword(password);
        cfg.setMaximumPoolSize(4);
        cfg.setConnectionTimeout(TimeUnit.SECONDS.toMillis(2)); // feiler fast hvis db ikke kjører lokalt.
        cfg.setAutoCommit(false);
        var ds = new HikariDataSource(cfg);
        Runtime.getRuntime().addShutdownHook(new Thread(ds::close));
        return ds;
    }
}
