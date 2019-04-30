package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.List;
import java.util.Optional;

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

    public boolean sjekkOmTilgjengelig(Behandling behandling, DokumentMalType mal) {
        DokumentMalRestriksjon restriksjon = mal.getDokumentMalRestriksjon();
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING.equals(restriksjon)) {
            return !behandling.erSaksbehandlingAvsluttet() && !behandling.erAvsluttet();
        }
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT.equals(restriksjon)) {
            return !(behandling.erSaksbehandlingAvsluttet() || behandling.erAvsluttet() || erDokumentProdusert(behandling.getId(), mal.getKode()));
        }
        return true;
    }

    public boolean erDokumentProdusert(Long behandlingId, String dokumentMalTypeKode) {
        DokumentMalType dokumentMalType = dokumentRepository.hentDokumentMalType(dokumentMalTypeKode);
        List<DokumentData> dokumentDatas = dokumentRepository.hentDokumentDataListe(behandlingId, dokumentMalType.getKode());
        Optional<DokumentData> dokumentData = dokumentDatas.stream().filter(dd -> dd.getBestiltTid() != null).findFirst();
        return dokumentData.isPresent();
    }
}
