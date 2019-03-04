package no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentHendelse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHistorikkDto;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class JsonHendelseHandler {

    private static final Logger log = LoggerFactory.getLogger(JsonHendelseHandler.class);

    private DokumentRepository dokumentRepository;
    private KodeverkRepository kodeverkRepository;
    private DokumentHistorikkTjeneste dokumentHistorikkTjeneste;

    public JsonHendelseHandler() {
        //CDI
    }

    @Inject
    public JsonHendelseHandler(DokumentRepository dokumentRepository,
                               KodeverkRepository kodeverkRepository,
                               DokumentHistorikkTjeneste dokumentHistorikkTjeneste) {
        this.dokumentRepository = dokumentRepository;
        this.kodeverkRepository = kodeverkRepository;
        this.dokumentHistorikkTjeneste = dokumentHistorikkTjeneste;

    }

    public void prosesser(DokumentHendelseDto jsonHendelse) {
        DokumentHendelse hendelse = new DokumentHendelse.Builder()
                .medBehandlingId(jsonHendelse.getBehandlingId())
                .medBehandlingType(utledBehandlingType(jsonHendelse.getBehandlingType()))
                .medFritekst(jsonHendelse.getFritekst())
                .medTittel(jsonHendelse.getTittel())
                .medDokumentMalType(utleddokumentMalType(jsonHendelse.getDokumentMal()))
                .build();

        dokumentRepository.lagre(hendelse);
        log.info("Prossessert og lagret hendelse: behandling: {} OK", jsonHendelse.getBehandlingId());
        lagHistorikk(hendelse);
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
