package no.nav.foreldrepenger.melding.hendelse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.brevbestiller.task.BrevTaskProperties;
import no.nav.foreldrepenger.melding.brevbestiller.task.ProduserBrevTask;
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
        if (hendelseRepository.finnesHendelseMedUuidAllerede(hendelse.getBestillingUuid())) {
            LOGGER.info("Lagrer ikke hendelse med duplikat bestillingUuid:{} for behandling: {} OK", hendelse.getBestillingUuid(), hendelse.getBehandlingUuid());
            return;
        }
        hendelseRepository.lagre(hendelse);
        opprettBestillBrevTask(hendelse);
        LOGGER.info("lagret hendelse:{} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingUuid());
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(ProduserBrevTask.TASKTYPE);
        prosessTaskData.setProperty(BrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(dokumentHendelse.getBehandlingUuid()));
        prosessTaskData.setGruppe("FORMIDLING");
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
