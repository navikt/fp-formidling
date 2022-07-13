package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.BrevBestillerTjeneste;
import no.nav.foreldrepenger.fpformidling.tjenester.DokumentHendelseTjeneste;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask("formidling.bestillBrev")
public class ProduserBrevTask implements ProsessTaskHandler {

    private BrevBestillerTjeneste brevBestillerApplikasjonTjeneste;
    private DokumentHendelseTjeneste dokumentHendelseTjeneste;

    @Inject
    public ProduserBrevTask(BrevBestillerTjeneste brevBestillerApplikasjonTjeneste,
                            DokumentHendelseTjeneste dokumentHendelseTjeneste) {
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        long hendelseId = Long.parseLong(prosessTaskData.getPropertyValue(BrevTaskProperties.HENDELSE_ID));
        brevBestillerApplikasjonTjeneste.bestillBrev(dokumentHendelseTjeneste.hentHendelse(hendelseId).orElseThrow());
    }
}
