package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.familie.topic.Topic;
import no.nav.familie.topic.TopicManifest;
import no.nav.vedtak.konfig.KonfigVerdi;

@Dependent
public class DokumentbestillingStreamKafkaProperties {

    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String username;
    private final String password;
    private final String topic;
    private final String applicationId;
    private Topic kontraktTopic;

    @Inject
    DokumentbestillingStreamKafkaProperties(@KonfigVerdi("kafka.bootstrap.servers") String bootstrapServers,
                                            @KonfigVerdi("schema.registry.url") String schemaRegistryUrl,
                                            @KonfigVerdi("systembruker.username") String username,
                                            @KonfigVerdi("systembruker.password") String password,
                                            @KonfigVerdi("kafka.dokumentbestilling.topic") String topic) {
        this.topic = topic;
        this.applicationId = ApplicationIdUtil.get();
        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.username = username;
        this.password = password;
        this.kontraktTopic = TopicManifest.DOKUMENT_HENDELSE;
    }

    String getBootstrapServers() {
        return bootstrapServers;
    }

    String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getTopic() {
        return topic;
    }

    Class<?> getKeyClass() {
        return kontraktTopic.getSerdeKey().getClass();
    }

    Class<?> getValueClass() {
        return kontraktTopic.getSerdeValue().getClass();
    }

    boolean harSattBrukernavn() {
        return username != null && !username.isEmpty();
    }

    String getApplicationId() {
        return applicationId;
    }

    String getClientId() {
        return "KC-" + topic;
    }

}
