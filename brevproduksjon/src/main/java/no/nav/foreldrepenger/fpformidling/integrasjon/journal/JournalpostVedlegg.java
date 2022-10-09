package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public interface JournalpostVedlegg {

    void tilknyttVedlegg(TilknyttVedleggRequest request, JournalpostId journalpostIdTil);

}
