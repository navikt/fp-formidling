package no.nav.foreldrepenger.melding.web.server.jetty.db;

import static no.nav.foreldrepenger.konfig.Cluster.LOCAL;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.konfig.Cluster;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import no.nav.vault.jdbc.hikaricp.VaultError;

public class DatasourceUtil {
    private static final Environment ENV = Environment.current();

    private static final String VAULT_PREPROD_NAVN = "preprod-fss";

    public static DataSource createDatasource(String datasourceName, DatasourceRole role, Cluster cluster, int maxPoolSize) {
        String rolePrefix = getRolePrefix(datasourceName);
        HikariConfig config = initConnectionPoolConfig(datasourceName, maxPoolSize);
        if (LOCAL.equals(cluster)) {
            return createLocalDatasource(config, "public", rolePrefix,
                    ENV.getProperty(datasourceName + ".password"));
        }
        return createVaultDatasource(config, mountPath(cluster), getRole(rolePrefix, role));
    }

    private static String mountPath(Cluster cluster) {
        return "postgresql/" + (cluster.isProd() ? cluster.clusterName() : VAULT_PREPROD_NAVN);
    }

    private static String getRole(String rolePrefix, DatasourceRole role) {
        return String.format("%s-%s", rolePrefix, role.name().toLowerCase());
    }

    public static String getDbRole(String datasoureName, DatasourceRole role) {
        return String.format("%s-%s", getRolePrefix(datasoureName), role.name().toLowerCase());
    }

    private static String getRolePrefix(String datasourceName) {
        return ENV.getProperty(datasourceName + ".username");
    }

    private static HikariConfig initConnectionPoolConfig(String dataSourceName, int maxPoolSize) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ENV.getProperty(dataSourceName + ".url"));

        config.setMinimumIdle(0);
        config.setMaximumPoolSize(maxPoolSize);
        config.setIdleTimeout(10001);
        config.setMaxLifetime(30001);
        config.setConnectionTestQuery("select 1");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMetricRegistry(Metrics.globalRegistry);
        Properties dsProperties = new Properties();
        config.setDataSourceProperties(dsProperties);

        return config;
    }

    private static DataSource createVaultDatasource(HikariConfig config, String mountPath, String role) {
        try {
            return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, role);
        } catch (VaultError vaultError) {
            throw new IllegalStateException("Vault feil ved opprettelse av databaseforbindelse", vaultError);
        }
    }

    private static DataSource createLocalDatasource(HikariConfig config, String schema, String username, String password) {
        config.setUsername(username);
        config.setPassword(password); // NOSONAR false positive
        if (schema != null && !schema.isEmpty()) {
            config.setSchema(schema);
        }
        return new HikariDataSource(config);
    }
}
