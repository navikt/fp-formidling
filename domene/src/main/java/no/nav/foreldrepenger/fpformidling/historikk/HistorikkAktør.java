package no.nav.foreldrepenger.fpformidling.historikk;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum HistorikkAktør implements Kodeverdi {

    BESLUTTER("BESL"),
    SAKSBEHANDLER("SBH"),
    SØKER("SOKER"),
    ARBEIDSGIVER("ARBEIDSGIVER"),
    VEDTAKSLØSNINGEN("VL"),
    UDEFINERT("-"),
    ;

    private static final Map<String, HistorikkAktør> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @JsonValue
    private String kode;

    private HistorikkAktør(String kode) {
        this.kode = kode;
    }

    public static HistorikkAktør fraKode(String kode) {
        if (kode == null) {
            return null;
        }
        return Optional.ofNullable(KODER.get(kode))
                .orElseThrow(() -> new IllegalArgumentException("Ukjent HistorikkAktør: " + kode));
    }

    @Override
    public String getKode() {
        return kode;
    }


    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<HistorikkAktør, String> {
        @Override
        public String convertToDatabaseColumn(HistorikkAktør attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public HistorikkAktør convertToEntityAttribute(String dbData) {
            return dbData == null ? null : fraKode(dbData);
        }
    }
}
