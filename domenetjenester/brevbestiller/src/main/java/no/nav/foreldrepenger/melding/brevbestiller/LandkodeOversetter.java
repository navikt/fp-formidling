package no.nav.foreldrepenger.melding.brevbestiller;

import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.geografisk.Landkoder;

@ApplicationScoped
public class LandkodeOversetter {

    // Tar med begge veier for illustrasjonen
    private static final Map<String, String> LANDKODER = Map.ofEntries(
            Map.entry("NO", Landkoder.NOR.getKode()),
            Map.entry("SE", Landkoder.SWE.getKode())
            //Map.entry(Landkoder.NOR.getKode(), "NO"),
            //Map.entry(Landkoder.SWE.getKode(), "NO")
    );

    public LandkodeOversetter() { /* for cdi proxy */ }

    // Dette er tullete navngivning. Se kommentar i KodeverkRepositoryImplTest. Eksisterende kode mapper fra NO -> NOR, ikke omvendt
    public String tilIso2(String iso3) {
        return Optional.ofNullable(iso3)
                .map(LANDKODER::get)
                .orElse(Landkoder.UOPPGITT.getKode());
    }

}
