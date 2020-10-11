package no.nav.foreldrepenger.melding.brevbestiller;

import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.melding.geografisk.Landkoder;

public class LandkodeOversetter {

    private static final Map<String, Landkoder> LANDKODER_ISO2 = Map.ofEntries(
            Map.entry("NO", Landkoder.NOR),
            Map.entry("SE", Landkoder.SWE)
    );

    public static String tilLandkoder(String landkode) {
        return Optional.ofNullable(landkode)
                .map(l -> LANDKODER_ISO2.getOrDefault(l, Landkoder.fraKodeDefaultUoppgitt(l)))
                .orElse(Landkoder.UOPPGITT)
                .getKode();
    }

}
