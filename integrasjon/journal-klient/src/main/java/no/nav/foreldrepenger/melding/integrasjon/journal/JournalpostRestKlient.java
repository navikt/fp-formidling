package no.nav.foreldrepenger.melding.integrasjon.journal;

import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;

import no.nav.foreldrepenger.melding.integrasjon.journal.dto.FerdigstillJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class JournalpostRestKlient {
    private static final String DEFAULT_URI = "http://dokarkiv.default/rest/journalpostapi/v1/journalpost";

    private URI endpoint;
    private OidcRestClient restKlient;

    JournalpostRestKlient() {
        // CDI
    }

    @Inject
    public JournalpostRestKlient(@KonfigVerdi(value = "journalpost_rest_v1.url", defaultVerdi = DEFAULT_URI) URI endpoint, OidcRestClient restKlient) {
        this.endpoint = endpoint;
        this.restKlient = restKlient;
    }

    public OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean ferdigstill) {
        try {
            var uri = new URIBuilder(endpoint).addParameter("forsoekFerdigstill", ""+ferdigstill).build();
            return restKlient.post(uri, request, OpprettJournalpostResponse.class);
        } catch (URISyntaxException e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteUriForNyJournalpost(request.getSak().getArkivsaksnummer(), e).toException();
        }
    }

    public void tilknyttVedlegg(TilknyttVedleggRequest request, JournalpostId journalpostIdTil) {
        try {
            String tilknyttPath = String.format("/%s/tilknyttVedlegg", journalpostIdTil.getVerdi());
            var uri = new URIBuilder(endpoint + tilknyttPath).build();
            restKlient.post(uri, request); //TODO(JEJ): Se hva HTTP-koden er og logge feil hvis ikke-OK
        } catch (URISyntaxException e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteUriForTilknytningAvVedlegg(journalpostIdTil, request.toString(), e).toException();
        }
    }

    public void ferdigstillJournalpost(FerdigstillJournalpostRequest request, JournalpostId journalpostId) {
        try {
            String tilknyttPath = String.format("/%s/ferdigstill", journalpostId.getVerdi());
            var uri = new URIBuilder(endpoint + tilknyttPath).build();
            restKlient.post(uri, request); //TODO(JEJ): Se hva HTTP-koden er og logge feil hvis ikke-OK
        } catch (URISyntaxException e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteUriForFerdigstillingAvJournalpost(journalpostId, request.toString(), e).toException();
        }
    }
}
