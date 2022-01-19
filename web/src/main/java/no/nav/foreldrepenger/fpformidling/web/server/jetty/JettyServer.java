package no.nav.foreldrepenger.fpformidling.web.server.jetty;

import static no.nav.foreldrepenger.konfig.Cluster.LOCAL;
import static no.nav.foreldrepenger.konfig.Cluster.NAIS_CLUSTER_NAME;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.web.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.fpformidling.web.server.jetty.db.DatabaseScript;
import no.nav.foreldrepenger.fpformidling.web.server.jetty.db.DatasourceRole;
import no.nav.foreldrepenger.fpformidling.web.server.jetty.db.DatasourceUtil;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vedtak.isso.IssoApplication;

public class JettyServer extends AbstractJettyServer {

    private static final Environment ENV = Environment.current();
    private static final String DATASOURCE_NAME = "defaultDS";
    private static final Logger log = LoggerFactory.getLogger(JettyServer.class);

    public JettyServer(int serverPort) {
        super(new JettyWebKonfigurasjon(serverPort));
    }

    public static void main(String[] args) throws Exception {
        jettyServer(args).bootStrap();
    }

    private static JettyServer jettyServer(String[] args) {
        if (args.length > 0) {
            return new JettyServer(Integer.parseUnsignedInt(args[0]));
        }
            return new JettyServer(8080);
    }

    @Override
    protected void konfigurerJndi() throws Exception {
        new EnvEntry("jdbc/defaultDS",
                DatasourceUtil.createDatasource(DATASOURCE_NAME, DatasourceRole.USER, ENV.getCluster(), 4));
    }

    @Override
    protected void migrerDatabaser() {
        String initSql = String.format("SET ROLE \"%s\"", DatasourceUtil.getDbRole(DATASOURCE_NAME, DatasourceRole.ADMIN));
        if (LOCAL.equals(ENV.getCluster())) {
            // TODO: Ønsker egentlig ikke dette, men har ikke satt opp skjema lokalt
            // til å ha en admin bruker som gjør migrering og en annen som gjør CRUD
            // operasjoner
            initSql = null;
        }
        DataSource migreringDs = DatasourceUtil.createDatasource(DATASOURCE_NAME, DatasourceRole.ADMIN, ENV.getCluster(),
                1);
        try {
            DatabaseScript.migrate(migreringDs, initSql, false);
            migreringDs.getConnection().close();
        } catch (SQLException e) {
            log.warn("Klarte ikke stenge connection etter migrering", e);
        }
    }

    @Override
    protected List<Class<?>> getWebInfClasses() {
        return List.of(ApplicationConfig.class, IssoApplication.class);
    }
}
