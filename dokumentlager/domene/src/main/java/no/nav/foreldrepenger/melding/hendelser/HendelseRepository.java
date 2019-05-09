package no.nav.foreldrepenger.melding.hendelser;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;

public interface HendelseRepository {

    void lagre(DokumentHendelse dokumentHendelse);

    void lagre(EventmottakFeillogg eventmottakFeillogg);

    DokumentHendelse hentDokumentHendelseMedId(long id);

    List<DokumentHendelse> hentDokumentHendelserForBehandling(UUID behandlingUuid);
}
