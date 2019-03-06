package no.nav.foreldrepenger.melding.dokumentdata.repository;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public interface DokumentRepository {

    DokumentMalType hentDokumentMalType(String kode);

}
