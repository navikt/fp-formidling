package no.nav.foreldrepenger.melding.dokumentproduksjon.v3;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.DokumentproduksjonV3;

class DokumentproduksjonSelftestConsumerImpl implements DokumentproduksjonSelftestConsumer {
    private DokumentproduksjonV3 port;
    private String endpointUrl;

    public DokumentproduksjonSelftestConsumerImpl(DokumentproduksjonV3 port, String endpointUrl) {
        this.port = port;
        this.endpointUrl = endpointUrl;
    }

    @Override
    public void ping() {
        port.ping();
    }

    @Override
    public String getEndpointUrl() {
        return endpointUrl;
    }
}
