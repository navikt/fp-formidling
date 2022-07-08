package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2.Oppgave;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2.Oppgaver;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2.OpprettOppgaveRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2.Prioritet;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

@ApplicationScoped
public class OppgaverTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaverTjeneste.class);
    private static final int DEFAULT_OPPGAVEFRIST_DAGER = 1;

    private Oppgaver restKlient;

    OppgaverTjeneste() {
        // for cdi proxy
    }

    @Inject
    public OppgaverTjeneste(@JavaClient Oppgaver restKlient) {
        this.restKlient = restKlient;
    }

    public Oppgave opprettOppgave(Behandling behandling, JournalpostId journalpostId, String oppgaveBeskrivelse) {

        var request = createRequest(journalpostId, behandling.getFagsakBackend().getAktørId(),
                behandling.getFagsakBackend().getSaksnummer().getVerdi(), behandling.getBehandlendeEnhetId(), oppgaveBeskrivelse);

        var oppgave = restKlient.opprettetOppgave(request);
        LOG.info("Oprettet GOSYS oppgave: {}", oppgave);
        return oppgave;
    }

    private OpprettOppgaveRequest createRequest(JournalpostId journalpostId,
                                                AktørId aktørId,
                                                String saksnummer,
                                                String enhet,
                                                String beskrivelse) {
        return new OpprettOppgaveRequest(enhet, enhet, journalpostId.getVerdi(), null, saksnummer, aktørId.getId(), beskrivelse,
                "FMLI", "FOR", null, "VUR_KONS_YTE", null, LocalDate.now(), Prioritet.NORM,
                VirkedagUtil.fomVirkedag(LocalDate.now().plusDays(DEFAULT_OPPGAVEFRIST_DAGER)));
    }
}
