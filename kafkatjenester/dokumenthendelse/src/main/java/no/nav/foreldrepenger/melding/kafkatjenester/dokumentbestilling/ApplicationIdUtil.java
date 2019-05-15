package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.UUID;

public final class ApplicationIdUtil {

    private ApplicationIdUtil() {
    }

    public static String get() {
        String hostname;
        try {
            hostname = System.getenv("hostname"); // settes på pod i cluster (pod identifikator)
            if (hostname == null) {
                hostname = Inet4Address.getLocalHost().getHostName();
            }
        } catch (NullPointerException | SecurityException | UnknownHostException e) {
            hostname = System.getProperty("application.name", "java-application") + "-" + UUID.randomUUID();
        }
        return hostname;
    }
}
