package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentKvitteringDto;

@ApplicationScoped
public class DokumentKvitteringTjeneste {

    private Behandlinger fpsakKlient;

    DokumentKvitteringTjeneste() {
        // CDI
    }

    @Inject
    public DokumentKvitteringTjeneste(Behandlinger restKlient) {
        this.fpsakKlient = restKlient;
    }

    public void sendKvittering(DokumentKvitteringDto kvittering) {
        fpsakKlient.kvitterDokument(kvittering);
    }

}
