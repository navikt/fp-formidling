package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstidspunkt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.ErrorResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaHttpKlient;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;
import no.nav.vedtak.sikkerhet.oidc.token.TokenProvider;

@ApplicationScoped
@JavaClient
public class JavaDokdistRestKlient extends JavaHttpKlient implements Dokdist {

    private static final Logger LOG = LoggerFactory.getLogger(JavaDokdistRestKlient.class);

    private final String dokdistRestBaseUri;

    @Inject
    public JavaDokdistRestKlient(@KonfigVerdi("dokdist.rest.base.url") String endpoint) {
        this.dokdistRestBaseUri = endpoint;
    }

    public Dokdist.Resultat distribuerJournalpost(JournalpostId journalpostId, String bestillingId, Distribusjonstype distribusjonstype) {
        var endpoint = URI.create(dokdistRestBaseUri + "/distribuerjournalpost");
        var request = getRequestBuilder()
                .uri(endpoint)
                .POST(ofFormData(journalpostId, bestillingId, distribusjonstype))
                .build();

        var response = sendRequest(request);

        int status = response.statusCode();

        if ((status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) || status == HttpStatus.SC_CONFLICT) {
            var okResponse = fromJson(response.body(), DistribuerJournalpostResponse.class);
            LOG.info("[HTTP {}] Distribuert {} med bestillingsId {}", status, journalpostId, okResponse.bestillingsId());
            return Resultat.OK;
        } else if (status == HttpStatus.SC_BAD_REQUEST) {
            var errorResponse = fromJson(response.body(), ErrorResponse.class);
            LOG.warn("[HTTP {}] Brevdistribusjon feilet: Fikk svar '{}'.", status, errorResponse.message());
            if (errorResponse.message().contains("Mottaker har ukjent adresse")) {
                LOG.warn("[HTTP {}] Brevdistribusjon feilet. Bruker mangler adresse. Sjekk med fag om GOSYS oppgaven er opprettet for journalpostId {}",
                        status, journalpostId);
                return Resultat.MANGLER_ADRESSE;
            } else {
                throw new IntegrasjonException("F-468815", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", status, endpoint, errorResponse.message()));
            }
        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
            var errorResponse = fromJson(response.body(), ErrorResponse.class);
            throw new ManglerTilgangException("F-468815", String.format("[HTTP %s] Feilet mot %s pga <%s>", status, endpoint, errorResponse.message()));
        } else if (status == HttpStatus.SC_FORBIDDEN) {
            throw new ManglerTilgangException("F-468815", String.format("[HTTP %s] Feilet mot %s", status, endpoint));
        } else if (status == HttpStatus.SC_NOT_FOUND) {
            throw new TekniskException("F-468815", String.format("[HTTP %s] Feilet mot %s. Journalpost<%s> finnes ikke.", status, endpoint, journalpostId.getVerdi()));
        } else {
            throw new IntegrasjonException("F-468815", String.format("[HTTP %s] Uventet respons fra %s", status, endpoint));
        }
    }

    private HttpRequest.BodyPublisher ofFormData(JournalpostId journalpostId, String bestillingId, Distribusjonstype distribusjonstype) {
        var dto = payload(journalpostId, bestillingId, distribusjonstype);
        return HttpRequest.BodyPublishers.ofString(toJson(dto), UTF_8);
    }

    private static DistribuerJournalpostRequest payload(JournalpostId journalpostId,
                                                        String bestillingId,
                                                        Distribusjonstype distribusjonstype) {
        LOG.info("Bestiller distribusjon av {} med batchId {}", journalpostId, bestillingId);
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(), bestillingId, Fagsystem.FPSAK.getOffisiellKode(),
                Fagsystem.FPSAK.getKode(), distribusjonstype, Distribusjonstidspunkt.KJERNETID);
    }

    @Override
    public Optional<String> getAuthorization() {
        return Optional.ofNullable(TokenProvider.getStsSystemToken().token());
    }

    @Override
    public Optional<String> getConsumerId() {
        return Optional.ofNullable(SubjectHandler.getSubjectHandler().getConsumerId());
    }

    @Override
    public String getCallId() {
        return MDCOperations.getCallId();
    }
}
