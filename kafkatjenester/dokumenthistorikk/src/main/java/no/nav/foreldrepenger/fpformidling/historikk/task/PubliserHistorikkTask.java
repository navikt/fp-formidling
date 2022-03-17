package no.nav.foreldrepenger.fpformidling.historikk.task;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.historikk.DokumentKvitteringTjeneste;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask("formidling.publiserHistorikk")
public class PubliserHistorikkTask implements ProsessTaskHandler {

    public static final String BESTILLING_UUID = "bestillingUuid";
    public static final String DOKUMENT_MAL_TYPE = "dokumentMalType";
    public static final String JOURNALPOST_ID = "journalpostId";
    public static final String DOKUMENT_ID = "dokumentId";

    private DokumentKvitteringTjeneste kvitteringTjeneste;

    public PubliserHistorikkTask() {
        //CDI
    }

    @Inject
    public PubliserHistorikkTask(DokumentKvitteringTjeneste kvitteringTjeneste) {
        this.kvitteringTjeneste = kvitteringTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var kvittering = new DokumentProdusertDto(
                prosessTaskData.getBehandlingUuid(),
                UUID.fromString(prosessTaskData.getPropertyValue(BESTILLING_UUID)),
                prosessTaskData.getPropertyValue(DOKUMENT_MAL_TYPE),
                prosessTaskData.getPropertyValue(JOURNALPOST_ID),
                prosessTaskData.getPropertyValue(DOKUMENT_ID));
        kvitteringTjeneste.sendKvittering(kvittering);
    }
}
