package no.nav.foreldrepenger.melding.uttak;

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
public enum StønadskontoType implements Kodeverdi {

    FELLESPERIODE("FELLESPERIODE"),
    MØDREKVOTE("MØDREKVOTE"),
    FEDREKVOTE("FEDREKVOTE"),
    FORELDREPENGER("FORELDREPENGER"),
    FLERBARNSDAGER("FLERBARNSDAGER"),
    FORELDREPENGER_FØR_FØDSEL("FORELDREPENGER_FØR_FØDSEL"),
    UDEFINERT("-"),
    ;

    private static final Map<String, StønadskontoType> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "STOENADSKONTOTYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private StønadskontoType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static StønadskontoType fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent StønadskontoType: " + kode);
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
