package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.FpsakRestKlient;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Saksnummer;
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

    private FpsakRestKlient fpsakRestKlient;

    SendKvitteringTask() {
        //CDI
    }

    @Inject
    public SendKvitteringTask(FpsakRestKlient fpsakRestKlient) {
        this.fpsakRestKlient = fpsakRestKlient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        fpsakRestKlient.kvitterDokument(new DokumentKvitteringDto(prosessTaskData.getBehandlingUuid(),
            new Saksnummer(prosessTaskData.getSaksnummer()),
            UUID.fromString(prosessTaskData.getPropertyValue(BESTILLING_UUID)),
            prosessTaskData.getPropertyValue(JOURNALPOST_ID),
            prosessTaskData.getPropertyValue(DOKUMENT_ID)));
    }
}
