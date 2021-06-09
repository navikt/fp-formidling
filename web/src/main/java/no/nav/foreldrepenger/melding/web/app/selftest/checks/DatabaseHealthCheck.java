package no.nav.foreldrepenger.melding.web.app.selftest.checks;

import java.sql.SQLException;
import java.sql.Statement;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import no.nav.vedtak.log.metrics.ReadinessAware;

@ApplicationScoped
public class DatabaseHealthCheck implements ReadinessAware {

    private static final String JDBC_DEFAULT_DS = "jdbc/defaultDS";
    private static final String SQL_QUERY = "select count(1) from PROSESS_TASK_TYPE";
    private final DataSource ds;

    public DatabaseHealthCheck() throws NamingException {
        this(JDBC_DEFAULT_DS);
    }

    DatabaseHealthCheck(String dsJndiName) throws NamingException {
        ds = DataSource.class.cast(new InitialContext().lookup(dsJndiName));
    }

    @Override
    public boolean isReady() {
        return performCheck();
    }

    private boolean performCheck() {
        try (var connection = ds.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                if (!statement.execute(SQL_QUERY)) {
                    throw new SQLException("SQL-spørring ga ikke et resultatsett");
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
