package no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.sikkerhet.loginmodule.ContainerLogin;
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
        ContainerLogin containerLogin = new ContainerLogin();
        containerLogin.login();
        MDCOperations.putCallId();
        DokumentHendelse hendelse = hendelseFraDto(jsonHendelse);

        hendelseRepository.lagre(hendelse);
        log.info("lagret hendelse:{} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingId());
        dokumentHistorikkTjeneste.lagreOgPubliserHistorikk(brevBestillerApplikasjonTjeneste.bestillBrev(hendelse));
        containerLogin.logout();
    }

    private DokumentHendelse hendelseFraDto(DokumentHendelseDto jsonHendelse) {
        //TODO HISTORIKKAKTØR OG FLERE FELTER
        return new DokumentHendelse.Builder()
                .medBehandlingId(jsonHendelse.getBehandlingId())
                .medYtelseType(utledYtelseType(jsonHendelse.getYtelseType()))
                .medBehandlingType(utledBehandlingType(jsonHendelse.getBehandlingType()))
                .medFritekst(jsonHendelse.getFritekst())
                .medTittel(jsonHendelse.getTittel())
                .medDokumentMalType(utleddokumentMalType(jsonHendelse.getDokumentMal()))
                .build();
    }

    private BehandlingType utledBehandlingType(String behandlingType) {
        if (StringUtils.nullOrEmpty(behandlingType)) {
            return null;
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
