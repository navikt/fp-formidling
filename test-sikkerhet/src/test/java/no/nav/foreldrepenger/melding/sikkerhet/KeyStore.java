package no.nav.foreldrepenger.melding.sikkerhet;

import java.util.Properties;

class KeyStore extends PropertySetter {

    public KeyStore(String filePath, String password) {
        super(createKeyStoreProperties(filePath, password));
    }

    private static Properties createKeyStoreProperties(String filePath, String password) {
        Properties props = new Properties();
        props.setProperty("no.nav.modig.security.appcert.keystore", filePath);
        props.setProperty("no.nav.modig.security.appcert.password", password);
        return props;
    }

}
