package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentKvitteringDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask("formidling.publiserHistorikk")
public class SendKvitteringTask implements ProsessTaskHandler {

    public static final String BESTILLING_UUID = "bestillingUuid";
    public static final String JOURNALPOST_ID = "journalpostId";
    public static final String DOKUMENT_ID = "dokumentId";

    private DokumentKvitteringTjeneste kvitteringTjeneste;

    public SendKvitteringTask() {
        //CDI
    }

    @Inject
    public SendKvitteringTask(DokumentKvitteringTjeneste kvitteringTjeneste) {
        this.kvitteringTjeneste = kvitteringTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var kvittering = new DokumentKvitteringDto(prosessTaskData.getBehandlingUuid(),
            UUID.fromString(prosessTaskData.getPropertyValue(BESTILLING_UUID)),
            prosessTaskData.getPropertyValue(JOURNALPOST_ID),
            prosessTaskData.getPropertyValue(DOKUMENT_ID));
        kvitteringTjeneste.sendKvittering(kvittering);
    }
}
