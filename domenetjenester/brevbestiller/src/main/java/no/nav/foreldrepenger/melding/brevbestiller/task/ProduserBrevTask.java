package no.nav.foreldrepenger.melding.brevbestiller.task;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.brevbestiller.impl.BrevBestillerTjeneste;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.task.PubliserHistorikkTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ApplicationScoped
@ProsessTask("formidling.bestillBrev")
public class ProduserBrevTask implements ProsessTaskHandler {

    private BrevBestillerTjeneste brevBestillerApplikasjonTjeneste;
    private HendelseRepository hendelseRepository;
    private HistorikkRepository historikkRepository;
    private ProsessTaskTjeneste taskTjeneste;

    public ProduserBrevTask() {
        //CDI
    }

    @Inject
    public ProduserBrevTask(BrevBestillerTjeneste brevBestillerApplikasjonTjeneste,
                            HendelseRepository hendelseRepository,
                            HistorikkRepository historikkRepository,
                            ProsessTaskTjeneste taskTjeneste) {
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
        this.hendelseRepository = hendelseRepository;
        this.historikkRepository = historikkRepository;
        this.taskTjeneste = taskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        long hendelseId = Long.parseLong(prosessTaskData.getPropertyValue(BrevTaskProperties.HENDELSE_ID));
        List<DokumentHistorikkinnslag> historikkinnslagList = brevBestillerApplikasjonTjeneste.bestillBrev(hendelseRepository.hentDokumentHendelseMedId(hendelseId));
        for (DokumentHistorikkinnslag historikkinnslag : historikkinnslagList) {
            historikkRepository.lagre(historikkinnslag);
            opprettProsesstaskForHistorikkInnslag(historikkinnslag);
        }
    }

    private void opprettProsesstaskForHistorikkInnslag(DokumentHistorikkinnslag historikkinnslag) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(PubliserHistorikkTask.class);
        prosessTaskData.setProperty(PubliserHistorikkTask.HISTORIKK_ID, String.valueOf(historikkinnslag.getId()));
        prosessTaskData.setGruppe("FORMIDLING");
        taskTjeneste.lagre(prosessTaskData);
    }
}
