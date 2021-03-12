package no.nav.foreldrepenger.melding.brevmapper;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class DokumentdataMapperProvider {

    public DokumentdataMapper getDokumentdataMapper(DokumentMalType dokumentMalType) {
        if (DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_BREVMALER.contains(dokumentMalType)) {
            return getMapper(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID);
        }
        return getMapper(dokumentMalType);
    }

    private DokumentdataMapper getMapper(DokumentMalType dokumentMalType) {
        return DokumentMalTypeRef.Lookup.find(DokumentdataMapper.class, dokumentMalType.getKode()).orElseThrow();
    }
}
