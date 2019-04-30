package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public interface DokumentRepository {

    Long lagre(DokumentAdresse adresse);

    DokumentMalType hentDokumentMalType(String kode);

    List<DokumentMalType> hentAlleDokumentMalTyper();

    List<DokumentData> hentDokumentDataListe(Long behandlingId, String dokumentmal);
}
