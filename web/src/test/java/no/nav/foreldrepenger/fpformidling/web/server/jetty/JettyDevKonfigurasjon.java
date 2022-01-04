package no.nav.foreldrepenger.fpformidling.web.server.jetty;

public class JettyDevKonfigurasjon extends JettyWebKonfigurasjon {
    private static final int SSL_SERVER_PORT = 8343;
    private static final int DEV_SERVER_PORT = 8010;

    public JettyDevKonfigurasjon() {
        super(DEV_SERVER_PORT);
    }

    @Override
    public int getSslPort() {
        return SSL_SERVER_PORT;
    }

}
