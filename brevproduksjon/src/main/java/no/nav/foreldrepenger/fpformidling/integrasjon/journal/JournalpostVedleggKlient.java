package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggResponse;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.STS_CC, endpointProperty = "journalpost.rest.proxy.v1.url",
        endpointDefault = "http://dokarkivproxy.teamdokumenthandtering/rest/journalpostapi/v1/journalpost")
public class JournalpostVedleggKlient implements JournalpostVedlegg {
    private static final Logger LOG = LoggerFactory.getLogger(JournalpostVedleggKlient.class);

    private final RestClient restKlient;
    private final RestConfig restConfig;

    public JournalpostVedleggKlient() {
        this.restKlient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
    }


    @Override
    public void tilknyttVedlegg(TilknyttVedleggRequest request, JournalpostId journalpostIdTil) {
        try {
            var tilknyttPath = String.format("/%s/tilknyttVedlegg", journalpostIdTil.getVerdi());
            var uri = UriBuilder.fromUri(restConfig.endpoint()).path(tilknyttPath).build();

            var method = new RestRequest.Method(RestRequest.WebMethod.PUT, RestRequest.jsonPublisher(request));
            var rrequest = RestRequest.newRequest(method, uri, restConfig);
            var tilknyttVedleggResponse = restKlient.send(rrequest, TilknyttVedleggResponse.class);

            if (!tilknyttVedleggResponse.feiledeDokumenter().isEmpty()) {
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

}
