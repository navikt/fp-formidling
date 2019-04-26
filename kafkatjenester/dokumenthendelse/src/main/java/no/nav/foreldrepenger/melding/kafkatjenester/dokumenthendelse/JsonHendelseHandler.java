package no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.brevbestiller.task.ProduserBrevTaskProperties;
import no.nav.foreldrepenger.melding.dtomapper.DtoTilDomeneobjektMapper;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

@ApplicationScoped
public class JsonHendelseHandler {

    private static final Logger log = LoggerFactory.getLogger(JsonHendelseHandler.class);

    private HendelseRepository hendelseRepository;
    private ProsessTaskRepository prosessTaskRepository;
    private DtoTilDomeneobjektMapper dtoTilDomeneobjektMapper;

    public JsonHendelseHandler() {
        //CDI
    }

    @Inject
    public JsonHendelseHandler(HendelseRepository hendelseRepository,
                               ProsessTaskRepository prosessTaskRepository,
                               DtoTilDomeneobjektMapper dtoTilDomeneobjektMapper) {
        this.hendelseRepository = hendelseRepository;
        this.prosessTaskRepository = prosessTaskRepository;
        this.dtoTilDomeneobjektMapper = dtoTilDomeneobjektMapper;
    }

    public void prosesser(DokumentHendelseDto jsonHendelse) {
        DokumentHendelse hendelse = dtoTilDomeneobjektMapper.fraDto(jsonHendelse);
        hendelseRepository.lagre(hendelse);
        opprettBestillBrevTask(hendelse);
        log.info("lagret hendelse:{} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingId());
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(ProduserBrevTaskProperties.TASKTYPE);
        prosessTaskData.setProperty(ProduserBrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setGruppe("FORMIDLING");
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
