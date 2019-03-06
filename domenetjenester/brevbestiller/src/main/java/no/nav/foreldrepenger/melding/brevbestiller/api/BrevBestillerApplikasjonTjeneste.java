package no.nav.foreldrepenger.melding.brevbestiller.api;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;

public interface BrevBestillerApplikasjonTjeneste {
    byte[] forhandsvisBrev(BehandlingDto behandlingDto, DokumentHendelseDto hendelseDto);

    void bestillBrev(BehandlingDto behandlingDto, DokumentHendelseDto hendelseDto);
}
