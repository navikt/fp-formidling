package no.nav.foreldrepenger.melding.kafkatjenester;

import java.util.List;

public interface MeldingConsumer {
    List<String> hentConsumerMeldingene();

    void manualCommitSync();

    void manualCommitAsync();

    List<String> hentConsumerMeldingeneFraStarten();


    void close();
}
