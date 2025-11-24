package no.nav.foreldrepenger.fpformidling.domene.klage;

import java.util.Set;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fpsak;

public enum KlageAvvistÅrsak {

    KLAGET_FOR_SENT,
    KLAGE_UGYLDIG,
    IKKE_PAKLAGD_VEDTAK,
    KLAGER_IKKE_PART,
    IKKE_KONKRET,
    IKKE_SIGNERT,
    UDEFINERT,
    ;

    // Legg på JsonValue hvis du vil bruke denne mot fpsak / fpdokgen
    public String getKode() {
        return UDEFINERT.equals(this) ? Fpsak.STANDARDKODE_UDEFINERT : this.name();
    }

    public Set<String> getLovHjemmel() {
        return switch (this) {
            case KLAGET_FOR_SENT -> Set.of("31", "33");
            case IKKE_PAKLAGD_VEDTAK, KLAGER_IKKE_PART -> Set.of("28", "33");
            case IKKE_KONKRET, IKKE_SIGNERT -> Set.of("32", "33");
            case KLAGE_UGYLDIG, UDEFINERT -> Set.of();
        };
    }


}
