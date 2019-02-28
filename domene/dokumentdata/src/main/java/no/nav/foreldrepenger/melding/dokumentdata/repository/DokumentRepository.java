package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentHendelse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public interface DokumentRepository {

    void lagre(DokumentHendelse dokumentHendelse);

    Optional<DokumentHendelse> hentDokumentHendelseMedId(long id);

    List<DokumentHendelse> hentDokumentHendelserForBehandling(long behandlingId);

    DokumentMalType hentDokumentMalType(String kode);

}
