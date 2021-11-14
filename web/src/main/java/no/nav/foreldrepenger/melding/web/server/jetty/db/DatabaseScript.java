package no.nav.foreldrepenger.melding.web.server.jetty.db;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

public class DatabaseScript {

    private static final String LOCATION = "classpath:/db/migration/";

    public static void migrate(final DataSource dataSource, String initSql, boolean clean) {
        var flywayKonfig = Flyway.configure()
                .dataSource(dataSource)
                .locations(LOCATION)
                .baselineOnMigrate(true);
        if (initSql != null) {
            flywayKonfig.initSql(initSql);
        }
        var flyway = flywayKonfig.load();
        try {
            flyway.migrate();
        } catch (FlywayException fwe) {
            if (clean) {
                flyway.clean();
                flyway.migrate();
            } else {
                throw new IllegalStateException("Migrering feiler", fwe);
            }
        }
    }
}
