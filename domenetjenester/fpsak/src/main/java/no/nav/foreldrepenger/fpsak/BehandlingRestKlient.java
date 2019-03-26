package no.nav.foreldrepenger.fpsak;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;

public interface BehandlingRestKlient {

    BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto);

    Optional<PersonopplysningDto> hentPersonopplysninger(List<BehandlingResourceLinkDto> resourceLinkDtos);

    VergeDto hentVerge(List<BehandlingResourceLinkDto> resourceLinkDtos);

    FamiliehendelseDto hentFamiliehendelse(List<BehandlingResourceLinkDto> resourceLinkDtos);

    BeregningsresultatEngangsstønadDto hentBeregningsresultatEngangsstønad(List<BehandlingResourceLinkDto> resourceLinkDtos);
}
