package no.nav.foreldrepenger.meldinger.kafkatjenester;

import java.util.List;

public interface MeldingConsumer {
    List<String> hentConsumerMeldingene();

    void manualCommitSync();

    void manualCommitAsync();

    List<String> hentConsumerMeldingeneFraStarten();


    void close();
}
