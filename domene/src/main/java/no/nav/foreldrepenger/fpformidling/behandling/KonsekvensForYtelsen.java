package no.nav.foreldrepenger.fpformidling.behandling;

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
public enum KonsekvensForYtelsen implements Kodeverdi {


    FORELDREPENGER_OPPHØRER("FORELDREPENGER_OPPHØRER"),
    ENDRING_I_BEREGNING("ENDRING_I_BEREGNING"),
    ENDRING_I_UTTAK("ENDRING_I_UTTAK"),
    ENDRING_I_FORDELING_AV_YTELSEN("ENDRING_I_FORDELING_AV_YTELSEN"),
    INGEN_ENDRING("INGEN_ENDRING"),
    ENDRING_I_BEREGNING_OG_UTTAK("ENDRING_I_BEREGNING_OG_UTTAK"),
    UDEFINERT("-"),
    ;
    
    private static final Map<String, KonsekvensForYtelsen> KODER = new LinkedHashMap<>();
    
    public static final String KODEVERK = "KONSEKVENS_FOR_YTELSEN";

    private String kode;

    private KonsekvensForYtelsen(String kode) {
        this.kode = kode;
    }
    @JsonCreator
    public static KonsekvensForYtelsen fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent KonsekvensForYtelsen: " + kode);
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
    
    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

}
