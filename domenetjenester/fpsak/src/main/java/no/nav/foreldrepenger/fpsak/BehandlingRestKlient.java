package no.nav.foreldrepenger.fpsak;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;

public interface BehandlingRestKlient {
    Optional<BehandlingDto> hentBehandling(BehandlingIdDto behandlingIdDto, boolean systembruker);

    Optional<PersonopplysningDto> hentPersonopplysninger(BehandlingIdDto behandlingIdDto, List<BehandlingResourceLinkDto> resourceLinkDtos, boolean systembruker);

    Optional<VergeDto> hentVerge(BehandlingIdDto behandlingIdDto, List<BehandlingResourceLinkDto> resourceLinkDtos, boolean systembruker);
}
