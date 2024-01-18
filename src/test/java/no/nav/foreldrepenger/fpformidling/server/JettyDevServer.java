package no.nav.foreldrepenger.fpformidling.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.Environment;

public class JettyDevServer extends JettyServer {

    private static final Environment ENV = Environment.current();
    private static final Logger log = LoggerFactory.getLogger(JettyDevServer.class);

    public static void main(String[] args) throws Exception {
        jettyServer(args).bootStrap();
    }

    static JettyDevServer jettyServer(String[] args) {
        if (args.length > 0) {
            return new JettyDevServer(Integer.parseUnsignedInt(args[0]));
        }
        return new JettyDevServer(ENV.getProperty("server.port", Integer.class, 8010));
    }

    private JettyDevServer(int serverPort) {
        super(serverPort);
    }
}
