package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2;

public record Oppgave (long id, String oppgavetype, String tema, Oppgavestatus status, Prioritet prioritet) {}
