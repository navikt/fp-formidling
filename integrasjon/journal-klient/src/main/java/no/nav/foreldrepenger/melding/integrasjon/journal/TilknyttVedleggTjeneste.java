package no.nav.foreldrepenger.melding.integrasjon.journal;

import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.DokumentTilknytt;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

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
            throw new TekniskException("FPFORMIDLING-156534", String.format("Klarte ikke knytte %s til %s med dokumentId %s.", knyttesFraJournalpostId, knyttesTilJournalpostId,dokumentId ), e);
        }
    }

    private void tilknyttVedlegg(JournalpostId journalpostIdTil, JournalpostId journalpostIdFra, String dokumentInfoId) {
        LOG.info("Knytter vedlegget med journalpostid {} og dokumentInfoId {} til innsynsbrev med journalpostId {}", journalpostIdFra.getVerdi(), dokumentInfoId, journalpostIdTil.getVerdi());
        journalpostRestKlient.tilknyttVedlegg(lagRequest(journalpostIdFra, dokumentInfoId), journalpostIdTil);
    }

    private TilknyttVedleggRequest lagRequest(JournalpostId journalpostIdFra, String dokumentInfoId) {
        return new TilknyttVedleggRequest(List.of(new DokumentTilknytt(journalpostIdFra.getVerdi(), dokumentInfoId)));
    }
}
