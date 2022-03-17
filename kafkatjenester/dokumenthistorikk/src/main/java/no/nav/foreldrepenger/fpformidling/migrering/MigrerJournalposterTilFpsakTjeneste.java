package no.nav.foreldrepenger.fpformidling.migrering;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.historikk.task.MigrerHistorikkTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ApplicationScoped
public class MigrerJournalposterTilFpsakTjeneste {

    private ProsessTaskTjeneste taskTjeneste;

    public MigrerJournalposterTilFpsakTjeneste() {
        //CDI
    }

    @Inject
    public MigrerJournalposterTilFpsakTjeneste(ProsessTaskTjeneste taskTjeneste) {
        this.taskTjeneste = taskTjeneste;
    }

    public void migrerHistorikk() {
        opprettTask();
    }

    private void opprettTask() {
        var data = ProsessTaskData.forProsessTask(MigrerHistorikkTask.class);
        taskTjeneste.lagre(data);
    }

}
