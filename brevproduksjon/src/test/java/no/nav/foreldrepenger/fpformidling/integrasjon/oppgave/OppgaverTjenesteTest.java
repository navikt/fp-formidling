package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
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
        var behandling = new Behandling.Builder()
                .medBehandlendeEnhetId(behandlendeEnhetId)
                .medFagsakBackend(FagsakBackend.ny()
                        .medAktørId(new AktørId(aktørId))
                        .medSaksnummer(saksnummer).build())
                .build();

        var id = 1234L;
        var captor = ArgumentCaptor.forClass(OpprettOppgave.class);
        when(klient.opprettetOppgave(captor.capture())).thenReturn(new Oppgave(id, null, null, null, null,  null, null,
                null, null, 1, null, null, null, null, null));

        var journalpostId = "76543322";
        var oppgave = tjeneste.opprettOppgave(behandling, new JournalpostId(journalpostId), "beskrivelse");

        var request = captor.getValue();
        assertThat(oppgave).isNotNull();
        assertThat(oppgave.id()).isEqualTo(id);
        assertThat(request.aktoerId()).isEqualTo(aktørId);
        assertThat(request.journalpostId()).isEqualTo(journalpostId);
        assertThat(request.tildeltEnhetsnr()).isEqualTo(behandlendeEnhetId);
    }
}
