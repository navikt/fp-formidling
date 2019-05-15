package no.nav.foreldrepenger.melding.web.server.jetty.db;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.ClassicConfiguration;

public class DatabaseScript {

    private static final String location = "classpath:/db/migration/";
    private static final boolean SKAL_CLEANE_DATABASEN = true;

    public static void migrate(final DataSource dataSource, String initSql) {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(dataSource);
        conf.setLocationsAsStrings(location);
        conf.setBaselineOnMigrate(true);
        if (initSql != null) {
            conf.setInitSql(initSql);
        }
        Flyway flyway = new Flyway(conf);
        try {
            flyway.migrate();
        } catch (FlywayException fwe) {
            if (SKAL_CLEANE_DATABASEN) {
                flyway.clean();
                flyway.migrate();
            } else {
                throw new IllegalStateException("Migrering feiler", fwe);
            }
        }
    }
}
