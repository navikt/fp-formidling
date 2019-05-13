package no.nav.foreldrepenger.melding.brevbestiller.api;

import no.nav.foreldrepenger.melding.brevbestiller.dto.BestillBrevDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;

public interface BrevBestillerApplikasjonTjeneste {
    byte[] forhandsvisBrev(BestillBrevDto hendelseDto);

    DokumentHistorikkinnslag bestillBrev(DokumentHendelse dokumentHendelse);
}
