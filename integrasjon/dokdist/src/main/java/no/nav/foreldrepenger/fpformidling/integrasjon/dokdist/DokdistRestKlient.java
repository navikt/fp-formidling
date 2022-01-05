package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

@Dependent
@Deprecated
/**
 *
 * @see JerseyDokdistKlient
 *
 */
public class DokdistRestKlient implements Dokdist {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokdistRestKlient.class);
    private static final String DOKDIST_REST_BASE_URL = "dokdist_rest_base.url";
    private static final String DISTRIBUERJOURNALPOST = "/distribuerjournalpost";

    private final OidcRestClient oidcRestClient;
    private final String endpointDokdistRestBase;

    @Inject
    public DokdistRestKlient(OidcRestClient oidcRestClient,
            @KonfigVerdi(DOKDIST_REST_BASE_URL) String endpointDokdistRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointDokdistRestBase = endpointDokdistRestBase;
    }

    @Override
    public void distribuerJournalpost(JournalpostId journalpostId) {
        DistribuerJournalpostRequest request = lagRequest(journalpostId);
        try {
            URIBuilder uriBuilder = new URIBuilder(endpointDokdistRestBase + DISTRIBUERJOURNALPOST);
            Optional<DistribuerJournalpostResponse> response = oidcRestClient.postReturnsOptional(uriBuilder.build(), request,
                    DistribuerJournalpostResponse.class);
            if (response.isPresent()) {
                LOGGER.info("Distribuert {} med bestillingsId {}", journalpostId, response);
            } else {
                throw new TekniskException("FPFORMIDLING-647352", String.format("Fikk tomt svar ved kall til dokdist for %s.", journalpostId));
            }
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-647353", String.format("Fikk feil ved kall til dokdist for %s.", journalpostId), e);
        }
    }

    private DistribuerJournalpostRequest lagRequest(JournalpostId journalpostId) {
        String batchId = UUID.randomUUID().toString();
        LOGGER.info("Bestiller distribusjon av {} med batchId {}", journalpostId, batchId);
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(), batchId, Fagsystem.FPSAK.getOffisiellKode(), Fagsystem.FPSAK.getKode());
    }
}
