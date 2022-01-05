package no.nav.foreldrepenger.fpformidling.klage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum KlageVurdering implements Kodeverdi {

    OPPHEVE_YTELSESVEDTAK("OPPHEVE_YTELSESVEDTAK"),
    STADFESTE_YTELSESVEDTAK("STADFESTE_YTELSESVEDTAK"),
    MEDHOLD_I_KLAGE("MEDHOLD_I_KLAGE"),
    AVVIS_KLAGE("AVVIS_KLAGE"),
    HJEMSENDE_UTEN_Å_OPPHEVE("HJEMSENDE_UTEN_Å_OPPHEVE"),
    UDEFINERT("-"),
    ;

    private static final Map<String, KlageVurdering> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "KLAGEVURDERING";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private KlageVurdering(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static KlageVurdering fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent KlageVurdering: " + kode);
        }
        return ad;
    }

    public static Map<String, KlageVurdering> kodeMap() {
        return Collections.unmodifiableMap(KODER);
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


}
