package no.nav.foreldrepenger.melding.web.server.jetty;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JettyDevServer extends JettyServer {
    /**
     * @see https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html
     */
    private static final String TRUSTSTORE_PASSW_PROP = "javax.net.ssl.trustStorePassword";
    private static final String TRUSTSTORE_PATH_PROP = "javax.net.ssl.trustStore";
    private static final String KEYSTORE_PASSW_PROP = "no.nav.modig.security.appcert.password";
    private static final String KEYSTORE_PATH_PROP = "no.nav.modig.security.appcert.keystore";

    public JettyDevServer() {
        super(new JettyDevKonfigurasjon());
    }

    public static void main(String[] args) throws Exception {
        JettyDevServer devServer = new JettyDevServer();
        devServer.bootStrap();
    }

    @Override
    protected void konfigurer() throws Exception {
        konfigurerLogback();
        konfigurerMiljø();
        super.konfigurer();
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

    protected void konfigurerMiljø() throws Exception {
        System.setProperty("develop-local", "true");
        PropertiesUtils.initProperties();

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
    protected void konfigurerSikkerhet() {
        System.setProperty("conf", "src/main/resources/jetty/");
        super.konfigurerSikkerhet();

        // truststore avgjør hva vi stoler på av sertifikater når vi gjør utadgående TLS kall
        initCryptoStoreConfig("truststore", TRUSTSTORE_PATH_PROP, TRUSTSTORE_PASSW_PROP, "changeit");

        // keystore genererer sertifikat og TLS for innkommende kall. Bruker standard prop hvis definert, ellers faller tilbake på modig props
        var keystoreProp = System.getProperty("javax.net.ssl.keyStore")!=null? "javax.net.ssl.keyStore" : KEYSTORE_PATH_PROP;
        var keystorePasswProp = System.getProperty("javax.net.ssl.keyStorePassword")!=null? "javax.net.ssl.keyStorePassword" : KEYSTORE_PASSW_PROP;
        initCryptoStoreConfig("keystore", keystoreProp, keystorePasswProp, "devillokeystore1234");
    }

    private static String initCryptoStoreConfig(String storeName, String storeProperty, String storePasswordProperty, String defaultPassword) {
        String defaultLocation = getProperty("user.home", ".") + "/.modig/" + storeName + ".jks";

        String storePath = getProperty(storeProperty, defaultLocation);
        File storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException("Finner ikke " + storeName + " i " + storePath
                    + "\n\tKonfigurer enten som System property \'" + storeProperty + "\' eller environment variabel \'"
                    + storeProperty.toUpperCase().replace('.', '_') + "\'");
        }
        String password = getProperty(storePasswordProperty, defaultPassword);
        if(password==null) {
            throw new IllegalStateException("Passord for å aksessere store "+storeName + " i " + storePath + " er null");
        }

        System.setProperty(storeProperty, storeFile.getAbsolutePath());
        System.setProperty(storePasswordProperty, password);
        return storePath;
    }

    private static String getProperty(String key, String defaultValue) {
        String val = System.getProperty(key, defaultValue);
        if (val == null) {
            val = System.getenv(key.toUpperCase().replace('.', '_'));
            val = val == null ? defaultValue : val;
        }
        return val;
    }

    @SuppressWarnings("resource")
    @Override
    protected List<Connector> createConnectors(AppKonfigurasjon appKonfigurasjon, Server server) {
        List<Connector> connectors = super.createConnectors(appKonfigurasjon, server);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(System.getProperty(KEYSTORE_PATH_PROP));
        sslContextFactory.setKeyStorePassword(System.getProperty(KEYSTORE_PASSW_PROP));
        sslContextFactory.setKeyManagerPassword(System.getProperty(KEYSTORE_PASSW_PROP));

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
    protected ResourceCollection createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web")
        );
    }
}
