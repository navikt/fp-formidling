package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import jakarta.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class DokumentdataMapperProvider {

    public DokumentdataMapper getDokumentdataMapper(DokumentMalType dokumentMalType) {
        if (DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_BREVMALER.contains(dokumentMalType)) {
            return getMapper(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID);
        }
        return getMapper(dokumentMalType);
    }

    private DokumentdataMapper getMapper(DokumentMalType dokumentMalType) {
        return DokumentMalTypeRef.Lookup.find(DokumentdataMapper.class, dokumentMalType.getKode())
            .orElseThrow(() -> new IllegalStateException("Kunne ikke finne en mapper for dokumentMalType: '{}'" + dokumentMalType.getKode()));
    }
}
