package no.nav.foreldrepenger.melding.klage;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum KlageAvvistÅrsak implements Kodeverdi, ÅrsakMedLovReferanse {

    KLAGET_FOR_SENT("KLAGET_FOR_SENT",
            "{\"klageAvvistAarsak\":{\"NFP\": {\"lovreferanser\": [\"31\", \"33\"]},\"KA\": {\"lovreferanser\": [\"31\", \"34\"]}}}"),
    KLAGE_UGYLDIG("KLAGE_UGYLDIG", null),
    IKKE_PAKLAGD_VEDTAK("IKKE_PAKLAGD_VEDTAK",
            "{\"klageAvvistAarsak\":{\"NFP\": {\"lovreferanser\": [\"28\", \"33\"]},\"KA\": {\"lovreferanser\": [\"28\", \"34\"]}}}"),
    KLAGER_IKKE_PART("KLAGER_IKKE_PART",
            "{\"klageAvvistAarsak\":{\"NFP\": {\"lovreferanser\": [\"28\", \"33\"]},\"KA\": {\"lovreferanser\": [\"28\", \"34\"]}}}"),
    IKKE_KONKRET("IKKE_KONKRET",
            "{\"klageAvvistAarsak\":{\"NFP\": {\"lovreferanser\": [\"32\", \"33\"]},\"KA\": {\"lovreferanser\": [\"32\", \"34\"]}}}"),
    IKKE_SIGNERT("IKKE_SIGNERT",
            "{\"klageAvvistAarsak\":{\"NFP\": {\"lovreferanser\": [\"31\", \"33\"]},\"KA\": {\"lovreferanser\": [\"31\", \"34\"]}}}"),
    UDEFINERT("-", null),
    ;

    private static final Map<String, KlageAvvistÅrsak> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "KLAGE_AVVIST_AARSAK";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    @JsonIgnore
    private String lovHjemmel;

    KlageAvvistÅrsak(String kode, String lovHjemmel) {
        this.kode = kode;
        this.lovHjemmel = lovHjemmel;
    }

    @JsonCreator
    public static KlageAvvistÅrsak fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent KlageAvvistÅrsak: " + kode);
        }
        return ad;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getLovHjemmelData() {
        return lovHjemmel;
    }


}
