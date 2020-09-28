package no.nav.foreldrepenger.melding.personopplysning;

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
public enum PersonstatusType implements Kodeverdi {

    ABNR("ABNR"),
    ADNR("ADNR"),
    BOSA("BOSA"),
    DØD("DØD"),
    DØDD("DØDD"),
    FOSV("FOSV"),
    FØDR("FØDR"),
    UFUL("UFUL"),
    UREG("UREG"),
    UTAN("UTAN"),
    UTPE("UTPE"),
    UTVA("UTVA"),
    
    UDEFINERT("-"),
    
    ;

    private static final Map<String, PersonstatusType> KODER = new LinkedHashMap<>();
    
    public static final String KODEVERK = "PERSONSTATUS_TYPE";
    
    private String kode;

    private PersonstatusType(String kode) {
        this.kode = kode;
    }

    public static boolean erDød(PersonstatusType personstatus) {
        return DØD.equals(personstatus) || DØDD.equals(personstatus);
    }
    

    @JsonCreator
    public static PersonstatusType fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent PersonstatusType: " + kode);
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
