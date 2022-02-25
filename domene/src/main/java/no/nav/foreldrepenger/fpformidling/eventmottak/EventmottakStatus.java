package no.nav.foreldrepenger.fpformidling.eventmottak;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


public enum EventmottakStatus implements Kodeverdi {

    FEILET("FEILET"),
    FERDIG("FERDIG"),
    ;

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

    @Override
    public String getKode() {
        return kode;
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

        private static EventmottakStatus fraKode(String kode) {
            if (kode == null) {
                return null;
            }
            return Optional.ofNullable(KODER.get(kode))
                    .orElseThrow(() -> new IllegalArgumentException("Ukjent EventmottakStatus: " + kode));
        }
    }

}
