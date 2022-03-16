package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

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

    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse) {
        Behandling behandling = hentBehandling(dokumentHendelse);
        DokumentMalType dokumentMal = utledDokumentMal(behandling, dokumentHendelse);
        return dokgenBrevproduksjonTjeneste.forhåndsvisBrev(dokumentHendelse, behandling, dokumentMal);
    }

    public void bestillBrev(DokumentHendelse dokumentHendelse) {
        Behandling behandling = hentBehandling(dokumentHendelse);
        DokumentMalType dokumentMal = utledDokumentMal(behandling, dokumentHendelse);
        dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal);
    }

    private Behandling hentBehandling(DokumentHendelse dokumentHendelse) {
        return domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
    }

    private DokumentMalType utledDokumentMal(Behandling behandling, DokumentHendelse dokumentHendelse) {
        return dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);
    }
}
