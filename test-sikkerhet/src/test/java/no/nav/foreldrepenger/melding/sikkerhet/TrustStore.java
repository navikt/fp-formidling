package no.nav.foreldrepenger.melding.sikkerhet;

import java.util.Properties;

class TrustStore extends PropertySetter {

    TrustStore(String truststoreFilePath, String truststorePassword) {
        super(createTrustStoreProperties(truststoreFilePath, truststorePassword));
    }

    private static Properties createTrustStoreProperties(String truststoreFilePath, String truststorePassword) {
        Properties props = new Properties();
        props.setProperty("javax.net.ssl.trustStore", truststoreFilePath);
        props.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
        return props;
    }
}
