package no.nav.foreldrepenger.melding.integrasjon.journal.dto;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum BrukerIdType {
    @JsonEnumDefaultValue
    UKJENT,
    AKTOERID,
    FNR,
    ORGNR
}
