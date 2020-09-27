package no.nav.foreldrepenger.melding.familiehendelse;

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
public enum FamilieHendelseType implements Kodeverdi {

    ADOPSJON("ADPSJN"),
    OMSORG("OMSRGO"),
    FØDSEL("FODSL"),
    TERMIN("TERM"),
    UDEFINERT("-"),
    ;

    private static final Map<String, FamilieHendelseType> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "FAMILIE_HENDELSE_TYPE";

    private String kode;

    private FamilieHendelseType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static FamilieHendelseType fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent FamilieHendelseType: " + kode);
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
