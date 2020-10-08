package no.nav.foreldrepenger.melding.poststed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(SynchKodeverkBatchTask.TASKTYPE)
public class SynchKodeverkBatchTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "kodeverk.synch";

    private PostnummerSynkroniseringTjeneste postnummerTjeneste;

    @Inject
    public SynchKodeverkBatchTask(PostnummerSynkroniseringTjeneste postnummerTjeneste) {
        this.postnummerTjeneste = postnummerTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        postnummerTjeneste.synkroniserPostnummer();
    }
}
