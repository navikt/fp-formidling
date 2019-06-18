package no.nav.vedtak.felles.prosesstask.batch;

import java.time.LocalDateTime;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.apptjeneste.AppServiceHandler;
import no.nav.vedtak.felles.AktiverContextOgTransaksjon;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskEntitet;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskType;
import no.nav.vedtak.felles.prosesstask.impl.cron.CronExpression;

/**
 * Starter tasks med cron-expression hvis disse ikke har noen status fra før av.
 */
@ApplicationScoped
@AktiverContextOgTransaksjon
public class BatchTaskSchedueler implements AppServiceHandler {

    private ProsessTaskRepository taskRepository;

    BatchTaskSchedueler() {
    }

    @Inject
    public BatchTaskSchedueler(ProsessTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void start() {
        Map<ProsessTaskType, ProsessTaskEntitet> statusForBatchTasks = taskRepository.finnStatusForBatchTasks();
        statusForBatchTasks.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == null)
                .forEach(this::opprettTaskForType);
    }

    private void opprettTaskForType(Map.Entry<ProsessTaskType, ProsessTaskEntitet> entry) {
        ProsessTaskType type = entry.getKey();
        ProsessTaskData data = new ProsessTaskData(type.getKode());
        LocalDateTime neste = new CronExpression(type.getCronExpression()).neste(LocalDateTime.now());
        data.setNesteKjøringEtter(neste);
        taskRepository.lagre(data);
    }

    @Override
    public void stop() {

    }
}
