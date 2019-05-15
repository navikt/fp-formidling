package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalRestriksjon;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;

public class SjekkDokumentTilgjengelig {
    private DokumentRepository dokumentRepository;

    public SjekkDokumentTilgjengelig(DokumentRepository dokumentRepository) {
        this.dokumentRepository = dokumentRepository;
    }

    boolean sjekkOmTilgjengelig(Behandling behandling, DokumentMalType mal) {
        DokumentMalRestriksjon restriksjon = mal.getDokumentMalRestriksjon();
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING.equals(restriksjon)) {
            return !behandling.erSaksbehandlingAvsluttet() && !behandling.erAvsluttet();
        }
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT.equals(restriksjon)) {
            return !(behandling.erSaksbehandlingAvsluttet() || behandling.erAvsluttet() || erDokumentProdusert(behandling.getUuid(), mal.getKode()));
        }
        return true;
    }

    boolean erDokumentProdusert(UUID behandlingUuId, String dokumentMalTypeKode) {
        DokumentMalType dokumentMalType = dokumentRepository.hentDokumentMalType(dokumentMalTypeKode);
        List<DokumentData> dokumentDatas = dokumentRepository.hentDokumentDataListe(behandlingUuId, dokumentMalType.getKode());
        Optional<DokumentData> dokumentData = dokumentDatas.stream().filter(dd -> dd.getBestiltTid() != null).findFirst();
        return dokumentData.isPresent();
    }
}
