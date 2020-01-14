package no.nav.foreldrepenger.melding.web.server.jetty;

public class JettyWebKonfigurasjon implements AppKonfigurasjon {
    private static final String CONTEXT_PATH = "/fpformidling";
    private static final String SWAGGER_HASH = "sha256-TxvXP6LyJEmi4uLFpxv96Wc8HJ28MOJtMXV+/vEae4g=";

    private Integer serverPort;

    public JettyWebKonfigurasjon() {
    }

    public JettyWebKonfigurasjon(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public int getServerPort() {
        if (serverPort == null) {
            return AppKonfigurasjon.DEFAULT_SERVER_PORT;
        }
        return serverPort;
    }

    @Override
    public String getContextPath() {
        return CONTEXT_PATH;
    }

    @Override
    public int getSslPort() {
        throw new IllegalStateException("SSL port should only be used locally");
    }

    @Override
    public String getSwaggerHash() {
        return SWAGGER_HASH;
    }
}
