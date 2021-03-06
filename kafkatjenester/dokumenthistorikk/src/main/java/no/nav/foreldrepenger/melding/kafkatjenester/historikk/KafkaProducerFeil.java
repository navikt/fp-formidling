package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.RetriableException;

public class KafkaProducerFeil {

    public static ManglerTilgangException feilIPålogging(String topic, Exception e) {
        return new ManglerTilgangException("FPFORMIDLINGKAFKA-821005", String.format("Feil i pålogging mot Kafka, topic:%s", topic), e);
    }

    public static IntegrasjonException uventetFeil(String topic, Exception e) {
        return new IntegrasjonException("FPFORMIDLINGKAFKA-925469", String.format("Uventet feil ved sending til Kafka, topic:%s", topic), e);
    }

    public static IntegrasjonException retriableExceptionMotKaka(String topic, RetriableException e) {
        return new IntegrasjonException("FPFORMIDLINGKAFKA-127608", String.format("Fikk transient feil mot Kafka, kan prøve igjen, topic:%s", topic), e);
    }

    public static IntegrasjonException annenExceptionMotKafka(String topic, KafkaException e) {
        return new IntegrasjonException("FPFORMIDLINGKAFKA-811208", String.format("Fikk feil mot Kafka, topic:%s", topic), e);
    }
}
