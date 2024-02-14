package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.BestillingType;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
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
        var behandling = hentBehandling(dokumentHendelse.getBehandlingUuid());
        var dokumentMal = utledDokumentMal(behandling, dokumentHendelse);
        return dokgenBrevproduksjonTjeneste.forhåndsvisBrev(dokumentHendelse, behandling, dokumentMal);
    }

    public void bestillBrev(DokumentHendelse dokumentHendelse) {
        var behandling = hentBehandling(dokumentHendelse.getBehandlingUuid());
        var dokumentMal = utledDokumentMal(behandling, dokumentHendelse);
        var dokumentType = utledDokumentType(behandling.getFagsakBackend().getYtelseType(), behandling, dokumentMal);
        dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal, dokumentType);
    }

    public String genererJson(UUID behandlingUuid){
        var behandling = hentBehandling(behandlingUuid);
        var dokumentHendelse = DokumentHendelse.builder()
            .medBestillingUuid(UUID.randomUUID())
            .medBehandlingUuid(behandlingUuid)
            .medGjelderVedtak(Boolean.TRUE).build();
        var dokumentMal = utledDokumentMal(behandling, dokumentHendelse);
        return  dokgenBrevproduksjonTjeneste.genererJson(dokumentHendelse, behandling, dokumentMal, BestillingType.UTKAST );
    }

    private DokumentMalType utledDokumentType(FagsakYtelseType ytelseType, Behandling behandling, DokumentMalType dokumentMal) {
        if (DokumentMalType.FRITEKSTBREV.equals(dokumentMal)) {
            return dokumentMalUtleder.utledDokumentType(behandling, ytelseType, true);
        }
        return dokumentMal;
    }

    private Behandling hentBehandling(UUID behandlingUuid) {
        var behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        domeneobjektProvider.hentFagsakBackend(behandling);
        return behandling;
    }

    private DokumentMalType utledDokumentMal(Behandling behandling, DokumentHendelse dokumentHendelse) {
        return dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);
    }
}
