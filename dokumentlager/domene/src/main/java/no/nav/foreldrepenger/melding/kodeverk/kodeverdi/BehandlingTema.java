package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum BehandlingTema implements Kodeverdi {
    ENGANGSSTØNAD("ENGST", "ab0327", "Engangsstønad"),
    ENGANGSSTØNAD_FØDSEL("ENGST_FODS", "ab0050", "Engangsstønad ved fødsel"),
    ENGANGSSTØNAD_ADOPSJON("ENGST_ADOP", "ab0027", "Engangsstønad ved adopsjon"),

    FORELDREPENGER("FORP", "ab0326", "Foreldrepenger"),
    FORELDREPENGER_FØDSEL("FORP_FODS", "ab0047", "Foreldrepenger ved fødsel"),
    FORELDREPENGER_ADOPSJON("FORP_ADOP", "ab0072", "Foreldrepenger ved adopsjon"),

    SVANGERSKAPSPENGER("SVP", "ab0126", "Svangerskapspenger"),

    OMS("OMS", "ab0271", "Omsorgspenger, Pleiepenger og opplæringspenger"),
    OMS_OMSORG("OMS_OMSORG", "ab0149", "Omsorgspenger"),
    OMS_OPP("OMS_OPP", "ab0141", "Opplæringspenger"),
    OMS_PLEIE_BARN("OMS_PLEIE_BARN", "ab0069", "Pleiepenger sykt barn"),
    OMS_PLEIE_BARN_NY("OMS_PLEIE_BARN_NY", "ab0320", "Pleiepenger sykt barn ny ordning"),
    OMS_PLEIE_INSTU("OMS_PLEIE_INSTU", "ab0153", "Pleiepenger ved institusjonsopphold"),

    UDEFINERT("-", null, "Ikke definert"),
    ;

    private static final Map<String, BehandlingTema> KODER = new LinkedHashMap<>();
    private static final Map<String, BehandlingTema> OFFISIELLE_KODER = new LinkedHashMap<>();
    private static final Map<String, BehandlingTema> ALLE_TERMNAVN = new LinkedHashMap<>();

    public static final String KODEVERK = "BEHANDLING_TEMA";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
            if (v.offisiellKode != null) {
                OFFISIELLE_KODER.putIfAbsent(v.offisiellKode, v);
            }
            if (v.termnavn != null) {
                ALLE_TERMNAVN.putIfAbsent(v.termnavn, v);
            }
        }
    }

    private String kode;

    @JsonIgnore
    private String offisiellKode;

    @JsonIgnore
    private String termnavn;

    private BehandlingTema(String kode, String offisiellKode, String termnavn) {
        this.kode = kode;
        this.offisiellKode = offisiellKode;
        this.termnavn = termnavn;
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

    public static BehandlingTema fraKodeDefaultUdefinert(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return UDEFINERT;
        }
        return KODER.getOrDefault(kode, UDEFINERT);
    }

    public static BehandlingTema fraOffisiellKode(String kode) {
        if (kode == null) {
            return UDEFINERT;
        }
        return OFFISIELLE_KODER.getOrDefault(kode, UDEFINERT);
    }

    public static BehandlingTema fraTermNavn(String termnavn) {
        if (termnavn == null) {
            return UDEFINERT;
        }
        return ALLE_TERMNAVN.getOrDefault(termnavn, UDEFINERT);
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

    public String getOffisiellKode() {
        return offisiellKode;
    }

    public String getTermNavn() {
        return termnavn;
    }

}
