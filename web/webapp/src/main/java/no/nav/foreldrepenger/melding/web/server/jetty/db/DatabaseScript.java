package no.nav.foreldrepenger.melding.web.server.jetty.db;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.ClassicConfiguration;

public class DatabaseScript {

    private static final String LOCATION = "classpath:/db/migration/";

    public static void migrate(final DataSource dataSource, String initSql, boolean clean) {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(dataSource);
        conf.setLocationsAsStrings(LOCATION);
        conf.setBaselineOnMigrate(true);
        if (initSql != null) {
            conf.setInitSql(initSql);
        }
        Flyway flyway = new Flyway(conf);
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
