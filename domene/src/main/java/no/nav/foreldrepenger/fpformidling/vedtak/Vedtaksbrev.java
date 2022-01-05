package no.nav.foreldrepenger.fpformidling.vedtak;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Vedtaksbrev implements Kodeverdi {

    AUTOMATISK("AUTOMATISK"),
    FRITEKST("FRITEKST"),
    INGEN("INGEN"),
    UDEFINERT("-"),
    ;

    private static final Map<String, Vedtaksbrev> KODER = new LinkedHashMap<>();


    public static final String KODEVERK = "VEDTAKSBREV";

    private String kode;

    private Vedtaksbrev(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static Vedtaksbrev fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Vedtaksbrev: " + kode);
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

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<Vedtaksbrev, String> {
        @Override
        public String convertToDatabaseColumn(Vedtaksbrev attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public Vedtaksbrev convertToEntityAttribute(String dbData) {
            return dbData == null ? null : fraKode(dbData);
        }
    }
}
