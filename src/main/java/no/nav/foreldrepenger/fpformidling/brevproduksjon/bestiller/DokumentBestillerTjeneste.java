package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.BestillingType;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum;

@ApplicationScoped
public class DokumentBestillerTjeneste {

    private DomeneobjektProvider domeneobjektProvider;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;

    DokumentBestillerTjeneste() {
        // for cdi proxy
    }

    @Inject
    public DokumentBestillerTjeneste(DomeneobjektProvider domeneobjektProvider,
                                     DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokgenBrevproduksjonTjeneste = dokgenBrevproduksjonTjeneste;
    }

    public byte[] forhandsvisBrev(DokumentHendelseEntitet dokumentHendelseEntitet) {
        var behandling = hentBehandling(dokumentHendelseEntitet.getBehandlingUuid());
        return dokgenBrevproduksjonTjeneste.forhåndsvisBrev(dokumentHendelseEntitet, behandling);
    }

    public void bestillBrev(DokumentHendelseEntitet dokumentHendelseEntitet) {
        var behandling = hentBehandling(dokumentHendelseEntitet.getBehandlingUuid());
        dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelseEntitet, behandling);
    }

    public String genererJson(UUID behandlingUuid){
        var behandling = hentBehandling(behandlingUuid);
        var dokumentHendelse = DokumentHendelseEntitet.builder()
            .medBestillingUuid(UUID.randomUUID())
            .medBehandlingUuid(behandlingUuid)
            .medDokumentMal(DokumentMalEnum.FORELDREPENGER_INNVILGELSE)
            .build();
        return  dokgenBrevproduksjonTjeneste.genererJson(dokumentHendelse, behandling, dokumentHendelse.getDokumentMal(), BestillingType.UTKAST );
    }


    private Behandling hentBehandling(UUID behandlingUuid) {
        var behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        domeneobjektProvider.hentFagsakBackend(behandling);
        return behandling;
    }

}
