package no.nav.foreldrepenger.fpformidling.kafkatjenester.historikk.task;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.fpformidling.historikk.HistorikkRepository;
import no.nav.foreldrepenger.fpformidling.kafkatjenester.historikk.KvitteringTjeneste;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask("formidling.publiserHistorikk")
public class PubliserHistorikkTask implements ProsessTaskHandler {

    @Deprecated(forRemoval = true)
    public static final String HISTORIKK_ID = "historikkId";
    public static final String BESTILLING_UUID = "bestillingUuid";
    public static final String DOKUMENT_MAL_TYPE = "dokumentMalType";
    public static final String JOURNALPOST_ID = "journalpostId";
    public static final String DOKUMENT_ID = "dokumentId";

    private HistorikkRepository historikkRepository;
    private KvitteringTjeneste kvitteringTjeneste;

    public PubliserHistorikkTask() {
        //CDI
    }

    @Inject
    public PubliserHistorikkTask(HistorikkRepository historikkRepository,
                                 KvitteringTjeneste kvitteringTjeneste) {
        this.historikkRepository = historikkRepository;
        this.kvitteringTjeneste = kvitteringTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var historikkIdOpt = Optional.ofNullable(prosessTaskData.getPropertyValue(PubliserHistorikkTask.HISTORIKK_ID));

        if (historikkIdOpt.isPresent()) {
            sendFromHistorikkWithId(historikkIdOpt.map(Long::parseLong).orElseThrow());
        } else {
            var kvittering = new DokumentProdusertDto(
                    prosessTaskData.getBehandlingUuid(),
                    UUID.fromString(prosessTaskData.getPropertyValue(BESTILLING_UUID)),
                    prosessTaskData.getPropertyValue(DOKUMENT_MAL_TYPE),
                    prosessTaskData.getPropertyValue(JOURNALPOST_ID),
                    prosessTaskData.getPropertyValue(DOKUMENT_ID));
            kvitteringTjeneste.sendKvittering(kvittering);
        }
    }

    private void sendFromHistorikkWithId(Long historikkId) {
        DokumentHistorikkinnslag historikkinnslag = historikkRepository.hent(historikkId);
        kvitteringTjeneste.publiserHistorikk(historikkinnslag);
    }
}
