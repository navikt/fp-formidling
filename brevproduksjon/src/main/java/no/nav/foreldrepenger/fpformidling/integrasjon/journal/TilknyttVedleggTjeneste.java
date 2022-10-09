package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class TilknyttVedleggTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(TilknyttVedleggTjeneste.class);

    private JournalpostVedlegg journalpostRestKlient;

    TilknyttVedleggTjeneste() {
        //CDI
    }

    @Inject
    public TilknyttVedleggTjeneste(JournalpostVedlegg journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    public void knyttAlleVedleggTilDokument(Collection<InnsynDokument> vedlegg, JournalpostId journalpostId) {
        vedlegg.forEach(v -> knyttVedleggTilForsendelse(journalpostId, v.journalpostId(), v.dokumentId()));
    }

    private void knyttVedleggTilForsendelse(JournalpostId knyttesTilJournalpostId, JournalpostId knyttesFraJournalpostId, String dokumentId) {
        try {
            tilknyttVedlegg(knyttesTilJournalpostId, knyttesFraJournalpostId, dokumentId);
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-156534", String.format("Klarte ikke knytte %s til %s med dokumentId %s.", knyttesFraJournalpostId, knyttesTilJournalpostId,dokumentId ), e);
        }
    }

    private void tilknyttVedlegg(JournalpostId journalpostIdTil, JournalpostId journalpostIdFra, String dokumentInfoId) {
        LOG.info("Knytter vedlegget med journalpostid {} og dokumentInfoId {} til innsynsbrev med journalpostId {}", journalpostIdFra.getVerdi(), dokumentInfoId, journalpostIdTil.getVerdi());
        journalpostRestKlient.tilknyttVedlegg(lagRequest(journalpostIdFra, dokumentInfoId), journalpostIdTil);
    }

    private TilknyttVedleggRequest lagRequest(JournalpostId journalpostIdFra, String dokumentInfoId) {
        return new TilknyttVedleggRequest(List.of(new TilknyttVedleggRequest.DokumentTilknytt(journalpostIdFra.getVerdi(), dokumentInfoId)));
    }
}
