package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentBestillerTjeneste;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseTjeneste;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@ProsessTask("formidling.bestillBrev")
public class ProduserBrevTask implements ProsessTaskHandler {

    private final DokumentBestillerTjeneste brevBestillerApplikasjonTjeneste;
    private final DokumentHendelseTjeneste dokumentHendelseTjeneste;

    @Inject
    public ProduserBrevTask(DokumentBestillerTjeneste brevBestillerApplikasjonTjeneste, DokumentHendelseTjeneste dokumentHendelseTjeneste) {
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var hendelseId = Long.parseLong(prosessTaskData.getPropertyValue(BrevTaskProperties.HENDELSE_ID));
        brevBestillerApplikasjonTjeneste.bestillBrev(dokumentHendelseTjeneste.hentHendelse(hendelseId).orElseThrow());
    }
}
