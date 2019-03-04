package no.nav.foreldrepenger.melding.feed;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface FeedPollerFeil extends DeklarerteFeil {

    FeedPollerFeil FACTORY = FeilFactory.create(FeedPollerFeil.class);

    @TekniskFeil(feilkode = "FPMELDING-051", feilmelding = "Kan ikke lese fra Kafkakø", logLevel = LogLevel.ERROR)
    Feil kanIkkeLeseFraKafkaKø(Exception e);

}
