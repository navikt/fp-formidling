package no.nav.foreldrepenger.fpformidling.server;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import no.nav.vault.jdbc.hikaricp.VaultError;

class DatasourceUtil {
    private static final Environment ENV = Environment.current();

    private DatasourceUtil() {
    }

    static HikariDataSource createDatasource(DatasourceRole role, int maxPoolSize) {
        var config = initConnectionPoolConfig(maxPoolSize);
        if (ENV.isVTP() || ENV.isLocal()) {
            return createLocalDatasource(config);
        }
        return createVaultDatasource(config, mountPath(), getRole(role));
    }

    static String getRole(DatasourceRole role) {
        return String.format("%s-%s", getUsername(), role.name().toLowerCase());
    }

    private static HikariConfig initConnectionPoolConfig(int maxPoolSize) {
        var config = new HikariConfig();
        config.setJdbcUrl(ENV.getRequiredProperty("defaultDS.url"));
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(1));
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(maxPoolSize);
        config.setConnectionTestQuery("select 1");
        config.setDriverClassName("org.postgresql.Driver");

        config.setMetricRegistry(Metrics.globalRegistry);

        var dsProperties = new Properties();
        dsProperties.setProperty("reWriteBatchedInserts", "true");
        dsProperties.setProperty("logServerErrorDetail", "false"); // skrur av batch exceptions som lekker statements i åpen logg
        config.setDataSourceProperties(dsProperties);
        // skrur av autocommit her, da kan vi bypasse dette senere når hibernate setter opp entitymanager for bedre conn mgmt
        config.setAutoCommit(false);
        return config;
    }

    private static HikariDataSource createLocalDatasource(HikariConfig config) {
        config.setUsername(getUsername());
        config.setPassword(getUsername());
        config.setSchema("public");
        return new HikariDataSource(config);
    }

    private static String getUsername() {
        return ENV.getRequiredProperty("defaultDS.username");
    }

    private static HikariDataSource createVaultDatasource(HikariConfig config, String mountPath, String role) {
        try {
            return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, role);
        } catch (VaultError vaultError) {
            throw new IllegalStateException("Vault feil ved opprettelse av databaseforbindelse", vaultError);
        }
    }

    private static String mountPath() {
        return "postgresql/" + (ENV.isProd() ? "prod-fss" : "preprod-fss");
    }
}
