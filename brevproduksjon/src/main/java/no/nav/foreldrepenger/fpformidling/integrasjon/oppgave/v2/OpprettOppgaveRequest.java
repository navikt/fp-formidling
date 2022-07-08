package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

public record OpprettOppgaveRequest(String tildeltEnhetsnr,
                                    String opprettetAvEnhetsnr,
                                    @NotNull String journalpostId,
                                    String behandlesAvApplikasjon,
                                    String saksreferanse,
                                    @NotNull String aktoerId,
                                    @NotNull String beskrivelse,
                                    String temagruppe,
                                    @NotNull String tema,
                                    @NotNull String behandlingstema,
                                    @NotNull String oppgavetype,
                                    String behandlingstype,
                                    @NotNull LocalDate aktivDato,
                                    @NotNull Prioritet prioritet,
                                    LocalDate fristFerdigstillelse) {
}
