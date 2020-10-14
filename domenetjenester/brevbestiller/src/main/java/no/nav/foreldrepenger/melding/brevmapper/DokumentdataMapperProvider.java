package no.nav.foreldrepenger.melding.brevmapper;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class DokumentdataMapperProvider {

    public DokumentdataMapper getDokumentdataMapper(DokumentMalType dokumentMalType) {
        return DokumentMalTypeRef.Lookup.find(DokumentdataMapper.class, dokumentMalType.getKode()).orElseThrow();
    }
}
