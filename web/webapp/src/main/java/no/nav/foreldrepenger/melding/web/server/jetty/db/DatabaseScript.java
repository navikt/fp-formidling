package no.nav.foreldrepenger.melding.web.server.jetty.db;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.ClassicConfiguration;

public class DatabaseScript {

    private final MigrationDataSource migrationDataSource;
    private final String locations;
    private final static boolean SKAL_CLEANE_DATABASEN = false;

    public DatabaseScript(MigrationDataSource migrationDataSource, String locations) {
        this.migrationDataSource = migrationDataSource;
        this.locations = locations;
    }

    public void migrate() {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(migrationDataSource.dataSource);
        conf.setLocationsAsStrings(locations);
        conf.setBaselineOnMigrate(true);
        if (migrationDataSource.initSql != null) {
            conf.setInitSql(migrationDataSource.initSql);
        }
        if (migrationDataSource.schema != null) {
            conf.setSchemas(migrationDataSource.schema);
        }
        Flyway flyway = new Flyway(conf);
        try {
            flyway.migrate();
        } catch (FlywayException fwe) {
            if (SKAL_CLEANE_DATABASEN) {
                flyway.clean();
                flyway.migrate();
            }
        }
    }

    static class MigrationDataSource {
        private final DataSource dataSource;
        private final String initSql;
        private final String schema;

        MigrationDataSource(DataSource dataSource, String initSql) {
            this(dataSource, initSql, null);
        }

        MigrationDataSource(DataSource dataSource, String initSql, String schema) {
            this.schema = schema;
            this.dataSource = dataSource;
            this.initSql = initSql;
        }

    }
}
