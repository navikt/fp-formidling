package no.nav.foreldrepenger.fpformidling.server;

import static org.eclipse.jetty.ee10.webapp.MetaInfConfiguration.CONTAINER_JAR_PATTERN;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.eclipse.jetty.ee10.cdi.CdiDecoratingListener;
import org.eclipse.jetty.ee10.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.ee10.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.konfig.ApiConfig;
import no.nav.foreldrepenger.fpformidling.konfig.InternalApiConfig;
import no.nav.foreldrepenger.konfig.Environment;

public class JettyServer {

    private static final Environment ENV = Environment.current();
    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

    private static final String CONTEXT_PATH = ENV.getProperty("context.path", "/fpformidling");
    private static final String JETTY_SCAN_LOCATIONS = "^.*jersey-.*\\.jar$|^.*felles-.*\\.jar$|^.*/app\\.jar$";
    private static final String JETTY_LOCAL_CLASSES = "^.*/target/classes/|";

    private final Integer serverPort;

    public static void main(String[] args) throws Exception {
        jettyServer(args).bootStrap();
    }

    private static JettyServer jettyServer(String[] args) {
        if (args.length > 0) {
            return new JettyServer(Integer.parseUnsignedInt(args[0]));
        }
        return new JettyServer(ENV.getProperty("server.port", Integer.class, 8080));
    }

    JettyServer(int serverPort) {
        this.serverPort = serverPort;
    }

    void bootStrap() throws Exception {
        konfigurerSikkerhet();
        konfigurerJndi();
        migrerDatabaser();
        start();
    }

    private void konfigurerSikkerhet() {
        if (ENV.isLocal()) {
            initTrustStore();
        }
    }

    private static void initTrustStore() {
        final var trustStorePathProp = "javax.net.ssl.trustStore";
        final var trustStorePasswordProp = "javax.net.ssl.trustStorePassword";

        var defaultLocation = ENV.getProperty("user.home", ".") + "/.modig/truststore.jks";
        var storePath = ENV.getProperty(trustStorePathProp, defaultLocation);
        var storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException(
                "Finner ikke truststore i " + storePath + "\n\tKonfrigurer enten som System property '" + trustStorePathProp
                    + "' eller environment variabel '" + trustStorePathProp.toUpperCase().replace('.', '_') + "'");
        }
        var password = ENV.getProperty(trustStorePasswordProp, "changeit");
        System.setProperty(trustStorePathProp, storeFile.getAbsolutePath());
        System.setProperty(trustStorePasswordProp, password);
    }

    private void konfigurerJndi() throws NamingException {
        new EnvEntry("jdbc/defaultDS", DatasourceUtil.createDatasource(DatasourceRole.USER, 4));
    }

    void migrerDatabaser() {
        try (var dataSource = DatasourceUtil.createDatasource(DatasourceRole.ADMIN, 2)) {
            var flyway = Flyway.configure().dataSource(dataSource).locations("classpath:/db/migration/defaultDS").baselineOnMigrate(true);
            if (ENV.isProd() || ENV.isDev()) {
                flyway.initSql(String.format("SET ROLE \"%s\"", DatasourceUtil.getRole(DatasourceRole.ADMIN)));
            }
            flyway.load().migrate();
        } catch (FlywayException e) {
            LOG.error("Feil under migrering av databasen.");
            throw e;
        }
    }

    private void start() throws Exception {
        var server = new Server(getServerPort());
        server.setConnectors(createConnectors(server).toArray(new Connector[]{}));
        server.setHandler(createContext());
        server.start();
        server.join();
    }

    private List<Connector> createConnectors(Server server) {
        List<Connector> connectors = new ArrayList<>();
        var httpConnector = new ServerConnector(server, new HttpConnectionFactory(createHttpConfiguration()));
        httpConnector.setPort(getServerPort());
        connectors.add(httpConnector);
        return connectors;
    }

    private static HttpConfiguration createHttpConfiguration() {
        var httpConfig = new HttpConfiguration();
        // Add support for X-Forwarded headers
        httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        // Gjør det mulig å ta imot større argumenter igjennom REST (trengs for dokgen-json-til-pdf i forvaltningsgrensesnittet)
        httpConfig.setRequestHeaderSize(32768);
        return httpConfig;
    }

    private static ContextHandler createContext() throws IOException {
        var ctx = new WebAppContext(CONTEXT_PATH, null, simpleConstraints(), null,
            new ErrorPageErrorHandler(), ServletContextHandler.NO_SESSIONS);
        ctx.setParentLoaderPriority(true);

        // må hoppe litt bukk for å hente web.xml fra classpath i stedet for fra filsystem.
        String baseResource;
        try (var factory = ResourceFactory.closeable()) {
            baseResource = factory.newResource(".").getRealURI().toURL().toExternalForm();
        }

        ctx.setBaseResourceAsString(baseResource);
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        // Scanns the CLASSPATH for classes and jars.
        ctx.setAttribute(CONTAINER_JAR_PATTERN, String.format("%s%s", ENV.isLocal() ? JETTY_LOCAL_CLASSES : "", JETTY_SCAN_LOCATIONS));

        // Enable Weld + CDI
        ctx.setInitParameter(CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE, CdiDecoratingListener.MODE);
        ctx.addServletContainerInitializer(new CdiServletContainerInitializer());
        ctx.addServletContainerInitializer(new org.jboss.weld.environment.servlet.EnhancedListener());

        ctx.setThrowUnavailableOnStartupException(true);
        return ctx;
    }

    private static ConstraintSecurityHandler simpleConstraints() {
        var handler = new ConstraintSecurityHandler();
        // Slipp gjennom kall fra plattform til JaxRs. Foreløpig kun behov for GET
        handler.addConstraintMapping(pathConstraint(Constraint.ALLOWED, InternalApiConfig.API_URI + "/*"));
        // Slipp gjennom til autentisering i JaxRs / auth-filter
        handler.addConstraintMapping(pathConstraint(Constraint.ALLOWED, ApiConfig.API_URI + "/*"));
        // Alt annet av paths og metoder forbudt - 403
        handler.addConstraintMapping(pathConstraint(Constraint.FORBIDDEN, "/*"));
        return handler;
    }

    private static ConstraintMapping pathConstraint(Constraint constraint, String path) {
        var mapping = new ConstraintMapping();
        mapping.setConstraint(constraint);
        mapping.setPathSpec(path);
        return mapping;
    }

    private Integer getServerPort() {
        return this.serverPort;
    }
}
