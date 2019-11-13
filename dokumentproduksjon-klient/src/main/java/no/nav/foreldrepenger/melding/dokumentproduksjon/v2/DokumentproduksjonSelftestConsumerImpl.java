package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.DokumentproduksjonV2;

class DokumentproduksjonSelftestConsumerImpl implements DokumentproduksjonSelftestConsumer {
    private DokumentproduksjonV2 port;
    private String endpointUrl;

    public DokumentproduksjonSelftestConsumerImpl(DokumentproduksjonV2 port, String endpointUrl) {
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
