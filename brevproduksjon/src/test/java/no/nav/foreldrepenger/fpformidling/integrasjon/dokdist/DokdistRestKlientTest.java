package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

public class DokdistRestKlientTest {

    private static final String BASE_URL = "https://base.url";
    private static final JournalpostId JOURNALPOST_ID = new JournalpostId("123456789");

    private OidcRestClient oidcRestClient;
    private DokdistRestKlient dokdistRestKlient;

    @BeforeEach
    void setUp() {
        oidcRestClient = mock(OidcRestClient.class);
        dokdistRestKlient = new DokdistRestKlient(oidcRestClient, BASE_URL);
    }

    @Test
    public void skal_kalle_dokdist_med_url_og_request_basert_på_journalpostId() throws Exception {
        // Arrange
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        ArgumentCaptor<DistribuerJournalpostRequest> requestCaptor = ArgumentCaptor.forClass(
                DistribuerJournalpostRequest.class);
        DistribuerJournalpostResponse response = new DistribuerJournalpostResponse("123");
        when(oidcRestClient.postAcceptConflict(uriCaptor.capture(), requestCaptor.capture(),
                eq(DistribuerJournalpostResponse.class))).thenReturn(response);

        // Act
        dokdistRestKlient.distribuerJournalpost(JOURNALPOST_ID, "12345");

        // Assert
        assertThat(uriCaptor.getValue().toURL().toString()).isEqualTo(BASE_URL + "/distribuerjournalpost");
        assertThat(requestCaptor.getValue().getJournalpostId()).isEqualTo(JOURNALPOST_ID.getVerdi());
        assertThat(requestCaptor.getValue().getBatchId()).isNotNull();
        assertThat(requestCaptor.getValue().getBestillendeFagsystem()).isEqualTo(Fagsystem.FPSAK.getOffisiellKode());
        assertThat(requestCaptor.getValue().getDokumentProdApp()).isEqualTo(Fagsystem.FPSAK.getKode());
    }

}
