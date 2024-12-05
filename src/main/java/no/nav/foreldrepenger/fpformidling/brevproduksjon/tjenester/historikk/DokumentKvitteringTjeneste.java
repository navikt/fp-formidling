package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Saksnummer;
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

    public void sendKvittering(UUID behandlingUuid, UUID bestillingUuid, String journalpostId, String dokumentId, String saksnummer) {
        fpsakKlient.kvitterDokument(new DokumentKvitteringDto(behandlingUuid, new Saksnummer(saksnummer), bestillingUuid, journalpostId, dokumentId));
    }

}
