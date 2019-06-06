package no.nav.foreldrepenger.melding.datamapper;

import java.time.Period;

import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametereImpl;

public class DatamapperTestUtil {

    public static BrevParametere getBrevParametere() {
        return new BrevParametereImpl(14, 14, Period.ofWeeks(6), Period.ofWeeks(6));
    }

}
