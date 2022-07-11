package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2;

import java.util.UUID;

record ErrorResponse(UUID uuid, String feilmelding) {}
