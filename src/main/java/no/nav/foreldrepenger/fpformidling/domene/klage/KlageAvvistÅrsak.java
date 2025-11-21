package no.nav.foreldrepenger.fpformidling.domene.klage;

import java.util.Set;

public enum KlageAvvistÅrsak {

    KLAGET_FOR_SENT("KLAGET_FOR_SENT", Set.of("31", "33")),
    KLAGE_UGYLDIG("KLAGE_UGYLDIG", null),
    IKKE_PAKLAGD_VEDTAK("IKKE_PAKLAGD_VEDTAK", Set.of("28", "33")),
    KLAGER_IKKE_PART("KLAGER_IKKE_PART", Set.of("28", "33")),
    IKKE_KONKRET("IKKE_KONKRET", Set.of("32", "33")),
    IKKE_SIGNERT("IKKE_SIGNERT", Set.of("32", "33")),
    UDEFINERT("-", null),
    ;

    private final String kode;

    private final Set<String> lovHjemmel;

    KlageAvvistÅrsak(String kode, Set<String> lovHjemmel) {
        this.kode = kode;
        this.lovHjemmel = lovHjemmel;
    }

    public String getKode() {
        return kode;
    }

    public Set<String> getLovHjemmel() {
        return lovHjemmel != null ? lovHjemmel : Set.of();
    }


}
