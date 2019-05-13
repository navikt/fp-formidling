package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.brevbestiller.task.ProduserBrevTaskProperties;
import no.nav.foreldrepenger.melding.dtomapper.DokumentHendelseDtoMapper;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

@ApplicationScoped
public class JsonHendelseHandler {

    private static final Logger log = LoggerFactory.getLogger(JsonHendelseHandler.class);

    private HendelseRepository hendelseRepository;
    private ProsessTaskRepository prosessTaskRepository;
    private DokumentHendelseDtoMapper dtoTilDomeneobjektMapper;

    public JsonHendelseHandler() {
        //CDI
    }

    @Inject
    public JsonHendelseHandler(HendelseRepository hendelseRepository,
                               ProsessTaskRepository prosessTaskRepository,
                               DokumentHendelseDtoMapper dtoTilDomeneobjektMapper) {
        this.hendelseRepository = hendelseRepository;
        this.prosessTaskRepository = prosessTaskRepository;
        this.dtoTilDomeneobjektMapper = dtoTilDomeneobjektMapper;
    }

    public void prosesser(DokumentbestillingV1 jsonHendelse) {
        DokumentHendelse hendelse = dtoTilDomeneobjektMapper.mapDokumentHendelseFraDtoForKafka(jsonHendelse);
        hendelseRepository.lagre(hendelse);
        opprettBestillBrevTask(hendelse);
        log.info("lagret hendelse:{} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingUuid());
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(ProduserBrevTaskProperties.TASKTYPE);
        prosessTaskData.setProperty(ProduserBrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setGruppe("FORMIDLING");
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
