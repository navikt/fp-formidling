package no.nav.foreldrepenger.melding.brevbestiller.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.dto.DokumentHendelseMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class BrevBestillerTjeneste {

    private DokumentMalUtleder dokumentMalUtleder;
    private DomeneobjektProvider domeneobjektProvider;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;

    public BrevBestillerTjeneste() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerTjeneste(DokumentMalUtleder dokumentMalUtleder,
                                 DomeneobjektProvider domeneobjektProvider,
                                 DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste) {
        this.dokumentMalUtleder = dokumentMalUtleder;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokgenBrevproduksjonTjeneste = dokgenBrevproduksjonTjeneste;
    }

    @Transactional
    public byte[] forhandsvisBrev(DokumentbestillingDto dokumentbestillingDto) {
        DokumentHendelse dokumentHendelse = DokumentHendelseMapper.mapFra(dokumentbestillingDto);
        Behandling behandling = domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);
        return dokgenBrevproduksjonTjeneste.forhandsvisBrev(dokumentHendelse, behandling, dokumentMal);
    }

    @Transactional
    public void bestillBrev(DokumentHendelse dokumentHendelse) {
        Behandling behandling = domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);
        dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal);
    }
}
