package no.nav.foreldrepenger.melding.web.app.selftest.checks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import no.nav.vedtak.felles.integrasjon.dokument.produksjon.DokumentproduksjonSelftestConsumer;

public class DokumentproduksjonWebServiceHealthCheckTest {

    @Test
    public void test_alt() {
        final String ENDPT = "http://test.erstatter";
        DokumentproduksjonSelftestConsumer mockSelftestConsumer = mock(DokumentproduksjonSelftestConsumer.class);
        when(mockSelftestConsumer.getEndpointUrl()).thenReturn(ENDPT);
        DokumentproduksjonWebServiceHealthCheck check = new DokumentproduksjonWebServiceHealthCheck(mockSelftestConsumer);

        assertThat(check.getDescription()).isNotNull();

        assertThat(check.getEndpoint()).isEqualTo(ENDPT);

        check.performWebServiceSelftest();

        new DokumentproduksjonWebServiceHealthCheck(); // som trengs av CDI
    }
}
