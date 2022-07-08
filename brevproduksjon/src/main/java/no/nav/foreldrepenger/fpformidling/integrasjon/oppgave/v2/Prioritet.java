package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Prioritet {
    HOY,
    @JsonEnumDefaultValue
    NORM,
    LAV
}
