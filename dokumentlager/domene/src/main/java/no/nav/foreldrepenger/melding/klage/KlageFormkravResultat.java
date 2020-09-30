package no.nav.foreldrepenger.melding.klage;

import java.util.ArrayList;
import java.util.List;

public class KlageFormkravResultat {
    private List<KlageAvvistÅrsak> avvistÅrsaker = new ArrayList<>();

    public KlageFormkravResultat(List<KlageAvvistÅrsak> avvistÅrsaker) {
        this.avvistÅrsaker = avvistÅrsaker;
    }

    public List<KlageAvvistÅrsak> getAvvistÅrsaker() {
        return avvistÅrsaker;
    }

}
