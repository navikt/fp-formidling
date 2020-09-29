package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost;

import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.DokumentTilknytt;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

@ApplicationScoped
public class TilknyttVedleggTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(TilknyttVedleggTjeneste.class);

    private JournalpostRestKlient journalpostRestKlient;

    TilknyttVedleggTjeneste() {
        //CDI
    }

    @Inject
    public TilknyttVedleggTjeneste(JournalpostRestKlient journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    public void knyttAlleVedleggTilDokument(Collection<InnsynDokument> vedlegg, JournalpostId journalpostId) {
        vedlegg.forEach(v -> knyttVedleggTilForsendelse(journalpostId, v.getJournalpostId(), v.getDokumentId()));
    }

    private void knyttVedleggTilForsendelse(JournalpostId knyttesTilJournalpostId, JournalpostId knyttesFraJournalpostId, String dokumentId) {
        try {
            tilknyttVedlegg(knyttesTilJournalpostId, knyttesFraJournalpostId, dokumentId);
        } catch (Exception e) {
            throw JournalpostFeil.FACTORY.klarteIkkeKnytteVedleggTilJournalpost(knyttesFraJournalpostId, knyttesTilJournalpostId, dokumentId, e).toException();
        }
    }

    private void tilknyttVedlegg(JournalpostId journalpostIdTil, JournalpostId journalpostIdFra, String dokumentInfoId) {
        LOG.info("Starter tilknytning av vedlegg til journalpostId {} fra journalpostId {} med dokumentInfoId {}", journalpostIdTil.getVerdi(), journalpostIdFra.getVerdi(), dokumentInfoId);
        journalpostRestKlient.tilknyttVedlegg(lagRequest(journalpostIdFra, dokumentInfoId), journalpostIdTil);
    }

    private TilknyttVedleggRequest lagRequest(JournalpostId journalpostIdFra, String dokumentInfoId) {
        return new TilknyttVedleggRequest(List.of(new DokumentTilknytt(journalpostIdFra.getVerdi(), dokumentInfoId)));
    }
}
