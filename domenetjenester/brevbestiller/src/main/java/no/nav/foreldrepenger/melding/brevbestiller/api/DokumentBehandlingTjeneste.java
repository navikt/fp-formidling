package no.nav.foreldrepenger.melding.brevbestiller.api;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.melding.brevbestiller.dto.BrevmalDto;

public interface DokumentBehandlingTjeneste {
    List<BrevmalDto> hentBrevmalerFor(UUID behandlingUuid);

    boolean erDokumentProdusert(UUID behandlingUuid, String dokumentMalTypeKode);
}
