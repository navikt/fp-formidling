package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.Behandlinger;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;

@ApplicationScoped
public class DokumentKvitteringTjeneste {

    private Behandlinger fpsakKlient;

    DokumentKvitteringTjeneste() {
        //CDI
    }

    @Inject
    public DokumentKvitteringTjeneste(Behandlinger restKlient) {
        this.fpsakKlient = restKlient;
    }

    public void sendKvittering(DokumentProdusertDto kvittering) {
        fpsakKlient.kvitterDokument(kvittering);
    }

}
