package no.nav.foreldrepenger.melding.brevbestiller.api;

import java.util.List;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;

public interface BrevproduksjonTjeneste {
    byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal);

    List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal);
}
