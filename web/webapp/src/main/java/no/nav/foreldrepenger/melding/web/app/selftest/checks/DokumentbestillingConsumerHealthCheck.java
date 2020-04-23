package no.nav.foreldrepenger.melding.web.app.selftest.checks;

import no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling.DokumentbestillingConsumer;
import org.apache.kafka.streams.KafkaStreams;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DokumentbestillingConsumerHealthCheck extends ExtHealthCheck {

    private DokumentbestillingConsumer consumer;

    DokumentbestillingConsumerHealthCheck() {
    }

    @Inject
    public DokumentbestillingConsumerHealthCheck(DokumentbestillingConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected String getDescription() {
        return "Test av consumering av bestillinger fra kafka";
    }

    @Override
    protected String getEndpoint() {
        return consumer.getTopic();
    }

    @Override
    protected InternalResult performCheck() {
        InternalResult intTestRes = new InternalResult();

        KafkaStreams.State tilstand = consumer.getTilstand();
        intTestRes.setMessage("Consumer is in state [" + tilstand.name() + "].");
        if (tilstand.isRunningOrRebalancing() || KafkaStreams.State.CREATED.equals(tilstand)) {
            intTestRes.setOk(true);
        } else {
            intTestRes.setOk(false);
        }
        intTestRes.noteResponseTime();

        return intTestRes;
    }
}
