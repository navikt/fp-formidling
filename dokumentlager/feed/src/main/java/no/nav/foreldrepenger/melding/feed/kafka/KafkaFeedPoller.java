package no.nav.foreldrepenger.melding.feed.kafka;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.codahale.metrics.annotation.Timed;

import no.nav.foreldrepenger.melding.feed.poller.FeedPoller;
import no.nav.foreldrepenger.melding.kafkatjenester.dokumenthendelse.KafkaReader;

@ApplicationScoped
public class KafkaFeedPoller implements FeedPoller {

    public static final String FEED_NAME = "KAFKA_FORELDREPENGER_EVENT_KØ";

    private KafkaReader kafaReader;

    public KafkaFeedPoller() {
    }

    @Inject
    public KafkaFeedPoller(KafkaReader kafaReader) {
        this.kafaReader = kafaReader;
    }

    @Override
    public String getName() {
        return FEED_NAME;
    }

    @Timed
    @Override
    public void poll() {
        kafaReader.hentOgLagreMeldingene();
    }
}
