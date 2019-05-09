package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public interface DokumentRepository {

    DokumentMalType hentDokumentMalType(String kode);

    List<DokumentMalType> hentAlleDokumentMalTyper();

    @Deprecated
    List<DokumentData> hentDokumentDataListe(Long behandlingId, String dokumentmal);
}
