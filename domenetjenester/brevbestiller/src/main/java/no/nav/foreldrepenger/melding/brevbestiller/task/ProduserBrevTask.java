package no.nav.foreldrepenger.melding.brevbestiller.task;

import static no.nav.foreldrepenger.melding.brevbestiller.task.ProduserBrevTask.TASKTYPE;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.brevbestiller.impl.BrevBestillerApplikasjonTjenesteImpl;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.task.PubliserHistorikkTaskProperties;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

@ApplicationScoped
@ProsessTask(TASKTYPE)
public class ProduserBrevTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "formidling.bestillBrev";

    private BrevBestillerApplikasjonTjenesteImpl brevBestillerApplikasjonTjeneste;
    private HendelseRepository hendelseRepository;
    private HistorikkRepository historikkRepository;
    private ProsessTaskRepository prosessTaskRepository;

    public ProduserBrevTask() {
        //CDI
    }

    @Inject
    public ProduserBrevTask(BrevBestillerApplikasjonTjenesteImpl brevBestillerApplikasjonTjeneste,
                            HendelseRepository hendelseRepository,
                            HistorikkRepository historikkRepository,
                            ProsessTaskRepository prosessTaskRepository) {
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
        this.hendelseRepository = hendelseRepository;
        this.historikkRepository = historikkRepository;
        this.prosessTaskRepository = prosessTaskRepository;
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
        ProsessTaskData prosessTaskData = new ProsessTaskData(PubliserHistorikkTaskProperties.TASKTYPE);
        prosessTaskData.setProperty(PubliserHistorikkTaskProperties.HISTORIKK_ID, String.valueOf(historikkinnslag.getId()));
        prosessTaskData.setGruppe("FORMIDLING");
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
