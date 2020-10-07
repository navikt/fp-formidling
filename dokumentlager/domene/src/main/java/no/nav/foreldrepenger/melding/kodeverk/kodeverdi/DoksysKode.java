package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum DoksysKode implements Kodeverdi{
    POSVED("000048"),
    INNHEN("000049"),
    HENLEG("000050"),
    AVSLAG("000051"),
    UENDRE("000052"),
    REVURD("000058"),
    FORLEN("000056"),
    INSSKR("000071"),
    INNVFP("000061"),
    OPPHOR("000085"),
    INNTID("000091"),
    AVSLFP("000080"),
    FRITKS("000096"),

    @Deprecated
    KLAGOV("000060"),
    @Deprecated
    KLAGNY("000059"),
    @Deprecated
    KLAGVE("000055"),
    @Deprecated
    KLAGAV("000054"),
    @Deprecated
    VEDMED("000114"),


    UDEFINERT("-")
    ;

    public static final String KODEVERK = "DOK_SYS_KODE"; //$NON-NLS-1$

    private static final Map<String, DoksysKode> KODER = new LinkedHashMap<>();


    private String kode;

    DoksysKode(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static DoksysKode fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Doksyskode: " + kode);
        }
        return ad;
    }

    public static DoksysKode fraKodeDefaultUdefinert(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        return KODER.getOrDefault(kode, UDEFINERT);
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
