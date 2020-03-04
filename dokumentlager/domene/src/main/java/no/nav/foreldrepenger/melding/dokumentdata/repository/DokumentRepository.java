package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public interface DokumentRepository {

    void lagre(DokumentData dokumentData);

    DokumentMalType hentDokumentMalType(String kode);

    List<DokumentMalType> hentAlleDokumentMalTyper();
}
