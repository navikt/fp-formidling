package no.nav.foreldrepenger.melding.sikkerhet;

import static no.nav.foreldrepenger.melding.sikkerhet.FileUtils.putInTempFile;

import java.io.InputStream;

public final class TestSertifikater {

    public static void setup(String keyStorePassword, String trustStorePassword) {
        TestSertifikater.setupTemporaryTrustStore("truststore.jts", trustStorePassword);
        TestSertifikater.setupTemporaryKeyStore("keystore.jks", keyStorePassword);
    }

    /**
     * sett opp key store for testing
     */
    private static void setupTemporaryKeyStore(String keyStoreResourceName, String password) {
        InputStream keyStore = TestSertifikater.class.getClassLoader().getResourceAsStream(keyStoreResourceName);
        setupTemporaryKeyStore(keyStore, password);
    }

    /**
     * sett opp key store for testing
     */
    private static void setupTemporaryKeyStore(InputStream keystore, String password) {
        new KeyStore(putInTempFile(keystore).getAbsolutePath(), password).setOn(System.getProperties());
    }

    /**
     * sett opp trust store for testing
     */
    private static void setupTemporaryTrustStore(String trustStoreResourceName, String password) {
        InputStream trustStore = TestSertifikater.class.getClassLoader().getResourceAsStream(trustStoreResourceName);
        setupTemporaryTrustStore(trustStore, password);
    }

    /**
     * sett opp trust store for testing
     */
    private static void setupTemporaryTrustStore(InputStream trustStore, String password) {
        new TrustStore(putInTempFile(trustStore).getAbsolutePath(), password).setOn(System.getProperties());
    }

    private TestSertifikater() {
    }

}
