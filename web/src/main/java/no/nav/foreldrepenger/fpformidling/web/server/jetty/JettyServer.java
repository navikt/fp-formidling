package no.nav.foreldrepenger.fpformidling.web.server.jetty;

import static org.eclipse.jetty.webapp.MetaInfConfiguration.WEBINF_JAR_PATTERN;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import no.nav.foreldrepenger.fpformidling.web.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.fpformidling.web.server.HeadersToMDCFilterBean;
import no.nav.foreldrepenger.fpformidling.web.server.jetty.db.DatasourceRole;
import no.nav.foreldrepenger.fpformidling.web.server.jetty.db.DatasourceUtil;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.core.configuration.IssuerProperties;
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.jaxrs.servlet.JaxrsJwtTokenValidationFilter;
import no.nav.vedtak.isso.IssoApplication;
import no.nav.vedtak.sikkerhet.ContextPathHolder;

public class JettyServer {

    public static final String AZUREAD = "azuread";
    public static final String OPENAM = "openam";

    private static final Environment ENV = Environment.current();
    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

    private static final String CONTEXT_PATH = ENV.getProperty("context.path","/fpformidling");
    private static final String STS = "sts";

    /**
     * Legges først slik at alltid resetter context før prosesserer nye requests.
     * Kjøres først så ikke risikerer andre har satt Request#setHandled(true).
     */
    static final class ResetLogContextHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
            MDC.clear();
        }
    }

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
        ContextPathHolder.instance(CONTEXT_PATH);
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
        final String trustStorePathProp = "javax.net.ssl.trustStore";
        final String trustStorePasswordProp = "javax.net.ssl.trustStorePassword";

        var defaultLocation = ENV.getProperty("user.home", ".") + "/.modig/truststore.jks";
        var storePath = ENV.getProperty(trustStorePathProp, defaultLocation);
        var storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException("Finner ikke truststore i " + storePath
                    + "\n\tKonfrigurer enten som System property '" + trustStorePathProp + "' eller environment variabel '"
                    + trustStorePathProp.toUpperCase().replace('.', '_') + "'");
        }
        var password = ENV.getProperty(trustStorePasswordProp, "changeit");
        System.setProperty(trustStorePathProp, storeFile.getAbsolutePath());
        System.setProperty(trustStorePasswordProp, password);
    }

    private void konfigurerJndi() throws NamingException {
        new EnvEntry("jdbc/defaultDS", DatasourceUtil.createDatasource(DatasourceRole.USER, 4));
    }

    void migrerDatabaser() {
        try (var dataSource = DatasourceUtil.createDatasource(DatasourceRole.ADMIN,2)) {
            var flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:/db/migration/defaultDS")
                    .baselineOnMigrate(true);
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
        var handlers = new HandlerList(new ResetLogContextHandler(), createContext());
        server.setHandler(handlers);
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

    private static WebAppContext createContext() throws IOException {
        var ctx = new WebAppContext();
        ctx.setParentLoaderPriority(true);
        ctx.setContextPath(CONTEXT_PATH);
        ctx.setBaseResource(createResourceCollection());
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        ctx.setAttribute(WEBINF_JAR_PATTERN, "^.*jersey-.*.jar$|^.*felles-.*.jar$");
        ctx.addEventListener(new org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener());
        ctx.addEventListener(new org.jboss.weld.environment.servlet.Listener());
        updateMetaData(ctx.getMetaData());
        ctx.setThrowUnavailableOnStartupException(true);
        addFilters(ctx);
        return ctx;
    }

    private static ResourceCollection createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"));
    }

    private static void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        var resources = getWebInfClasses().stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .distinct()
                .toList();

        metaData.setWebInfClassesResources(resources);
    }

    private static void addFilters(WebAppContext ctx) {
        var dispatcherType = EnumSet.of(DispatcherType.REQUEST);

        LOG.trace("Installerer JaxrsJwtTokenValidationFilter");
        ctx.addFilter(new FilterHolder(new JaxrsJwtTokenValidationFilter(config())),
                "/api/*",
                dispatcherType);
        ctx.addFilter(new FilterHolder(new HeadersToMDCFilterBean()),
                "/api/*",
                dispatcherType);
        var corsFilter = ctx.addFilter(CrossOriginFilter.class, "/*", dispatcherType);

        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, ENV.getProperty("cors.allowed.origins", "*"));
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, ENV.getProperty("cors.allowed.headers", "*"));
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, ENV.getProperty("cors.allowed.methods", "*"));
    }

    private static MultiIssuerConfiguration config() {
        return new MultiIssuerConfiguration(
                Map.of(
                        STS, stsIssuerProperties(),
                        AZUREAD, azureIssuerProperties(),
                        OPENAM, openAmIssuerProperties()));
    }

    private static IssuerProperties stsIssuerProperties() {
        return new IssuerProperties(ENV.getRequiredProperty("oidc.sts.well.known.url", URL.class),
                new IssuerProperties.Validation(List.of("azp", "identType")));
    }

    private static IssuerProperties azureIssuerProperties() {
        var issuerProperties = new IssuerProperties(
                ENV.getRequiredProperty("azure.app.well.known.url", URL.class),
                List.of(ENV.getRequiredProperty("azure.app.client.id")));
        if (!ENV.isLocal()) {
            issuerProperties.setProxyUrl(ENV.getRequiredProperty("http.proxy", URL.class));
        }
        return issuerProperties;
    }

    private static IssuerProperties openAmIssuerProperties() {
        return new IssuerProperties(ENV.getRequiredProperty("oidc.open.am.well.known.url", URL.class), List.of(ENV.getRequiredProperty(
                "oidc.open.am.client.id")), "ID_token");
    }

    private static List<Class<?>> getWebInfClasses() {
        return List.of(ApplicationConfig.class, IssoApplication.class);
    }

    private Integer getServerPort() {
        return this.serverPort;
    }
}
