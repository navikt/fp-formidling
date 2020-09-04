package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevproduksjonTjeneste;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;

@ApplicationScoped
public class DokgenBrevproduksjonTjeneste implements BrevproduksjonTjeneste {

    @Override
    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        return new byte[0];
    }

    @Override
    public List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        return null;
    }
}
