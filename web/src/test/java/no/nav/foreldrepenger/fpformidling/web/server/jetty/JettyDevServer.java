package no.nav.foreldrepenger.fpformidling.web.server.jetty;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;

import no.nav.foreldrepenger.konfig.Environment;

public class JettyDevServer extends JettyServer {

    private static final Environment ENV = Environment.current();
    private static final String TRUSTSTORE_PASSW_PROP = "javax.net.ssl.trustStorePassword";
    private static final String TRUSTSTORE_PATH_PROP = "javax.net.ssl.trustStore";

    public JettyDevServer() {
        super(8010);
    }

    public static void main(String[] args) throws Exception {
        new JettyDevServer().bootStrap();
    }

    @Override
    protected void konfigurer() throws Exception {
        System.setProperty("conf", "src/main/resources/jetty/");
        konfigurerMiljø();
        super.konfigurer();
    }

    protected void konfigurerMiljø() throws Exception {
        System.setProperty("develop-local", "true");

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
        // truststore avgjør hva vi stoler på av sertifikater når vi gjør utadgående TLS kall
        initCryptoStoreConfig();
        super.konfigurerSikkerhet();
    }

    @SuppressWarnings("resource")
    @Override
    protected List<Connector> createConnectors(JettyWebKonfigurasjon jettyWebKonfigurasjon, Server server) {
        var connectors = super.createConnectors(jettyWebKonfigurasjon, server);
        var https = createHttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        return connectors;
    }

    private static void initCryptoStoreConfig() {
        var defaultLocation = ENV.getProperty("user.home", ".") + "/.modig/truststore.jks";

        var storePath = ENV.getProperty(TRUSTSTORE_PATH_PROP, defaultLocation);
        var storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException("Finner ikke truststore i " + storePath
                    + "\n\tKonfigurer enten som System property '" + TRUSTSTORE_PATH_PROP + "' eller environment variabel '"
                    + TRUSTSTORE_PATH_PROP.toUpperCase().replace('.', '_') + "'");
        }
        var password = ENV.getProperty(TRUSTSTORE_PASSW_PROP, "changeit");
        if(password == null) {
            throw new IllegalStateException("Passord for å aksessere store truststore i " + storePath + " er null");
        }

        System.setProperty(TRUSTSTORE_PATH_PROP, storeFile.getAbsolutePath());
        System.setProperty(TRUSTSTORE_PASSW_PROP, password);
    }
}
