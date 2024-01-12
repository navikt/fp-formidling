package no.nav.foreldrepenger.fpformidling.domene.vedtak;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Vedtaksbrev implements Kodeverdi {

    AUTOMATISK("AUTOMATISK"),
    FRITEKST("FRITEKST"),
    INGEN("INGEN"),
    UDEFINERT("-"),
    ;

    private static final Map<String, Vedtaksbrev> KODER = new LinkedHashMap<>();


    @JsonValue
    private String kode;

    private Vedtaksbrev(String kode) {
        this.kode = kode;
    }

    public static Vedtaksbrev fraKode(String kode) {
        if (kode == null) {
            return null;
        }
        return Optional.ofNullable(KODER.get(kode)).orElseThrow(() -> new IllegalArgumentException("Ukjent Vedtaksbrev: " + kode));
    }

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
