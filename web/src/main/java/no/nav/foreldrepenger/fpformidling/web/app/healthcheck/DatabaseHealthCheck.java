package no.nav.foreldrepenger.fpformidling.web.app.healthcheck;

import jakarta.annotation.Resource;
import no.nav.vedtak.log.metrics.LiveAndReadinessAware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

import java.sql.SQLException;

@ApplicationScoped
public class DatabaseHealthCheck implements LiveAndReadinessAware {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    private static final String JDBC_DEFAULT_DS = "jdbc/defaultDS";
    private static final String SQL_QUERY = "select 1";

    @Resource(mappedName = JDBC_DEFAULT_DS)
    private DataSource dataSource;

    DatabaseHealthCheck() {
        // CDI
    }

    private boolean isOK() {
        try (var connection = dataSource.getConnection()) {
            try (var statement = connection.createStatement()) {
                if (!statement.execute(SQL_QUERY)) {
                    LOG.warn("Feil ved SQL-spørring {} mot databasen", SQL_QUERY);
                    return false;
                }
            }
        } catch (SQLException e) {
            LOG.warn("Feil ved SQL-spørring {} mot databasen", SQL_QUERY);
            return false;
        }

        return true;
    }

    @Override
    public boolean isReady() {
        return isOK();
    }

    @Override
    public boolean isAlive() {
        return isOK();
    }
}
