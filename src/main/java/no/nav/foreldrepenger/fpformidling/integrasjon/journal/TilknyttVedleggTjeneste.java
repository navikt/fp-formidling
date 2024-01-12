package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import java.util.Collection;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.vedtak.felles.integrasjon.dokarkiv.DokArkiv;

import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.TilknyttVedleggRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.domene.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.fpformidling.domene.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class TilknyttVedleggTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(TilknyttVedleggTjeneste.class);

    private DokArkiv dokArkivKlient;

    TilknyttVedleggTjeneste() {
        //CDI
    }

    @Inject
    public TilknyttVedleggTjeneste(DokArkiv dokArkivKlient) {
        this.dokArkivKlient = dokArkivKlient;
    }

    public void knyttAlleVedleggTilDokument(Collection<InnsynDokument> vedlegg, JournalpostId journalpostId) {
        vedlegg.forEach(v -> knyttVedleggTilForsendelse(journalpostId, v.journalpostId(), v.dokumentId()));
    }

    private void knyttVedleggTilForsendelse(JournalpostId knyttesTilJournalpostId, JournalpostId knyttesFraJournalpostId, String dokumentId) {
        try {
            tilknyttVedlegg(knyttesTilJournalpostId, knyttesFraJournalpostId, dokumentId);
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-156534",
                String.format("Klarte ikke knytte %s til %s med dokumentId %s.", knyttesFraJournalpostId, knyttesTilJournalpostId, dokumentId), e);
        }
    }

    private void tilknyttVedlegg(JournalpostId journalpostIdTil, JournalpostId journalpostIdFra, String dokumentInfoId) {
        LOG.info("Knytter vedlegget med journalpostid {} og dokumentInfoId {} til innsynsbrev med journalpostId {}", journalpostIdFra.getVerdi(),
            dokumentInfoId, journalpostIdTil.getVerdi());
        dokArkivKlient.tilknyttVedlegg(lagRequest(journalpostIdFra, dokumentInfoId), journalpostIdTil.getVerdi());
    }

    private TilknyttVedleggRequest lagRequest(JournalpostId journalpostIdFra, String dokumentInfoId) {
        return new TilknyttVedleggRequest(List.of(new TilknyttVedleggRequest.DokumentTilknytt(journalpostIdFra.getVerdi(), dokumentInfoId)));
    }
}
