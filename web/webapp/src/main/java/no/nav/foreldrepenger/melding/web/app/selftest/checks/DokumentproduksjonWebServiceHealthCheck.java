package no.nav.foreldrepenger.melding.web.app.selftest.checks;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.felles.integrasjon.dokument.produksjon.DokumentproduksjonSelftestConsumer;

@ApplicationScoped
public class DokumentproduksjonWebServiceHealthCheck extends WebServiceHealthCheck {

    private DokumentproduksjonSelftestConsumer selftestConsumer;

    DokumentproduksjonWebServiceHealthCheck() {
        // for CDI proxy
    }

    @Inject
    public DokumentproduksjonWebServiceHealthCheck(DokumentproduksjonSelftestConsumer selftestConsumer) {
        this.selftestConsumer = selftestConsumer;
    }

    @Override
    protected String getDescription() {
        return "Test av web service Dokumentproduksjon";
    }

    @Override
    protected String getEndpoint() {
        return selftestConsumer.getEndpointUrl();
    }

    @Override
    protected void performWebServiceSelftest() {
        selftestConsumer.ping();
    }
}