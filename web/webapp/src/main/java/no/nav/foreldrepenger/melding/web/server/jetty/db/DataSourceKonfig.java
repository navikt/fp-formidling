package no.nav.foreldrepenger.melding.web.server.jetty.db;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import no.nav.vault.jdbc.hikaricp.VaultError;
import no.nav.vedtak.konfig.PropertyUtil;

public class DataSourceKonfig {

    private static final String location = "classpath:/db/migration/";
    private final DataSource defaultDatasource;
    private final DatabaseScript.MigrationDataSource migrationDatasource;
    private final String migrationScripts;

    public DataSourceKonfig(String schema) {
        final String dataSourceName = "defaultDS";
        migrationScripts = location + "defaultDS";
        final boolean useVault = Boolean.parseBoolean(PropertyUtil.getProperty(dataSourceName + ".vault.enable"));
        HikariConfig config = initHikariConfig(dataSourceName);
        if (useVault) {
            final String vaultRolePrefix = PropertyUtil.getProperty(dataSourceName + ".vault.roleprefix");
            final String vaultMountPath = PropertyUtil.getProperty(dataSourceName + ".vault.mountpath");
            // TODO: validate not null etc?
            defaultDatasource = createVaultDatasource(config, vaultMountPath, vaultRolePrefix, false);
            config = initHikariConfig(dataSourceName);
            config.setMaximumPoolSize(1);
            migrationDatasource = new DatabaseScript.MigrationDataSource(
                    createVaultDatasource(config, vaultMountPath, vaultRolePrefix, true),
                    "set role '" + roleWithRolePrefix(vaultRolePrefix, true) + "'");
        } else {
            defaultDatasource = createUsernamePasswordDatasource(dataSourceName, config, schema);
            migrationDatasource = new DatabaseScript.MigrationDataSource(defaultDatasource, null, schema);
        }
    }

    public DataSourceKonfig() {
        this(null);
    }

    public DataSource getDefaultDatasource() {
        return defaultDatasource;
    }

    public DatabaseScript.MigrationDataSource getMigrationDatasource() {
        return migrationDatasource;
    }

    public String getMigrationScripts() {
        return migrationScripts;
    }

    private HikariConfig initHikariConfig(String dataSourceName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(PropertyUtil.getProperty(dataSourceName + ".url"));

        config.setConnectionTimeout(1000);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(3);
        config.setMaxLifetime(60 * 1000 * 5); //5 minutter max TTL
        config.setConnectionTestQuery("select 1");
        config.setDriverClassName("org.postgresql.Driver");

        Properties dsProperties = new Properties();
        config.setDataSourceProperties(dsProperties);

        return config;
    }

    private DataSource createVaultDatasource(HikariConfig config, String mountPath, String rolePrefixForVault, boolean admin) {
        try {
            return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, roleWithRolePrefix(rolePrefixForVault, admin));
        } catch (VaultError vaultError) {
            throw new RuntimeException("Vault feil ved opprettelse av databaseforbindelse", vaultError);
        }
    }

    private String roleWithRolePrefix(String rolePrefixForVault, boolean admin) {
        // Dobbelsjekk at det ikke er noen skumle tegn i denne konfigen siden den faktisk blir en del av "initSql" som kjøres før migrering
        if (!inneholderKunAlfanumeriskOgStrek(rolePrefixForVault)) {
            throw new IllegalArgumentException("Forventet kun alfanumeriske tegn, _ og - i rolePrefixForVault");
        }
        return rolePrefixForVault + "-" + (admin ? "admin" : "user");
    }

    private boolean inneholderKunAlfanumeriskOgStrek(final String s) {
        return StringUtils.isAlphanumeric(s.replace('-', 'a').replace('_', 'a'));
    }

    private DataSource createUsernamePasswordDatasource(String dataSourceName, HikariConfig config, String schema) {
        config.setUsername(PropertyUtil.getProperty(dataSourceName + ".username"));
        config.setPassword(PropertyUtil.getProperty(dataSourceName + ".password")); // NOSONAR false positive
        if (!no.nav.vedtak.util.StringUtils.nullOrEmpty(schema)) {
            config.setSchema(schema);
        }
        return new HikariDataSource(config);
    }
}
