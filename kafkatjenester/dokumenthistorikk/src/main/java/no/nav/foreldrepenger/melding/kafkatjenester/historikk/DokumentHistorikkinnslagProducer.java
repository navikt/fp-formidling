package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.serialization.StringSerializer;

import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class DokumentHistorikkinnslagProducer {

    Producer<String, String> producer;
    String topic;

    public DokumentHistorikkinnslagProducer() {
        // for CDI proxy
    }

    @Inject
    public DokumentHistorikkinnslagProducer(@KonfigVerdi("kafka.historikkinnslag.topic") String topic,
                                            @KonfigVerdi("kafka.bootstrap.servers") String bootstrapServers,
                                            @KonfigVerdi("schema.registry.url") String schemaRegistryUrl,
                                            @KonfigVerdi("systembruker.username") String username,
                                            @KonfigVerdi("systembruker.password") String password) {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("schema.registry.url", schemaRegistryUrl);
        properties.put("client.id", "KP-" + topic);

        setSecurity(username, properties);
        setUsernameAndPassword(username, password, properties);

        this.producer = createProducer(properties);
        this.topic = topic;
    }

    void runProducerWithSingleJson(ProducerRecord<String, String> record) {
        try {
            producer.send(record)
                    .get();
        } catch (ExecutionException e) {
            throw KafkaProducerFeil.FACTORY.uventetFeil(topic, e).toException();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw KafkaProducerFeil.FACTORY.uventetFeil(topic, e).toException();
        } catch (AuthenticationException | AuthorizationException e) {
            throw KafkaProducerFeil.FACTORY.feilIPålogging(topic, e).toException();
        } catch (RetriableException e) {
            throw KafkaProducerFeil.FACTORY.retriableExceptionMotKaka(topic, e).toException();
        } catch (KafkaException e) {
            throw KafkaProducerFeil.FACTORY.annenExceptionMotKafka(topic, e).toException();
        }
    }

    void setUsernameAndPassword(String username, String password, Properties properties) {
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            String jaasCfg = String.format(jaasTemplate, username, password);
            properties.put("sasl.jaas.config", jaasCfg);
        }
    }

    Producer<String, String> createProducer(Properties properties) {
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }

    void setSecurity(String username, Properties properties) {
        if (username != null && !username.isEmpty()) {
            properties.put("security.protocol", "SASL_SSL");
            properties.put("sasl.mechanism", "PLAIN");
        }
    }

    public void sendJson(String json) {
        runProducerWithSingleJson(new ProducerRecord<>(topic, json));
    }
}
