package no.nav.foreldrepenger.melding.brevbestiller.task;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.brevbestiller.impl.BrevBestillerTjeneste;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask("formidling.bestillBrev")
public class ProduserBrevTask implements ProsessTaskHandler {

    private BrevBestillerTjeneste brevBestillerApplikasjonTjeneste;
    private HendelseRepository hendelseRepository;

    public ProduserBrevTask() {
        //CDI
    }

    @Inject
    public ProduserBrevTask(BrevBestillerTjeneste brevBestillerApplikasjonTjeneste,
                            HendelseRepository hendelseRepository) {
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
        this.hendelseRepository = hendelseRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        long hendelseId = Long.parseLong(prosessTaskData.getPropertyValue(BrevTaskProperties.HENDELSE_ID));
        brevBestillerApplikasjonTjeneste.bestillBrev(hendelseRepository.hentDokumentHendelseMedId(hendelseId));
    }
}
