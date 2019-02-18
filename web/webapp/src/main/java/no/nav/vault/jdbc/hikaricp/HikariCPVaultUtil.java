package no.nav.vault.jdbc.hikaricp;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class HikariCPVaultUtil {

    private static final Logger logger = LoggerFactory.getLogger(HikariCPVaultUtil.class);
    private final HikariConfig hikariConfig;
    private final Vault vault;
    private final String mountPath;
    private final String role;
    private HikariDataSource ds = null;

    private HikariCPVaultUtil(final HikariConfig config, final Vault vault, final String mountPath, final String role) {
        this.hikariConfig = config;
        this.vault = vault;
        this.mountPath = mountPath;
        this.role = role;
    }

    public static HikariDataSource createHikariDataSourceWithVaultIntegration(final HikariConfig config, final String mountPath, final String role) throws VaultError {
        final VaultUtil instance = VaultUtil.getInstance();
        final Vault vault = instance.getClient();

        final HikariCPVaultUtil hikariCPVaultUtil = new HikariCPVaultUtil(config, vault, mountPath, role);

        final class RefreshDbCredentialsTask extends TimerTask {
            @Override
            public void run() {
                try {
                    final RefreshResult refreshResult = hikariCPVaultUtil.refreshCredentialsAndReturnRefreshInterval();
                    instance.getTimer().schedule(new RefreshDbCredentialsTask(), VaultUtil.suggestedRefreshInterval(refreshResult.leaseDuration * 1000));
                } catch (VaultException e) {
                    // We cannot re-throw exceptions (since this task runs in its own thread), so we log them instead.
                    if (e.getHttpStatusCode() == 403) {
                        logger.error("Vault denied permission to fetch database credentials for role \"" + role + "\"", e);
                    } else {
                        logger.error("Could not fetch database credentials for role \"" + role + "\"", e);
                    }
                }
            }
        }

        // The first time we fetch credentials, we can rethrow an exception if we get one - fail fast!
        try {
            final RefreshResult refreshResult = hikariCPVaultUtil.refreshCredentialsAndReturnRefreshInterval();
            instance.getTimer().schedule(new RefreshDbCredentialsTask(), VaultUtil.suggestedRefreshInterval(refreshResult.leaseDuration * 1000));
        } catch (VaultException e) {
            throw new VaultError("Could not fetch database credentials for role \"" + role + "\"", e);
        }

        final HikariDataSource ds = new HikariDataSource(config);
        hikariCPVaultUtil.setDs(ds);

        return ds;
    }

    private void setDs(final HikariDataSource ds) {
        this.ds = ds;
    }

    private RefreshResult refreshCredentialsAndReturnRefreshInterval() throws VaultException {
        final String path = mountPath + "/creds/" + role;
        logger.info("Renewing database credentials for role \"" + role + "\"");
        final LogicalResponse response = vault.logical().read(path);
        final String username = response.getData().get("username");
        final String password = response.getData().get("password");
        logger.info("Got new credentials (username=" + username + ")");

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        if (ds != null) {
            ds.setUsername(username);
            ds.setPassword(password);
        }

        return new RefreshResult(response.getLeaseId(), response.getLeaseDuration());
    }

    private static final class RefreshResult {
        final String leaseId;
        final long leaseDuration;

        RefreshResult(String leaseId, long leaseDuration) {
            this.leaseId = leaseId;
            this.leaseDuration = leaseDuration;
        }
    }
}
