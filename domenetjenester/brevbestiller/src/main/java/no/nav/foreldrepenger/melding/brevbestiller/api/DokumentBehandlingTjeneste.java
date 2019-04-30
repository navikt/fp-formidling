package no.nav.foreldrepenger.melding.brevbestiller.api;

import java.util.List;

import no.nav.foreldrepenger.melding.brevbestiller.dto.BrevmalDto;

public interface DokumentBehandlingTjeneste {
    List<BrevmalDto> hentBrevmalerFor(Long behandlingId);

    boolean erDokumentProdusert(Long behandlingId, String dokumentMalTypeKode);
}
