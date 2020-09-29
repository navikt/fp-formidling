package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.FerdigstillJournalpostRequest;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

@ApplicationScoped
public class FerdigstillJournalpostTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(FerdigstillJournalpostTjeneste.class);
    private static final String AUTOMATISK_JOURNALFØRENDE_ENHET = "9999";

    private JournalpostRestKlient journalpostRestKlient;

    FerdigstillJournalpostTjeneste() {
        //CDI
    }

    @Inject
    public FerdigstillJournalpostTjeneste(JournalpostRestKlient journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    public void ferdigstillForsendelse(JournalpostId journalpostId) {
        LOG.info("Starter ferdigstilling av journalpostId {}", journalpostId);
        try {
            journalpostRestKlient.ferdigstillJournalpost(lagRequest(), journalpostId);
        } catch (Exception e) {
            throw JournalpostFeil.FACTORY.klarteIkkeFerdigstilleJournalpost(journalpostId, e).toException();
        }
    }

    private FerdigstillJournalpostRequest lagRequest() {
        return new FerdigstillJournalpostRequest(AUTOMATISK_JOURNALFØRENDE_ENHET);
    }
}
