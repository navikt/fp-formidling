package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient;

import java.util.UUID;

record ErrorResponse(UUID uuid, String feilmelding) {}
