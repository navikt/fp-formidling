package no.nav.foreldrepenger.melding.brevbestiller.api;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

public interface BrevBestillerApplikasjonTjeneste {
    byte[] forhandsvisBrev(BehandlingDto behandlingDto, DokumentHendelseDto hendelseDto);

    void bestillBrev(DokumentHendelse dokumentHendelse);
}
