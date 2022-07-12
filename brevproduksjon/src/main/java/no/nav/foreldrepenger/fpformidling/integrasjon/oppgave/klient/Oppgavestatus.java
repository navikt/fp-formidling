package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

enum Oppgavestatus {
    @JsonEnumDefaultValue
    OPPRETTET,
    AAPNET,
    UNDER_BEHANDLING,
    FERDIGSTILT,
    FEILREGISTRERT;
}
