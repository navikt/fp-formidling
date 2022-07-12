package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient;

/**
 * Brukes til oppretting av GSAK oppgaver.
 * Swagger dokumentasjon: <a href="https://oppgave-q1.dev-fss-pub.nais.io/">Swagger API dokumentasjon i dev.</a>
 */
public interface Oppgaver {
    Oppgave opprettetOppgave(OpprettOppgaveRequest oppgave);
}
