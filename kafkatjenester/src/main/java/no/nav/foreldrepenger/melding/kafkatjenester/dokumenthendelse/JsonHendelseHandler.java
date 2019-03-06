package no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHistorikkDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class JsonHendelseHandler {

    private static final Logger log = LoggerFactory.getLogger(JsonHendelseHandler.class);

    private HendelseRepository hendelseRepository;
    private DokumentRepository dokumentRepository;
    private KodeverkRepository kodeverkRepository;
    private DokumentHistorikkTjeneste dokumentHistorikkTjeneste;
    private BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste;

    public JsonHendelseHandler() {
        //CDI
    }

    @Inject
    public JsonHendelseHandler(HendelseRepository hendelseRepository,
                               DokumentRepository dokumentRepository,
                               KodeverkRepository kodeverkRepository,
                               DokumentHistorikkTjeneste dokumentHistorikkTjeneste,
                               BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste) {
        this.hendelseRepository = hendelseRepository;
        this.dokumentRepository = dokumentRepository;
        this.kodeverkRepository = kodeverkRepository;
        this.dokumentHistorikkTjeneste = dokumentHistorikkTjeneste;
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;

    }

    public void prosesser(DokumentHendelseDto jsonHendelse) {
        DokumentHendelse hendelse = hendelseFraDto(jsonHendelse);

        hendelseRepository.lagre(hendelse);
        log.info("Prossessert og lagret hendelse: behandling: {} OK", jsonHendelse.getBehandlingId());
        brevBestillerApplikasjonTjeneste.bestillBrev(hendelse);
        //TODO ta output fra bestillbrev og push det til historikk
        lagHistorikk(hendelse);
    }

    private DokumentHendelse hendelseFraDto(DokumentHendelseDto jsonHendelse) {
        return new DokumentHendelse.Builder()
                .medBehandlingId(jsonHendelse.getBehandlingId())
                .medBehandlingType(utledBehandlingType(jsonHendelse.getBehandlingType()))
                .medFritekst(jsonHendelse.getFritekst())
                .medTittel(jsonHendelse.getTittel())
                .medDokumentMalType(utleddokumentMalType(jsonHendelse.getDokumentMal()))
                .build();
    }

    private void lagHistorikk(DokumentHendelse hendelse) {
        DokumentHistorikkDto historikk = new DokumentHistorikkDto();
        historikk.setBehandlingId(hendelse.getBehandlingId());
        dokumentHistorikkTjeneste.publiserHistorikk(historikk);
    }

    private BehandlingType utledBehandlingType(String behandlingType) {
        if (StringUtils.nullOrEmpty(behandlingType)) {
            return null;
        }
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }

    private DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (StringUtils.nullOrEmpty(dokumentmal)) {
            return null;
        }
        return dokumentRepository.hentDokumentMalType(dokumentmal);
    }

}
