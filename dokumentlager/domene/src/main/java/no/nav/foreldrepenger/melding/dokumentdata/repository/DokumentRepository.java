package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.SaksbehandlerTekst;

public interface DokumentRepository {

    void lagre(DokumentData dokumentData);

    DokumentMalType hentDokumentMalType(String kode);

    List<DokumentMalType> hentAlleDokumentMalTyper();

    List<DokumentData> hentDokumentDataListe(UUID behandlingUuid, String dokumentmal);

    void lagre(SaksbehandlerTekst saksbehandlerTekst);

    Optional<SaksbehandlerTekst> hentSaksbehandlerTekstHvisEksisterer(UUID behandlingUuid);
}
