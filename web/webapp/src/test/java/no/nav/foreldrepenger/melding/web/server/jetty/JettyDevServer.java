package no.nav.foreldrepenger.melding.web.server.jetty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import no.nav.foreldrepenger.melding.sikkerhet.TestSertifikater;
import no.nav.foreldrepenger.melding.web.server.jetty.db.EnvironmentClass;

public class JettyDevServer extends JettyServer {
    private static final String VTP_ARGUMENT = "--vtp";
    private static boolean vtp;
    String schema;

    public JettyDevServer() {
        super(new JettyDevKonfigurasjon());
    }

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            if (arg.equals(VTP_ARGUMENT)) {
                vtp = true;
            }
        }

        JettyDevServer devServer = new JettyDevServer();
        devServer.bootStrap();
    }

    @Override
    protected void konfigurer() throws Exception {
        konfigurerLogback();
        super.konfigurer();
    }

    @Override
    protected EnvironmentClass getEnvironmentClass() {
        return EnvironmentClass.LOCALHOST;
    }

    protected void konfigurerLogback() throws IOException {
        new File("./logs").mkdirs();
        System.setProperty("APP_LOG_HOME", "./logs");
        File logbackConfig = PropertiesUtils.lagLogbackConfig();

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(logbackConfig.getAbsolutePath());
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    @Override
    protected void konfigurerMiljø() throws Exception {
        System.setProperty("develop-local", "true");
        PropertiesUtils.lagPropertiesFilFraTemplate();
        PropertiesUtils.initProperties(JettyDevServer.vtp);

        List<JettyDevDbKonfigurasjon> konfigs = PropertiesUtils.getDBConnectionProperties()
                .stream()
                .filter(jettyDevDbKonfigurasjon -> jettyDevDbKonfigurasjon.getDatasource().equals("defaultDS"))
                .collect(Collectors.toList());
        if (konfigs.size() == 1) {
            final JettyDevDbKonfigurasjon konfig = konfigs.get(0);
            System.setProperty("defaultDS.url", konfig.getUrl());
            System.setProperty("defaultDS.username", konfig.getUser()); // benyttes kun hvis vault.enable=false
            System.setProperty("defaultDS.password", konfig.getPassword()); // benyttes kun hvis vault.enable=false
        } else {
            throw new RuntimeException("forventet én datasourc-konfiger med defaultDS, men fant " + konfigs.size());
        }
    }

    @Override
    protected void konfigurerSikkerhet() throws IOException {
        System.setProperty("conf", "src/main/resources/jetty/");
        super.konfigurerSikkerhet();
        //Oppsett for å koble mot miljø fra lokalt, uten å ha avhengighet til modig.
        //Krever at man har tilgang til sertifikater og passord
        TestSertifikater.setup(System.getProperty("keystore.password"), System.getProperty("truststore.password"));
        // Eksponer truststore for run-java-local.sh
        File tempTrustStore = new File(System.getProperty("javax.net.ssl.trustStore"));
        File truststore = new File("./truststore.jts");
        Files.copy(tempTrustStore.toPath(), truststore.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @SuppressWarnings("resource")
    @Override
    protected List<Connector> createConnectors(AppKonfigurasjon appKonfigurasjon, Server server) {
        List<Connector> connectors = super.createConnectors(appKonfigurasjon, server);

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(System.getProperty("no.nav.modig.security.appcert.keystore"));
        sslContextFactory.setKeyStorePassword(System.getProperty("no.nav.modig.security.appcert.password"));
        sslContextFactory.setKeyManagerPassword(System.getProperty("no.nav.modig.security.appcert.password"));

        HttpConfiguration https = createHttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(appKonfigurasjon.getSslPort());
        connectors.add(sslConnector);

        return connectors;
    }

    @Override
    protected WebAppContext createContext(AppKonfigurasjon appKonfigurasjon) throws IOException {
        WebAppContext webAppContext = super.createContext(appKonfigurasjon);
        // https://www.eclipse.org/jetty/documentation/9.4.x/troubleshooting-locked-files-on-windows.html
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        return webAppContext;
    }

    @Override
    protected ResourceCollection createResourceCollection() throws IOException {
        return new ResourceCollection(
                Resource.newClassPathResource("/web")
        );
    }
}
