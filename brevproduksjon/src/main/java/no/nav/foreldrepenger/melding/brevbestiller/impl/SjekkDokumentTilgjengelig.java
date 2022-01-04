package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalRestriksjon;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

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
        DokumentMalRestriksjon restriksjon = mal.getDokumentmalRestriksjon();
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING.equals(restriksjon)) {
            return !behandling.erSaksbehandlingAvsluttet() && !behandling.erAvsluttet();
        }
        if (DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT.equals(restriksjon)) {
            return !(behandling.erSaksbehandlingAvsluttet() || behandling.erAvsluttet() || erDokumentBestilt(behandling.getUuid(), mal));
        }
        return true;
    }

    boolean erDokumentBestilt(UUID behandlingUuId, DokumentMalType dokumentMalType) {
        return hendelseRepository.erDokumentHendelseMottatt(behandlingUuId, dokumentMalType);
    }
}
