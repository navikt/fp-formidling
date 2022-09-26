package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import java.net.URI;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.FerdigstillJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggResponse;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@NativeClient
@RestClientConfig(tokenConfig = TokenFlow.STS_CC)
public class JournalpostNativeRestKlient implements Journalpost {
    private static final Logger LOG = LoggerFactory.getLogger(JournalpostNativeRestKlient.class);

    private final URI endpoint;
    private final URI endpointProxy;
    private final RestClient restKlient;

    @Inject
    public JournalpostNativeRestKlient(RestClient restClient, @KonfigVerdi(value = "journalpost.rest.v1.url") URI endpoint,
                                       @KonfigVerdi(value = "journalpost.rest.proxy.v1.url") URI endpointProxy) {
        this.endpoint = endpoint;
        this.restKlient = restClient;
        this.endpointProxy = endpointProxy;
    }

    @Override
    public OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean ferdigstill) {
        try {
            var opprett = ferdigstill ? UriBuilder.fromUri(endpoint).queryParam("forsoekFerdigstill", "true").build() : endpoint;
            var rrequest = RestRequest.newPOSTJson(request, opprett, JournalpostNativeRestKlient.class);
            return restKlient.sendExpectConflict(rrequest, OpprettJournalpostResponse.class);
        } catch (IllegalArgumentException|UriBuilderException e) {
            throw new TekniskException("FPFORMIDLING-156530", String
                    .format("Feil ved oppretting av URI for opprettelse av ny journalpost til fagsak %s.", request.getSak().getArkivsaksnummer()), e);
        }
    }

    @Override
    public void tilknyttVedlegg(TilknyttVedleggRequest request, JournalpostId journalpostIdTil) {
        try {
            var tilknyttPath = String.format("/%s/tilknyttVedlegg", journalpostIdTil.getVerdi());
            var uri = UriBuilder.fromUri(endpointProxy).path(tilknyttPath).build();

            var method = new RestRequest.Method(RestRequest.WebMethod.PUT, RestRequest.jsonPublisher(request));
            var rrequest = RestRequest.newRequest(method, uri, JournalpostNativeRestKlient.class);
            var tilknyttVedleggResponse = restKlient.send(rrequest, TilknyttVedleggResponse.class);

            if (!tilknyttVedleggResponse.getFeiledeDokumenter().isEmpty()) {
                throw new IllegalStateException(
                        "Følgende vedlegg feilet " + tilknyttVedleggResponse.toString() + " for journalpost " + journalpostIdTil);
            } else {
                LOG.info("Vedlegg tilknyttet {} OK", journalpostIdTil);
            }
        } catch (UriBuilderException|IllegalArgumentException e) {
            throw new TekniskException("FPFORMIDLING-156531",
                    String.format("Feil ved oppretting av URI for tilknytning av vedlegg til %s: %s.", journalpostIdTil, request.toString()), e);
        }
    }

    @Override
    public void ferdigstillJournalpost(JournalpostId journalpostId) {
        try {
            LOG.info("Ferdigstiller journalpost {}", journalpostId);
            var ferdigstill = UriBuilder.fromUri(endpoint).path(String.format("/%s/ferdigstill", journalpostId.getVerdi())).build();
            var method = new RestRequest.Method(RestRequest.WebMethod.PATCH, RestRequest.jsonPublisher(new FerdigstillJournalpostRequest("9999")));
            var rrequest = RestRequest.newRequest(method, ferdigstill, JournalpostNativeRestKlient.class);
            restKlient.send(rrequest, String.class);
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-156535", String.format("Klarte ikke ferdigstille %s.", journalpostId), e);
        }
    }
}
