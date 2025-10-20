package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.brevGrunnlag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.oppgave.v1.Oppgave;
import no.nav.vedtak.felles.integrasjon.oppgave.v1.Oppgaver;
import no.nav.vedtak.felles.integrasjon.oppgave.v1.OpprettOppgave;

@ExtendWith(MockitoExtension.class)
class OppgaverTjenesteTest {

    private OppgaverTjeneste tjeneste;
    @Mock
    private Oppgaver klient;

    @BeforeEach
    void setUp() {
        tjeneste = new OppgaverTjeneste(klient);
    }

    @Test
    void opprettOppgave() {
        var behandlendeEnhetId = "1234";
        var aktørId = "12345678";
        var saksnummer = "98766544";
        var behandling = brevGrunnlag().behandlendeEnhet(behandlendeEnhetId).aktørId(aktørId).saksnummer(saksnummer).build();

        var oppgaveId = 1234L;
        var oppgaveBeskrivelse = "beskrivelse";
        var captor = ArgumentCaptor.forClass(OpprettOppgave.class);

        var mockOppgave = new Oppgave(oppgaveId,              // id
            null,                   // versjon
            null,                   // oppgavetype
            null,                   // status
            null,                   // tema
            null,                   // opprettetTidspunkt
            null,                   // aktivDato
            null,                   // fristFerdigstillelse
            null,                   // prioritet
            1,                      // antallDager
            null,                   // behandlingstema
            null,                   // behandlingstype
            null,                   // aktoerId
            null,                   // saksreferanse
            null,                   // tildeltEnhetsnr
            oppgaveBeskrivelse,     // beskrivelse
            "ressurs"               // ressurs
        );

        when(klient.opprettetOppgave(captor.capture())).thenReturn(mockOppgave);

        var journalpostId = "76543322";
        var oppgave = tjeneste.opprettOppgave(behandling, new JournalpostId(journalpostId), oppgaveBeskrivelse);

        var request = captor.getValue();
        assertThat(oppgave).isNotNull();
        assertThat(oppgave.id()).isEqualTo(oppgaveId);
        assertThat(request.aktoerId()).isEqualTo(aktørId);
        assertThat(request.journalpostId()).isEqualTo(journalpostId);
        assertThat(request.tildeltEnhetsnr()).isEqualTo(behandlendeEnhetId);
    }
}
