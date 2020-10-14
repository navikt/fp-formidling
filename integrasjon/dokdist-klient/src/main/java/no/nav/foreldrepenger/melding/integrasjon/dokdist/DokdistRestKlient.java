package no.nav.foreldrepenger.melding.integrasjon.dokdist;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class DokdistRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokdistRestKlient.class);
    private static final String DOKDIST_REST_BASE_URL = "dokdist_rest_base.url";
    private static final String DISTRIBUERJOURNALPOST = "/distribuerjournalpost";

    private OidcRestClient oidcRestClient;
    private String endpointDokdistRestBase;

    public DokdistRestKlient() {
        //CDI
    }

    @Inject
    public DokdistRestKlient(OidcRestClient oidcRestClient,
                             @KonfigVerdi(DOKDIST_REST_BASE_URL) String endpointDokdistRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointDokdistRestBase = endpointDokdistRestBase;
    }

    public void distribuerJournalpost(JournalpostId journalpostId) {
        DistribuerJournalpostRequest request = lagRequest(journalpostId);
        try {
            URIBuilder uriBuilder = new URIBuilder(endpointDokdistRestBase + DISTRIBUERJOURNALPOST);
            Optional<DistribuerJournalpostResponse> response = oidcRestClient.postReturnsOptional(uriBuilder.build(), request, DistribuerJournalpostResponse.class);
            if (response.isPresent()) {
                LOGGER.info("Distribuert {} med bestillingsId {}", journalpostId, response);
            } else {
                throw DokdistFeil.FACTORY.tomtSvarFraDokdist(journalpostId).toException();
            }
        } catch (Exception e) {
            throw DokdistFeil.FACTORY.feilVedKallTilDokdist(journalpostId, e).toException();
        }
    }

    private DistribuerJournalpostRequest lagRequest(JournalpostId journalpostId) {
        String batchId = UUID.randomUUID().toString();
        LOGGER.info("Bestiller distribusjon av {} med batchId {}", journalpostId, batchId);
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(), batchId, Fagsystem.FPSAK.getOffisiellKode(), Fagsystem.FPSAK.getKode());
    }
}
