package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalRestriksjon;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;

@ApplicationScoped
public class SjekkDokumentTilgjengelig {
    private HendelseRepository hendelseRepository;

    public SjekkDokumentTilgjengelig() {
    }

    @Inject
    public SjekkDokumentTilgjengelig(HendelseRepository hendelseRepository) {
        this.hendelseRepository = hendelseRepository;
    }

    boolean sjekkOmTilgjengelig(Behandling behandling, DokumentMalType mal) {
        DokumentMalRestriksjon restriksjon = mal.getDokumentMalRestriksjon();
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING.equals(restriksjon)) {
            return !behandling.erSaksbehandlingAvsluttet() && !behandling.erAvsluttet();
        }
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT.equals(restriksjon)) {
            return !(behandling.erSaksbehandlingAvsluttet() || behandling.erAvsluttet() || erDokumentBestilt(behandling.getUuid(), mal.getKode()));
        }
        return true;
    }

    boolean erDokumentBestilt(UUID behandlingUuId, String dokumentMalTypeKode) {
        return hendelseRepository.erDokumentHendelseMottatt(behandlingUuId, dokumentMalTypeKode);
    }
}
