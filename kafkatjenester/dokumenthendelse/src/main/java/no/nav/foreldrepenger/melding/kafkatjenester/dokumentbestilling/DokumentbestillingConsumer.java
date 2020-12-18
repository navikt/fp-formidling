package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import java.time.Duration;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.kodeverk.kafka.KafkaIntegration;
import no.nav.vedtak.apptjeneste.AppServiceHandler;

@ApplicationScoped
@ActivateRequestContext
@Transactional
public class DokumentbestillingConsumer implements AppServiceHandler, KafkaIntegration {

    private static final Logger log = LoggerFactory.getLogger(DokumentbestillingConsumer.class);
    private KafkaStreams stream;
    private String topic;
    private KafkaReader kafkaReader;

    public DokumentbestillingConsumer() {
        //CDI
    }

    @Inject
    public DokumentbestillingConsumer(DokumentbestillingStreamKafkaProperties streamKafkaProperties,
                                      KafkaReader kafkaReader) {
        this.topic = streamKafkaProperties.getTopic();
        this.kafkaReader = kafkaReader;

        Properties props = setupProperties(streamKafkaProperties);

        final StreamsBuilder builder = new StreamsBuilder();

        Consumed<String, String> stringStringConsumed = Consumed.with(Topology.AutoOffsetReset.EARLIEST);
        builder.stream(this.topic, stringStringConsumed)
                .foreach(this::handleMessage);

        final Topology topology = builder.build();
        stream = new KafkaStreams(topology, props);
    }

    private Properties setupProperties(DokumentbestillingStreamKafkaProperties streamProperties) {
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, streamProperties.getApplicationId());
        log.info("Bruker applicationID: {}", streamProperties.getApplicationId());
        props.put(StreamsConfig.CLIENT_ID_CONFIG, streamProperties.getClientId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, streamProperties.getBootstrapServers());

        // Sikkerhet
        if (streamProperties.harSattBrukernavn()) {
            log.info("Using user name {} to authenticate against Kafka brokers", streamProperties.getUsername());
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(jaasTemplate, streamProperties.getUsername(), streamProperties.getPassword()));
        }

        // Setup schema-registry
        if (streamProperties.getSchemaRegistryUrl() != null) {
            props.put("schema.registry.url", streamProperties.getSchemaRegistryUrl());
        }
        // Serde
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, streamProperties.getKeyClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, streamProperties.getValueClass());
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndFailExceptionHandler.class);

        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "at_least_once");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "100000");

        return props;
    }

    public KafkaStreams.State getTilstand() {
        return stream.state();
    }

    @Override
    public boolean isAlive() {
        return stream != null && stream.state().isRunningOrRebalancing();
    }

    public String getTopic() {
        return topic;
    }

    private void handleMessage(String key, String payload) {
        kafkaReader.prosesser(payload);
    }

    @Override
    public void start() {
        addShutdownHooks();

        stream.start();
        log.info("Starter konsumering av topic={}, tilstand={}", topic, stream.state());
    }

    @Override
    public void stop() {
        log.info("Starter shutdown av topic={}, tilstand={} med 10 sekunder timeout", topic, stream.state());
        stream.close(Duration.ofSeconds(10));
        log.info("Shutdown av topic={}, tilstand={} med 10 sekunder timeout", topic, stream.state());
    }


    private void addShutdownHooks() {
        stream.setStateListener((oldState, newState) -> {
            log.info("From state={} to state={}", oldState, newState);

            if (newState == KafkaStreams.State.ERROR) {
                // if the stream has died there is no reason to keep spinning
                log.warn("No reason to keep living, closing stream");
                stop();
            }
        });
        stream.setUncaughtExceptionHandler((t, e) -> {
            log.error("Caught exception in stream, exiting", e);
            stop();
        });
    }
}
