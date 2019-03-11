package no.nav.foreldrepenger.melding.brevbestiller.api;

import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;

public interface BrevBestillerApplikasjonTjeneste {
    byte[] forhandsvisBrev(DokumentHendelseDto hendelseDto);

    DokumentHistorikkinnslag bestillBrev(DokumentHendelse dokumentHendelse);
}
