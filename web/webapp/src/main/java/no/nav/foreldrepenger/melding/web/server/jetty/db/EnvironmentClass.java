package no.nav.foreldrepenger.melding.web.server.jetty.db;

public enum EnvironmentClass {
    LOCALHOST, PREPROD, PROD;

    public String mountPath() {
        return "postgresql/" + name().toLowerCase() + "-fss";
    }
}
