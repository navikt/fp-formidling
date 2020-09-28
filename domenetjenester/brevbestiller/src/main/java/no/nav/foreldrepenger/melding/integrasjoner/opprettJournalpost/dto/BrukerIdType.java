package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum BrukerIdType {
    @JsonEnumDefaultValue
    UKJENT,
    AKTOERID,
    FNR,
    ORGNR
}
