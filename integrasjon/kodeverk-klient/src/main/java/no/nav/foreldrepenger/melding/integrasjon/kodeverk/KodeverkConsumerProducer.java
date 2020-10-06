package no.nav.foreldrepenger.melding.integrasjon.kodeverk;


import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class KodeverkConsumerProducer {
    private KodeverkConsumerConfig consumerConfig;

    @Inject
    public void setConfig(KodeverkConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public KodeverkConsumer kodeverkConsumer() {
        return new KodeverkConsumerImpl(consumerConfig.getPort());
    }

}
