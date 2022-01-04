package no.nav.foreldrepenger.melding.integrasjon.dokgen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

public class DokgenRestKlientTest {

    private static final String BASE_URL = "https://base.url";
    private static final String MAL_TYPE = "testmal";
    private static final Språkkode SPRÅKKODE = Språkkode.NN;
    private static final byte[] RESPONSE = "SVAR".getBytes();

    private OidcRestClient oidcRestClient;
    private DokgenRestKlient dokgenRestKlient;

    @BeforeEach
    void setUp() {
        oidcRestClient = mock(OidcRestClient.class);
        dokgenRestKlient = new DokgenRestKlient(oidcRestClient, BASE_URL);
    }

    @Test
    public void skal_kalle_dokgen_med_url_basert_på_templatenavn_og_språk() throws Exception {
        // Arrange
        TestDokumentdata dokumentdata = new TestDokumentdata();
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        when(oidcRestClient.postReturnsOptionalOfByteArray(uriCaptor.capture(), eq(dokumentdata))).thenReturn(Optional.of(RESPONSE));

        // Act
        dokgenRestKlient.genererPdf(MAL_TYPE, SPRÅKKODE, dokumentdata);

        // Assert
        assertThat(uriCaptor.getValue().toURL().toString()).isEqualTo(BASE_URL + "/template/" + MAL_TYPE + "/template_" + SPRÅKKODE.getKode().toLowerCase() + "/create-pdf-variation");
    }

    @Test
    public void skal_bruke_default_språk_bokmål_hvis_ikke_støttet_språk_sendes_inn() throws Exception {
        // Arrange
        TestDokumentdata dokumentdata = new TestDokumentdata();
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        when(oidcRestClient.postReturnsOptionalOfByteArray(uriCaptor.capture(), eq(dokumentdata))).thenReturn(Optional.of(RESPONSE));

        // Act
        dokgenRestKlient.genererPdf(MAL_TYPE, Språkkode.UDEFINERT, dokumentdata);

        // Assert
        assertThat(uriCaptor.getValue().toURL().toString()).isEqualTo(BASE_URL + "/template/" + MAL_TYPE + "/template_" + Språkkode.NB.getKode().toLowerCase() + "/create-pdf-variation");
    }

    @Test
    public void skal_kaste_exception_hvis_svaret_er_tomt() {
        // Arrange
        TestDokumentdata dokumentdata = new TestDokumentdata();
        when(oidcRestClient.postReturnsOptionalOfByteArray(any(URI.class), eq(dokumentdata))).thenReturn(Optional.empty());

        // Act
        assertThrows(VLException.class, () -> dokgenRestKlient.genererPdf(MAL_TYPE, SPRÅKKODE, dokumentdata));
    }

    private static class TestDokumentdata extends Dokumentdata {
    }
}
