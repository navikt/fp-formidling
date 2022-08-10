package no.nav.foreldrepenger.fpformidling.web.server.jetty;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.web.server.jetty.db.DatasourceRole;
import no.nav.foreldrepenger.fpformidling.web.server.jetty.db.DatasourceUtil;
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

    @Override
    void migrerDatabaser() {
        try {
            super.migrerDatabaser();
        } catch (Exception e) {
            log.info("Migreringer feilet, cleaner og prøver på nytt for lokal db.");
            try (var migreringDs = DatasourceUtil.createDatasource(DatasourceRole.ADMIN, 1)) {
                var flyway = Flyway.configure()
                        .dataSource(migreringDs)
                        .locations("classpath:/db/migration/")
                        .baselineOnMigrate(true)
                        .load();
                try {
                    flyway.clean();
                } catch (FlywayException fwe) {
                    throw new IllegalStateException("Migrering feiler.", fwe);
                }
                super.migrerDatabaser();
            }
        }
    }
}
