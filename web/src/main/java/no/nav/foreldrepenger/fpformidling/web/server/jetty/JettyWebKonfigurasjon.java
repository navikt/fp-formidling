package no.nav.foreldrepenger.fpformidling.web.server.jetty;

public class JettyWebKonfigurasjon {

    private static final String CONTEXT_PATH = "/fpformidling";

    private Integer serverPort;

    public JettyWebKonfigurasjon(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getContextPath() {
        return CONTEXT_PATH;
    }
}
