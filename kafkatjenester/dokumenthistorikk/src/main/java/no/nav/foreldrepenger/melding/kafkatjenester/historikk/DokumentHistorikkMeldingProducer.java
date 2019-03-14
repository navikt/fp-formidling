package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.konfig.KonfigVerdi;
import no.vedtak.felles.kafka.GenerellMeldingProducer;
import no.vedtak.felles.kafka.MeldingProducer;

@ApplicationScoped
public class DokumentHistorikkMeldingProducer extends GenerellMeldingProducer implements MeldingProducer {

    public DokumentHistorikkMeldingProducer() {
        // for CDI proxy
    }

    @Inject
    public DokumentHistorikkMeldingProducer(@KonfigVerdi("kafka.dokumenthistorikk.topic") String topic,
                                            @KonfigVerdi("bootstrap.servers") String bootstrapServers,
                                            @KonfigVerdi("kafka.dokumenthistorikk.schema.registry.url") String schemaRegistryUrl,
                                            @KonfigVerdi("kafka.dokumenthistorikk.client.id") String clientId,
                                            @KonfigVerdi("systembruker.username") String username,
                                            @KonfigVerdi("systembruker.password") String password) {
        super(topic, bootstrapServers, schemaRegistryUrl, clientId, username, password);
    }

}
