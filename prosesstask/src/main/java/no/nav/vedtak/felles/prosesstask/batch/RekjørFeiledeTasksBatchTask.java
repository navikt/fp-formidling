package no.nav.vedtak.felles.prosesstask.batch;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

@ApplicationScoped
@ProsessTask(RekjørFeiledeTasksBatchTask.TASKTYPE)
public class RekjørFeiledeTasksBatchTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "retry.feilendeTasks";
    private static final Logger log = LoggerFactory.getLogger(RekjørFeiledeTasksBatchTask.class);
    private ProsessTaskRepository taskRepository;

    @Inject
    public RekjørFeiledeTasksBatchTask(ProsessTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        int rekjørAlleFeiledeTasks = taskRepository.rekjørAlleFeiledeTasks();
        log.info("Rekjører alle feilende tasks, oppdaterte {} tasks", rekjørAlleFeiledeTasks);
    }
}
