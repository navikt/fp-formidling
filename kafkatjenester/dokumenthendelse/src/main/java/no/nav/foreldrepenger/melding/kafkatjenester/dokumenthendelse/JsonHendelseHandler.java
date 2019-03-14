package no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.task.ProduserBrevTaskProperties;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class JsonHendelseHandler {

    private static final Logger log = LoggerFactory.getLogger(JsonHendelseHandler.class);

    private HendelseRepository hendelseRepository;
    private DokumentRepository dokumentRepository;
    private KodeverkRepository kodeverkRepository;
    private ProsessTaskRepository prosessTaskRepository;
//    private DokumentHistorikkTjeneste dokumentHistorikkTjeneste;
//    private BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste;

    public JsonHendelseHandler() {
        //CDI
    }

    @Inject
    public JsonHendelseHandler(HendelseRepository hendelseRepository,
                               DokumentRepository dokumentRepository,
                               KodeverkRepository kodeverkRepository,
//                               DokumentHistorikkTjeneste dokumentHistorikkTjeneste,
//                               BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste,
                               ProsessTaskRepository prosessTaskRepository) {
        this.hendelseRepository = hendelseRepository;
        this.dokumentRepository = dokumentRepository;
        this.kodeverkRepository = kodeverkRepository;
        this.prosessTaskRepository = prosessTaskRepository;
//        this.dokumentHistorikkTjeneste = dokumentHistorikkTjeneste;
//        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
    }

    public void prosesser(DokumentHendelseDto jsonHendelse) {
        DokumentHendelse hendelse = hendelseFraDto(jsonHendelse);
        hendelseRepository.lagre(hendelse);
        opprettBestillBrevTask(hendelse);
        log.info("lagret hendelse:{} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingId());
        //TODO (aleksander)
        //dokumentHistorikkTjeneste.publiserHistorikk(brevBestillerApplikasjonTjeneste.bestillBrev(hendelse));
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(ProduserBrevTaskProperties.TASKTYPE);
        prosessTaskData.setProperty(ProduserBrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        //TODO (aleksander)
        prosessTaskData.setGruppe("FORMIDLING");
        prosessTaskRepository.lagre(prosessTaskData);
    }

    private DokumentHendelse hendelseFraDto(DokumentHendelseDto jsonHendelse) {
        //TODO HISTORIKKAKTØR OG FLERE FELTER
        return new DokumentHendelse.Builder()
                .medBehandlingId(jsonHendelse.getBehandlingId())
                .medYtelseType(utledYtelseType(jsonHendelse.getYtelseType()))
                .medBehandlingType(utledBehandlingType(jsonHendelse.getBehandlingType()))
                .medFritekst(jsonHendelse.getFritekst())
                .medTittel(jsonHendelse.getTittel())
                .medHistorikkAktør(utledHistorikkAktør(jsonHendelse.getHistorikkAktør()))
                .medDokumentMalType(utleddokumentMalType(jsonHendelse.getDokumentMal()))
                .medGjelderVedtak(jsonHendelse.isGjelderVedtak())
                .build();
    }

    private HistorikkAktør utledHistorikkAktør(String historikkAktør) {
        if (StringUtils.nullOrEmpty(historikkAktør)) {
            return HistorikkAktør.UDEFINERT;
        }
        return kodeverkRepository.finn(HistorikkAktør.class, historikkAktør);
    }

    private BehandlingType utledBehandlingType(String behandlingType) {
        if (StringUtils.nullOrEmpty(behandlingType)) {
            return BehandlingType.UDEFINERT;
        }
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }

    private FagsakYtelseType utledYtelseType(String ytelseType) {
        if (StringUtils.nullOrEmpty(ytelseType)) {
            return null;
        }
        return kodeverkRepository.finn(FagsakYtelseType.class, ytelseType);
    }

    private DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (StringUtils.nullOrEmpty(dokumentmal)) {
            return null;
        }
        return dokumentRepository.hentDokumentMalType(dokumentmal);
    }

}
