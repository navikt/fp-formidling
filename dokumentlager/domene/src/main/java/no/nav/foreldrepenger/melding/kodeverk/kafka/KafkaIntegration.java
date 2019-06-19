package no.nav.foreldrepenger.melding.kodeverk.kafka;

public interface KafkaIntegration {

    /**
     * Er integrasjonen i live.
     *
     * @return true / false
     */
    boolean isAlive();
}
