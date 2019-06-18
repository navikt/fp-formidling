package no.nav.foreldrepenger.melding.sikkerhet;

import java.io.File;

public final class TestSertifikater {
    private TestSertifikater() {
    }

    /**
     * sett opp key store for testing
     */
    public static void setupTemporaryKeyStore(String keyStorePathProperty, String keyStorePasswordProperty) {
        // keystore genererer sertifikat og TLS for innkommende kall. Bruker standard prop hvis definert, ellers faller tilbake på modig props
        var keystoreProp = System.getProperty("javax.net.ssl.keyStore") != null ? "javax.net.ssl.keyStore" : keyStorePathProperty;
        var keystorePasswProp = System.getProperty("javax.net.ssl.keyStorePassword") != null ? "javax.net.ssl.keyStorePassword" : keyStorePasswordProperty;

        initCryptoStoreConfig("keystore", keystoreProp, keystorePasswProp, System.getProperty("keystore.password"));
    }

    /**
     * sett opp trust store for testing
     */
    public static void setupTemporaryTrustStore(String trustStorePathProperty, String trustStorePasswordProperty) {
        // truststore avgjør hva vi stoler på av sertifikater når vi gjør utadgående TLS kall
        initCryptoStoreConfig("truststore", trustStorePathProperty, trustStorePasswordProperty, System.getProperty("truststore.password"));
    }

    private static void initCryptoStoreConfig(String storeName, String storeProperty, String storePasswordProperty, String defaultPassword) {
        String defaultLocation = getProperty("user.home", ".") + "/.modig/" + storeName + ".jks";

        String storePath = getProperty(storeProperty, defaultLocation);
        File storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException("Finner ikke " + storeName + " i " + storePath
                    + "\n\tKonfigurer enten som System property \'" + storeProperty + "\' eller environment variabel \'"
                    + storeProperty.toUpperCase().replace('.', '_') + "\'");
        }
        String password = getProperty(storePasswordProperty, defaultPassword);
        if (password == null) {
            throw new IllegalStateException("Passord for å aksessere store " + storeName + " i " + storePath + " er null");
        }

        System.setProperty(storeProperty, storeFile.getAbsolutePath());
        System.setProperty(storePasswordProperty, password);
    }

    private static String getProperty(String key, String defaultValue) {
        String val = System.getProperty(key, defaultValue);
        if (val == null) {
            val = System.getenv(key.toUpperCase().replace('.', '_'));
            val = val == null ? defaultValue : val;
        }
        return val;
    }
}
