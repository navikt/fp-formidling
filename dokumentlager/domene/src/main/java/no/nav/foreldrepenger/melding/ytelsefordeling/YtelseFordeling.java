package no.nav.foreldrepenger.melding.ytelsefordeling;

import no.nav.foreldrepenger.melding.fagsak.Dekningsgrad;

public class YtelseFordeling {
    Dekningsgrad dekningsgrad;

    public YtelseFordeling(Dekningsgrad dekningsgrad) {
        this.dekningsgrad = dekningsgrad;
    }

    public Dekningsgrad getDekningsgrad() {
        return dekningsgrad;
    }
}
