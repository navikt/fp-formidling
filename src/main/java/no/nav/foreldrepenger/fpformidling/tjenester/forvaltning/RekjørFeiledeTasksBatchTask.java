package no.nav.foreldrepenger.fpformidling.tjenester.forvaltning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ApplicationScoped

@ProsessTask(value = "retry.feilendeTasks", prioritet = 2, cronExpression = "0 30 6 * * *", maxFailedRuns = 1)
public class RekjørFeiledeTasksBatchTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RekjørFeiledeTasksBatchTask.class);
    private final ProsessTaskTjeneste taskTjeneste;

    @Inject
    public RekjørFeiledeTasksBatchTask(ProsessTaskTjeneste taskTjeneste) {
        this.taskTjeneste = taskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var rekjørAlleFeiledeTasks = taskTjeneste.restartAlleFeiledeTasks();
        LOG.info("Rekjører alle feilende tasks, oppdaterte {} tasks", rekjørAlleFeiledeTasks);
    }
}
