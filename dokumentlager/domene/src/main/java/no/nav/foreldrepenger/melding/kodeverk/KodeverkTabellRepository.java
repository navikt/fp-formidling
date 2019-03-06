package no.nav.foreldrepenger.melding.kodeverk;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public interface KodeverkTabellRepository {

    DokumentMalType finnDokumentMalType(String kode);
}
