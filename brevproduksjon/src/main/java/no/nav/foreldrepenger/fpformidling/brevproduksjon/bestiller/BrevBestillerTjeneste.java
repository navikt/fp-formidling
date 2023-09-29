package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class BrevBestillerTjeneste {

    private DokumentMalUtleder dokumentMalUtleder;
    private DomeneobjektProvider domeneobjektProvider;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;

    BrevBestillerTjeneste() {
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
        var behandling = hentBehandling(dokumentHendelse);
        var dokumentMal = utledDokumentMal(behandling, dokumentHendelse);
        return dokgenBrevproduksjonTjeneste.forhåndsvisBrev(dokumentHendelse, behandling, dokumentMal);
    }

    public void bestillBrev(DokumentHendelse dokumentHendelse) {
        var behandling = hentBehandling(dokumentHendelse);
        var dokumentMal = utledDokumentMal(behandling, dokumentHendelse);
        var dokumentType = utledDokumentType(dokumentHendelse.getYtelseType(), behandling, dokumentMal);
        dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal, dokumentType);
    }

    private DokumentMalType utledDokumentType(FagsakYtelseType ytelseType, Behandling behandling, DokumentMalType dokumentMal) {
        if (DokumentMalType.FRITEKSTBREV.equals(dokumentMal)) {
            return dokumentMalUtleder.utledDokumentType(behandling, ytelseType, true);
        }
        return dokumentMal;
    }

    private Behandling hentBehandling(DokumentHendelse dokumentHendelse) {
        return domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
    }

    private DokumentMalType utledDokumentMal(Behandling behandling, DokumentHendelse dokumentHendelse) {
        return dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);
    }
}
