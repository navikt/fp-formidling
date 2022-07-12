package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient;

public record Oppgave (long id, String oppgavetype, String tema, Oppgavestatus status, Prioritet prioritet) {}
