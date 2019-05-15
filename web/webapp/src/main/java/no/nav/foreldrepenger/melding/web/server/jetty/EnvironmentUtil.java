package no.nav.foreldrepenger.melding.web.server.jetty;

import no.nav.foreldrepenger.melding.web.server.jetty.db.EnvironmentClass;
import no.nav.vedtak.konfig.PropertyUtil;

public final class EnvironmentUtil {
    private EnvironmentUtil() {
    }

    public static EnvironmentClass getEnvironmentClass() {
        String cluster = PropertyUtil.getProperty("nais.cluster.name");
        if (cluster != null) {
            cluster = cluster.substring(0, cluster.indexOf("-")).toUpperCase();
            if ("DEV".equalsIgnoreCase(cluster)) {
                return EnvironmentClass.PREPROD;
            }
            return EnvironmentClass.valueOf(cluster);
        }
        return EnvironmentClass.PROD;
    }
}
