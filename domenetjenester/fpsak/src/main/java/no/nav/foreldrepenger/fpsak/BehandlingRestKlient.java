package no.nav.foreldrepenger.fpsak;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadDto;

public interface BehandlingRestKlient {

    BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto);

    Optional<PersonopplysningDto> hentPersonopplysninger(List<BehandlingResourceLinkDto> resourceLinkDtos);

    VergeDto hentVerge(List<BehandlingResourceLinkDto> resourceLinkDtos);

    FamiliehendelseDto hentFamiliehendelse(List<BehandlingResourceLinkDto> resourceLinkDtos);

    SoknadDto hentSoknad(List<BehandlingResourceLinkDto> resourceLinkDtos);

    BeregningsresultatEngangsstønadDto hentBeregningsresultatEngangsstønad(List<BehandlingResourceLinkDto> resourceLinkDtos);

    InntektArbeidYtelseDto hentInntektArbeidYtelseDto(List<BehandlingResourceLinkDto> resourceLinkDtos);

    KlagebehandlingDto hentKlagebehandling(List<BehandlingResourceLinkDto> resourceLinkDtos);

    InnsynsbehandlingDto hentInnsynsbehandling(List<BehandlingResourceLinkDto> resourceLinkDtos);

}
