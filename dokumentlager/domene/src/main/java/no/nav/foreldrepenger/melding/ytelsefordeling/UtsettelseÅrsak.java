package no.nav.foreldrepenger.melding.ytelsefordeling;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum UtsettelseÅrsak implements Årsak {

    ARBEID("ARBEID"),
    FERIE("LOVBESTEMT_FERIE"),
    SYKDOM("SYKDOM"),
    INSTITUSJON_SØKER("INSTITUSJONSOPPHOLD_SØKER"),
    INSTITUSJON_BARN("INSTITUSJONSOPPHOLD_BARNET"),
    HV_OVELSE("HV_OVELSE"),
    NAV_TILTAK("NAV_TILTAK"),
    UDEFINERT("-"),
    ;
    private static final Map<String, UtsettelseÅrsak> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "UTSETTELSE_AARSAK_TYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    UtsettelseÅrsak(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static UtsettelseÅrsak fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent UtsettelseÅrsak: " + kode);
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
