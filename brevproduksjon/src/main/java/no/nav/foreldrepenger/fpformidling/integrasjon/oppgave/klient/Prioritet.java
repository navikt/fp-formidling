package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Prioritet {
    HOY,
    @JsonEnumDefaultValue
    NORM,
    LAV
}
