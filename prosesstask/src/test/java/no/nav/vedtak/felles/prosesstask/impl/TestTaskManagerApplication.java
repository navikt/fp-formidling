package no.nav.vedtak.felles.prosesstask.impl;

import java.io.IOException;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.lokal.dbstoette.DBConnectionProperties;
import no.nav.vedtak.felles.lokal.dbstoette.DatabaseStøtte;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskDispatcher;
import no.nav.vedtak.felles.prosesstask.dbstoette.Databaseskjemainitialisering;
import no.nav.vedtak.felles.prosesstask.dbstoette.DatasourceConfiguration;
import no.nav.vedtak.felles.testutilities.UnitTestConfiguration;
import no.nav.vedtak.felles.testutilities.cdi.WeldContext;

class TestTaskManagerApplication {
    private static final Logger log = LoggerFactory.getLogger(TestTaskManagerApplication.class);

    /**
     * Test method.
     */
    public static void main(String[] args) throws IOException {
        WeldContext.getInstance(); // kun for å initialisere CDI miljø

        UnitTestConfiguration.loadUnitTestProperties();
        Databaseskjemainitialisering.settOppSkjemaer();
        List<DBConnectionProperties> connectionProperties = DatasourceConfiguration.UNIT_TEST.get();
        DatabaseStøtte.settOppJndiForDefaultDataSource(connectionProperties);
        DatabaseStøtte.kjørMigreringFor(connectionProperties);

        ProsessTaskDispatcher taskDispatcher = (task) -> {
            log.info("*********** Processed: task=" + task.getTaskType()); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        };

        TaskManager taskManager = CDI.current().select(TaskManager.class).get();
        taskManager.setProsessTaskDispatcher(taskDispatcher);

        taskManager.start();
    }
}
