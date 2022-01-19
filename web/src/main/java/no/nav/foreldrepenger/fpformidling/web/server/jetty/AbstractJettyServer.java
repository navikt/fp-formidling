package no.nav.foreldrepenger.fpformidling.web.server.jetty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.jaspi.DefaultAuthConfigFactory;
import org.eclipse.jetty.security.jaspi.JaspiAuthenticatorFactory;
import org.eclipse.jetty.security.jaspi.provider.JaspiAuthConfigProvider;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.MDC;

import no.nav.vedtak.sikkerhet.jaspic.OidcAuthModule;

abstract class AbstractJettyServer {

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

    private final JettyWebKonfigurasjon webKonfigurasjon;

    public AbstractJettyServer(JettyWebKonfigurasjon webKonfigurasjon) {
        this.webKonfigurasjon = webKonfigurasjon;
    }

    protected void bootStrap() throws Exception {
        konfigurer();
        migrerDatabaser();
        start(webKonfigurasjon);
    }

    protected void konfigurer() throws Exception {
        konfigurerSikkerhet();
        konfigurerJndi();
    }

    protected void konfigurerSikkerhet() {
        var factory = new DefaultAuthConfigFactory();
        factory.registerConfigProvider(new JaspiAuthConfigProvider(new OidcAuthModule()),
                "HttpServlet",
                "server " + webKonfigurasjon.getContextPath(),
                "OIDC Authentication");

        AuthConfigFactory.setFactory(factory);
    }

    protected abstract void konfigurerJndi() throws Exception; //NOSONAR

    protected abstract void migrerDatabaser();

    protected void start(JettyWebKonfigurasjon jettyWebKonfigurasjon) throws Exception {
        var server = new Server(jettyWebKonfigurasjon.getServerPort());
        server.setConnectors(createConnectors(jettyWebKonfigurasjon, server).toArray(new Connector[]{}));
        var handlers = new HandlerList(new ResetLogContextHandler(), createContext(jettyWebKonfigurasjon));
        server.setHandler(handlers);
        server.start();
        server.join();
    }

    protected List<Connector> createConnectors(JettyWebKonfigurasjon jettyWebKonfigurasjon, Server server) {
        List<Connector> connectors = new ArrayList<>();
        var httpConnector = new ServerConnector(server, new HttpConnectionFactory(createHttpConfiguration()));
        httpConnector.setPort(jettyWebKonfigurasjon.getServerPort());
        connectors.add(httpConnector);

        return connectors;
    }

    protected WebAppContext createContext(JettyWebKonfigurasjon webKonfigurasjon) throws IOException {
        var webAppContext = new WebAppContext();
        webAppContext.setParentLoaderPriority(true);

        // må hoppe litt bukk for å hente web.xml fra classpath i stedet for fra
        // filsystem.
        String descriptor;
        try (var resource = Resource.newClassPathResource("/WEB-INF/web.xml")) {
            descriptor = resource.getURI().toURL().toExternalForm();
        }
        webAppContext.setDescriptor(descriptor);
        webAppContext.setBaseResource(createResourceCollection());
        webAppContext.setContextPath(webKonfigurasjon.getContextPath());
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern", "^.*jersey-.*.jar$|^.*felles-.*.jar$");
        webAppContext.setSecurityHandler(createSecurityHandler());

        webAppContext.setParentLoaderPriority(true);
        updateMetaData(webAppContext.getMetaData());
        return webAppContext;
    }

    private ResourceCollection createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"));
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        var resources = getWebInfClasses().stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .distinct()
                .collect(Collectors.toList());

        metaData.setWebInfClassesResources(resources);
    }

    protected abstract List<Class<?>> getWebInfClasses();

    protected HttpConfiguration createHttpConfiguration() {
        // Create HTTP Config
        var httpConfig = new HttpConfiguration();

        // Add support for X-Forwarded headers
        httpConfig.addCustomizer(new ForwardedRequestCustomizer());

        // Gjør det mulig å ta imot større argumenter igjennom REST (trengs for dokgen-json-til-pdf i forvaltningsgrensesnittet)
        httpConfig.setRequestHeaderSize(32768);

        return httpConfig;
    }

    private static SecurityHandler createSecurityHandler() {
        var securityHandler = new ConstraintSecurityHandler();
        securityHandler.setAuthenticatorFactory(new JaspiAuthenticatorFactory());

        var loginService = new JAASLoginService();
        loginService.setName("jetty-login");
        loginService.setLoginModuleName("jetty-login");
        loginService.setIdentityService(new DefaultIdentityService());
        securityHandler.setLoginService(loginService);

        return securityHandler;
    }
}
