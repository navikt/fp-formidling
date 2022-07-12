package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient.Oppgave;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient.Oppgaver;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient.OpprettOppgaveRequest;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

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
        var behandling = new Behandling.Builder()
                .medBehandlendeEnhetId(behandlendeEnhetId)
                .medFagsakBackend(FagsakBackend.ny()
                        .medAktørId(new AktørId(aktørId))
                        .medSaksnummer(saksnummer).build())
                .build();

        var id = 1234;
        when(klient.opprettetOppgave(any(OpprettOppgaveRequest.class))).thenReturn(new Oppgave(id, "test", "FOR", null, null));

        var journalpostId = "76543322";
        var oppgave = tjeneste.opprettOppgave(behandling, new JournalpostId(journalpostId), "beskrivelse");

        assertThat(oppgave).isNotNull();
        assertThat(oppgave.id()).isEqualTo(id);
    }
}
