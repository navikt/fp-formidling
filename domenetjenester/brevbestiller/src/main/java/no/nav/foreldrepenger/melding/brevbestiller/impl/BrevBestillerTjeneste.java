package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static no.nav.foreldrepenger.melding.brevbestiller.impl.DokgenLanseringTjeneste.malSkalBrukeDokgen;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.dto.DokumentHendelseMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class BrevBestillerTjeneste {

    private DokumentMalUtleder dokumentMalUtleder;
    private DomeneobjektProvider domeneobjektProvider;
    private DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;

    public BrevBestillerTjeneste() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerTjeneste(DokumentMalUtleder dokumentMalUtleder,
                                 DomeneobjektProvider domeneobjektProvider,
                                 DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste,
                                 DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste) {
        this.dokumentMalUtleder = dokumentMalUtleder;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokprodBrevproduksjonTjeneste = dokprodBrevproduksjonTjeneste;
        this.dokgenBrevproduksjonTjeneste = dokgenBrevproduksjonTjeneste;
    }

    public byte[] forhandsvisBrev(DokumentbestillingDto dokumentbestillingDto) {
        DokumentHendelse dokumentHendelse = DokumentHendelseMapper.mapFra(dokumentbestillingDto);
        Behandling behandling = domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);

        if (malSkalBrukeDokgen(dokumentMal)) {
            return dokgenBrevproduksjonTjeneste.forhandsvisBrev(dokumentHendelse, behandling, dokumentMal);
        } else {
            return dokprodBrevproduksjonTjeneste.forhandsvisBrev(dokumentHendelse, behandling, dokumentMal);
        }
    }

    public List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse) {
        Behandling behandling = domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);

        if (malSkalBrukeDokgen(dokumentMal)) {
            return dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal);
        } else {
            return dokprodBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal);
        }
    }
}
