package no.nav.foreldrepenger.melding.anke;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum AnkeVurdering implements Kodeverdi {

    ANKE_STADFESTE_YTELSESVEDTAK("ANKE_STADFESTE_YTELSESVEDTAK"),
    ANKE_OPPHEVE_OG_HJEMSENDE("ANKE_OPPHEVE_OG_HJEMSENDE"),
    ANKE_HJEMSEND_UTEN_OPPHEV("ANKE_HJEMSENDE_UTEN_OPPHEV"),
    ANKE_OMGJOER("ANKE_OMGJOER"),
    ANKE_AVVIS("ANKE_AVVIS"),
    UDEFINERT("-"),
    ;

    private static final Map<String, AnkeVurdering> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "ANKEVURDERING";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }


    private String kode;

    AnkeVurdering(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static AnkeVurdering fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent AnkeVurdering: " + kode);
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

}
