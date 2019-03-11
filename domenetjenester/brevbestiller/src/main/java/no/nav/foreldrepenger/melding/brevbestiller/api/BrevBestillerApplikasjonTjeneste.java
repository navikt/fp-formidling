package no.nav.foreldrepenger.melding.brevbestiller.api;

import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

public interface BrevBestillerApplikasjonTjeneste {
    byte[] forhandsvisBrev(DokumentHendelseDto hendelseDto);

    void bestillBrev(DokumentHendelse dokumentHendelse);
}
