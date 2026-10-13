package no.nav.foreldrepenger.fpformidling.database;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vedtak.felles.jpa.NamingStandard;
import no.nav.vedtak.felles.jpa.jdbc.DataSourceHolder;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;
import no.nav.vedtak.felles.testutilities.db.MigrationUtil;

public class JpaExtension extends EntityManagerAwareExtension {

    private static final Environment ENV = Environment.current();
    private static final String TEST_DB_CONTAINER = Environment.current()
        .getProperty("testcontainer.test.db", String.class, "postgres:18-alpine");

    static {
        initDatabase();
    }

    private static synchronized void initDatabase() {
        if (!ENV.isLocal()) {
            throw new IllegalStateException("Forventer at denne migreringen bare kjøres lokalt");
        }
        if (!DataSourceHolder.isInitialized()) {
            var testDatabase = new PostgreSQLContainer(DockerImageName.parse(TEST_DB_CONTAINER)).withReuse(true);
            testDatabase.start();
            var dataSource = MigrationUtil.createLocalBuildTestDataSource(testDatabase.getJdbcUrl(), testDatabase.getUsername(), testDatabase.getPassword());
            MigrationUtil.migrateLocalBuildTest(dataSource, NamingStandard.DEFAULT_DS_MIGRATION_CLASSPATH);
            DataSourceHolder.initialize(dataSource);
        }
    }
}
