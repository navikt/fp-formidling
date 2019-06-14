package no.nav.foreldrepenger.melding.web.server.jetty.db;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import no.nav.vault.jdbc.hikaricp.VaultError;
import no.nav.vedtak.konfig.PropertyUtil;

public class DatasourceUtil {

    public static DataSource createDatasource(String datasourceName, DatasourceRole role, EnvironmentClass environmentClass) {
        String rolePrefix = getRolePrefix(datasourceName);
        HikariConfig config = initConnectionPoolConfig(datasourceName);
        if (EnvironmentClass.LOCALHOST.equals(environmentClass)) {
            String password = PropertyUtil.getProperty(datasourceName + ".password");
            return createLocalDatasource(config, "public", rolePrefix, password);
        } else {
            String dbRole = getRole(rolePrefix, role);
            return createVaultDatasource(config, environmentClass.mountPath(), dbRole);
        }
    }

    private static String getRole(String rolePrefix, DatasourceRole role) {
        return String.format("%s-%s", rolePrefix, role.name().toLowerCase());
    }

    public static String getDbRole(String datasoureName, DatasourceRole role) {
        return String.format("%s-%s", getRolePrefix(datasoureName), role.name().toLowerCase());
    }

    private static String getRolePrefix(String datasourceName) {
        return PropertyUtil.getProperty(datasourceName + ".username");
    }

    private static HikariConfig initConnectionPoolConfig(String dataSourceName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(PropertyUtil.getProperty(dataSourceName + ".url"));

        config.setConnectionTimeout(1000);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(2);
        config.setConnectionTestQuery("select 1");
        config.setDriverClassName("org.postgresql.Driver");

        Properties dsProperties = new Properties();
        config.setDataSourceProperties(dsProperties);

        return config;
    }

    private static DataSource createVaultDatasource(HikariConfig config, String mountPath, String role) {
        try {
            return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, role);
        } catch (VaultError vaultError) {
            throw new RuntimeException("Vault feil ved opprettelse av databaseforbindelse", vaultError);
        }
    }

    private static DataSource createLocalDatasource(HikariConfig config, String schema, String username, String password) {
        config.setUsername(username);
        config.setPassword(password); // NOSONAR false positive
        if (!no.nav.vedtak.util.StringUtils.nullOrEmpty(schema)) {
            config.setSchema(schema);
        }
        return new HikariDataSource(config);
    }
}
