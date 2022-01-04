package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum BehandlingTema implements Kodeverdi, KodeverdiMedOffisiellKode {

    ENGANGSSTØNAD("ENGST", "ab0327"),
    ENGANGSSTØNAD_FØDSEL("ENGST_FODS", "ab0050"),
    ENGANGSSTØNAD_ADOPSJON("ENGST_ADOP", "ab0027"),

    FORELDREPENGER("FORP", "ab0326"),
    FORELDREPENGER_FØDSEL("FORP_FODS", "ab0047"),
    FORELDREPENGER_ADOPSJON("FORP_ADOP", "ab0072"),

    SVANGERSKAPSPENGER("SVP", "ab0126"),

    OMS("OMS", "ab0271"),
    OMS_OMSORG("OMS_OMSORG", "ab0149"),
    OMS_OPP("OMS_OPP", "ab0141"),
    OMS_PLEIE_BARN("OMS_PLEIE_BARN", "ab0069"),
    OMS_PLEIE_BARN_NY("OMS_PLEIE_BARN_NY", "ab0320"),
    OMS_PLEIE_INSTU("OMS_PLEIE_INSTU", "ab0153"),

    UDEFINERT("-", null),
    ;

    private static final Map<String, BehandlingTema> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "BEHANDLING_TEMA";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    @JsonIgnore
    private String offisiellKode;


    BehandlingTema(String kode, String offisiellKode) {
        this.kode = kode;
        this.offisiellKode = offisiellKode;
    }

    @JsonCreator
    public static BehandlingTema fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Tema: " + kode);
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
    public String getOffisiellKode() {
        return offisiellKode;
    }

}
