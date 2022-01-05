package no.nav.foreldrepenger.fpformidling.anke;

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
public enum AnkeVurderingOmgjør implements Kodeverdi {

    ANKE_TIL_GUNST("ANKE_TIL_GUNST"),
    ANKE_DELVIS_OMGJOERING_TIL_GUNST("ANKE_DELVIS_OMGJOERING_TIL_GUNST"),
    ANKE_TIL_UGUNST("ANKE_TIL_UGUNST"),
    UDEFINERT("-"),
    ;
    private static final Map<String, AnkeVurderingOmgjør> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "ANKE_VURDERING_OMGJOER";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private AnkeVurderingOmgjør(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static AnkeVurderingOmgjør fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent AnkeVurderingOmgjør: " + kode);
        }
        return ad;
    }
    public static Map<String, AnkeVurderingOmgjør> kodeMap() {
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

