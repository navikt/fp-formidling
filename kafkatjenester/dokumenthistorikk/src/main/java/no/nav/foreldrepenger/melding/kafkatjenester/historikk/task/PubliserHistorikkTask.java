package no.nav.foreldrepenger.melding.kafkatjenester.historikk.task;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask("formidling.publiserHistorikk")
public class PubliserHistorikkTask implements ProsessTaskHandler {

    public static final String HISTORIKK_ID = "historikkId";

    private HistorikkRepository historikkRepository;
    private DokumentHistorikkTjeneste dokumentHistorikkTjeneste;

    public PubliserHistorikkTask() {
        //CDI
    }

    @Inject
    public PubliserHistorikkTask(HistorikkRepository historikkRepository,
                                 DokumentHistorikkTjeneste dokumentHistorikkTjeneste) {
        this.historikkRepository = historikkRepository;
        this.dokumentHistorikkTjeneste = dokumentHistorikkTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        long historikkId = Long.parseLong(prosessTaskData.getPropertyValue(PubliserHistorikkTask.HISTORIKK_ID));
        DokumentHistorikkinnslag historikkinnslag = historikkRepository.hentInnslagMedId(historikkId);
        dokumentHistorikkTjeneste.publiserHistorikk(historikkinnslag);
    }
}
