package no.nav.foreldrepenger.melding.brevbestiller.api;

import java.util.List;

import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;

public interface BrevBestillerApplikasjonTjeneste {
    byte[] forhandsvisBrev(DokumentbestillingDto dokumentbestillingDto);

    List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse);
}
