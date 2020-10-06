package no.nav.foreldrepenger.melding.integrasjon.kodeverk;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class KodeverkConsumerProducerDelegator {
    private KodeverkConsumerProducer producer;

    @Inject
    public KodeverkConsumerProducerDelegator(KodeverkConsumerProducer producer) {
        this.producer = producer;
    }

    @Produces
    public KodeverkConsumer kodeverkConsumerForEndUser() {
        return producer.kodeverkConsumer();
    }

}
