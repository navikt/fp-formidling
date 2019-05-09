package no.nav.foreldrepenger.melding.historikk;

import java.util.List;
import java.util.UUID;

public interface HistorikkRepository {

    void lagre(DokumentHistorikkinnslag dokumentHistorikkinnslag);

    List<DokumentHistorikkinnslag> hentInnslagForBehandling(UUID behandlingUUID);

    DokumentHistorikkinnslag hentInnslagMedId(long historikkinnslagId);

}
