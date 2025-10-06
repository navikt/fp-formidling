package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.oppgave.v1.Oppgave;
import no.nav.vedtak.felles.integrasjon.oppgave.v1.Oppgaver;
import no.nav.vedtak.felles.integrasjon.oppgave.v1.Oppgavetype;
import no.nav.vedtak.felles.integrasjon.oppgave.v1.OpprettOppgave;

@ApplicationScoped
public class OppgaverTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaverTjeneste.class);

    private Oppgaver restKlient;

    OppgaverTjeneste() {
        // for cdi proxy
    }

    @Inject
    public OppgaverTjeneste(Oppgaver restKlient) {
        this.restKlient = restKlient;
    }

    public Oppgave opprettOppgave(Behandling behandling, JournalpostId journalpostId, String oppgaveBeskrivelse) {

        var request = OpprettOppgave.getBuilderTemaFOR(Oppgavetype.VURDER_KONSEKVENS_YTELSE,
                no.nav.vedtak.felles.integrasjon.oppgave.v1.Prioritet.NORM, 1)
            .medAktoerId(behandling.getFagsak().getAktørId().getId())
            .medSaksreferanse(behandling.getFagsak().getSaksnummer().getVerdi())
            .medTildeltEnhetsnr(behandling.getBehandlendeEnhetId())
            .medJournalpostId(journalpostId.getVerdi())
            .medBeskrivelse(oppgaveBeskrivelse)
            .build();

        var oppgave = restKlient.opprettetOppgave(request);
        LOG.info("Oprettet GOSYS oppgave: {}", oppgave);
        return oppgave;
    }

}
