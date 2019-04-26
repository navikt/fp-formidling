package no.nav.foreldrepenger.fpsak;

import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;

public interface BehandlingRestKlient {

    BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto);

    PersonopplysningDto hentPersonopplysninger(List<BehandlingResourceLinkDto> resourceLinkDtos);

    VergeDto hentVerge(List<BehandlingResourceLinkDto> resourceLinkDtos);

    FamiliehendelseDto hentFamiliehendelse(List<BehandlingResourceLinkDto> resourceLinkDtos);

    SoknadDto hentSoknad(List<BehandlingResourceLinkDto> resourceLinkDtos);

    BeregningsresultatEngangsstønadDto hentBeregningsresultatEngangsstønad(List<BehandlingResourceLinkDto> resourceLinkDtos);

    BeregningsresultatMedUttaksplanDto hentBeregningsresultatForeldrepenger(List<BehandlingResourceLinkDto> resourceLinkDtos);

    InntektArbeidYtelseDto hentInntektArbeidYtelseDto(List<BehandlingResourceLinkDto> resourceLinkDtos);

    KlagebehandlingDto hentKlagebehandling(List<BehandlingResourceLinkDto> resourceLinkDtos);

    InnsynsbehandlingDto hentInnsynsbehandling(List<BehandlingResourceLinkDto> resourceLinkDtos);

    BeregningsgrunnlagDto hentBeregningsgrunnlag(List<BehandlingResourceLinkDto> resourceLinkDtos);

    VilkårDto hentVilkår(List<BehandlingResourceLinkDto> resourceLinkDtos);

    FagsakDto hentFagsak(List<BehandlingResourceLinkDto> resourceLinkDtos);

    UttakResultatPerioderDto hentUttaksresultat(List<BehandlingResourceLinkDto> resourceLinkDtos);
}
