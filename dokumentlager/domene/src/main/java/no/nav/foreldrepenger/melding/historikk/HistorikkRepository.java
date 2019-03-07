package no.nav.foreldrepenger.melding.historikk;

import java.util.List;

public interface HistorikkRepository {

    void lagre(DokumentHistorikkinnslag dokumentHistorikkinnslag);

    List<DokumentHistorikkinnslag> hentInnslagForBehandling(long behandlingId);

}
