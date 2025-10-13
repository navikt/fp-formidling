package no.nav.foreldrepenger.fpformidling.database;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class JpaExtension extends EntityManagerAwareExtension {

    private static final String TEST_DB_CONTAINER = Environment.current().getProperty("testcontainer.test.db", String.class, "postgres:18-alpine");
    private static final PostgreSQLContainer TEST_DATABASE;
    static {
        TEST_DATABASE = new PostgreSQLContainer<>(DockerImageName.parse(TEST_DB_CONTAINER))
            .withReuse(true);
        TEST_DATABASE.start();
        TestDatabaseInit.settOppDatasourceOgMigrer(TEST_DATABASE.getJdbcUrl(), TEST_DATABASE.getUsername(), TEST_DATABASE.getPassword());
    }
}
