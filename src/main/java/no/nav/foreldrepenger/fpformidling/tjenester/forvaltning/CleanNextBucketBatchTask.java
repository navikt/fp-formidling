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
@ProsessTask(value = "partition.cleanBucket", prioritet = 3, cronExpression = "0 0 0 1 * *", maxFailedRuns = 1)
public class CleanNextBucketBatchTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CleanNextBucketBatchTask.class);
    private final ProsessTaskTjeneste taskTjeneste;

    @Inject
    public CleanNextBucketBatchTask(ProsessTaskTjeneste taskTjeneste) {
        this.taskTjeneste = taskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var antallSlettet = taskTjeneste.tømNestePartisjon();
        LOG.info("Tømmer neste partisjon med ferdige tasks, slettet {}", antallSlettet);
    }
}
