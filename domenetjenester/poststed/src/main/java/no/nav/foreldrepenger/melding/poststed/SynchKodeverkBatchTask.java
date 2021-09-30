package no.nav.foreldrepenger.melding.poststed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "kodeverk.synch", cronExpression = "0 0 6 1 * *", maxFailedRuns = 1)
public class SynchKodeverkBatchTask implements ProsessTaskHandler {

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
