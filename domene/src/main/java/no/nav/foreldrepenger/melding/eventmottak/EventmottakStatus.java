package no.nav.foreldrepenger.melding.eventmottak;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum EventmottakStatus implements Kodeverdi {

    FEILET("FEILET"),
    FERDIG("FERDIG"),
    ;

    public static final String KODEVERK = "EVENTMOTTAK_STATUS"; //$NON-NLS-1$

    private static final Map<String, EventmottakStatus> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private EventmottakStatus(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static EventmottakStatus fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent FagsakYtelseType: " + kode);
        }
        return ad;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }


    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<EventmottakStatus, String> {
        @Override
        public String convertToDatabaseColumn(EventmottakStatus attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public EventmottakStatus convertToEntityAttribute(String dbData) {
            return dbData == null ? null : fraKode(dbData);
        }
    }

}
