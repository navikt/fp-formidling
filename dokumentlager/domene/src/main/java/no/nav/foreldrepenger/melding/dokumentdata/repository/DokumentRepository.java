package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public interface DokumentRepository {

    void lagre(DokumentData dokumentData);

    DokumentMalType hentDokumentMalType(String kode);

    List<DokumentMalType> hentAlleDokumentMalTyper();

    List<DokumentData> hentDokumentDataListe(UUID behandlingUuid, String dokumentmal);
}
