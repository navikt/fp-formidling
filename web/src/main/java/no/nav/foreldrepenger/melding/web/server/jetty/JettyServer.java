package no.nav.foreldrepenger.melding.web.server.jetty;

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

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.melding.web.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.melding.web.server.jetty.db.DatabaseScript;
import no.nav.foreldrepenger.melding.web.server.jetty.db.DatasourceRole;
import no.nav.foreldrepenger.melding.web.server.jetty.db.DatasourceUtil;
import no.nav.vedtak.isso.IssoApplication;

public class JettyServer extends AbstractJettyServer {

    private static final Environment ENV = Environment.current();
    private static final String DATASOURCE_NAME = "defaultDS";
    private static final Logger log = LoggerFactory.getLogger(JettyServer.class);

    public JettyServer() {
        this(new JettyWebKonfigurasjon());
    }

    public JettyServer(int serverPort) {
        this(new JettyWebKonfigurasjon(serverPort));
    }

    JettyServer(AppKonfigurasjon appKonfigurasjon) {
        super(appKonfigurasjon);
    }

    public static void main(String[] args) throws Exception {
        System.setProperty(NAIS_CLUSTER_NAME, ENV.clusterName());
        JettyServer jettyServer;
        if (args.length > 0) {
            int serverPort = Integer.parseUnsignedInt(args[0]);
            jettyServer = new JettyServer(serverPort);
        } else {
            jettyServer = new JettyServer();
        }
        jettyServer.bootStrap();
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
    protected WebAppContext createContext(AppKonfigurasjon appKonfigurasjon) throws IOException {
        WebAppContext webAppContext = super.createContext(appKonfigurasjon);
        webAppContext.setParentLoaderPriority(true);
        updateMetaData(webAppContext.getMetaData());
        return webAppContext;
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        List<Class<?>> appClasses = Arrays.asList((Class<?>) ApplicationConfig.class, (Class<?>) IssoApplication.class);

        List<Resource> resources = appClasses.stream().map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .collect(Collectors.toList());

        metaData.setWebInfClassesResources(resources);
    }

    @Override
    protected ResourceCollection createResourceCollection() throws IOException {
        return new ResourceCollection(
                Resource.newClassPathResource("META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"));
    }

}
