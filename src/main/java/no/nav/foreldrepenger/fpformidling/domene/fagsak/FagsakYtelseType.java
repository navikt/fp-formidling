package no.nav.foreldrepenger.fpformidling.domene.fagsak;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum FagsakYtelseType implements Kodeverdi {

    ENGANGSTØNAD("ES"),
    FORELDREPENGER("FP"),
    SVANGERSKAPSPENGER("SVP"),
    UDEFINERT("-"),
    ;

    private static final Map<String, FagsakYtelseType> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @JsonValue
    private String kode;

    private FagsakYtelseType(String kode) {
        this.kode = kode;
    }

    public static FagsakYtelseType fraKode(String kode) {
        if (kode == null) {
            return null;
        }
        return Optional.ofNullable(KODER.get(kode)).orElseThrow(() -> new IllegalArgumentException("Ukjent FagsakYtelseType: " + kode));
    }

    @Override
    public String getKode() {
        return kode;
    }

    public final boolean gjelderEngangsstønad() {
        return ENGANGSTØNAD.equals(this);
    }

    public final boolean gjelderForeldrepenger() {
        return FORELDREPENGER.equals(this);
    }

    public final boolean gjelderSvangerskapspenger() {
        return SVANGERSKAPSPENGER.equals(this);
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<FagsakYtelseType, String> {
        @Override
        public String convertToDatabaseColumn(FagsakYtelseType attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public FagsakYtelseType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : fraKode(dbData);
        }
    }
}
