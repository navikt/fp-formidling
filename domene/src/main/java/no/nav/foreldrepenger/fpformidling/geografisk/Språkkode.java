package no.nav.foreldrepenger.fpformidling.geografisk;


import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Språkkode implements Kodeverdi {

    @JsonEnumDefaultValue
    NB("NB"),
    NN("NN"),
    EN("EN"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    Språkkode(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<Språkkode, String> {
        @Override
        public String convertToDatabaseColumn(Språkkode attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public Språkkode convertToEntityAttribute(String dbData) {
            return dbData == null ? null : defaultNorsk(dbData);
        }
    }

    public static final Språkkode defaultNorsk(String kode) {
        return finnSpråkIgnoreCase(kode).orElse(Språkkode.NB);
    }

    private static Optional<Språkkode> finnSpråkIgnoreCase(String kode) {
        if (kode == null) {
            return Optional.empty();
        }
        return Stream.of(NB, NN, EN).filter(sp -> kode.equalsIgnoreCase(sp.getKode())).findFirst();
    }
}
