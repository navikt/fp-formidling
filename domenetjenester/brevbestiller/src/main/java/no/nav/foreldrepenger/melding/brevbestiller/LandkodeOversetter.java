package no.nav.foreldrepenger.melding.brevbestiller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class LandkodeOversetter {

    public static final String UOPPGITT = "???";

    private KodeverkRepository kodeRepo;
    private Map<String, String> landkoder = new HashMap<>();

    public LandkodeOversetter() { /* for cdi proxy */ }

    @Inject
    public LandkodeOversetter(KodeverkRepository kodeRepo) {
        this.kodeRepo = kodeRepo;
    }

    public String tilIso2(String iso3) {
        if (landkoder.isEmpty()) {
            landkoder.putAll(kodeRepo.hentLandkodeISO2TilLandkoderMap());
        }

        return Optional.ofNullable(iso3)
                .map(iso2 -> landkoder.get(iso2))
                .orElse(UOPPGITT);
    }

}
