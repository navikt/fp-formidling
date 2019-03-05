package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentHendelse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;

public interface DokumentRepository {

    void lagre(DokumentHendelse dokumentHendelse);

    void lagre(EventmottakFeillogg eventmottakFeillogg);

    Optional<DokumentHendelse> hentDokumentHendelseMedId(long id);

    List<DokumentHendelse> hentDokumentHendelserForBehandling(long behandlingId);

    DokumentMalType hentDokumentMalType(String kode);

}
