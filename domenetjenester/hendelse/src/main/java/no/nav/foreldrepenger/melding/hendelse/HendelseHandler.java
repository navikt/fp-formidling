package no.nav.foreldrepenger.melding.hendelse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.brevbestiller.task.ProduserBrevTaskProperties;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

@ApplicationScoped
public class HendelseHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HendelseHandler.class);
    private HendelseRepository hendelseRepository;
    private ProsessTaskRepository prosessTaskRepository;

    public HendelseHandler() {
        //CDI
    }

    @Inject
    public HendelseHandler(HendelseRepository hendelseRepository,
                           ProsessTaskRepository prosessTaskRepository) {
        this.hendelseRepository = hendelseRepository;
        this.prosessTaskRepository = prosessTaskRepository;
    }

    public void prosesser(DokumentHendelse hendelse) {
        hendelseRepository.lagre(hendelse);
        opprettBestillBrevTask(hendelse);
        LOGGER.info("lagret hendelse:{} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingUuid());
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(ProduserBrevTaskProperties.TASKTYPE);
        prosessTaskData.setProperty(ProduserBrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setGruppe("FORMIDLING");
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
