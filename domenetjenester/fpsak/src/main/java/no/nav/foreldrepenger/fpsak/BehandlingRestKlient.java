package no.nav.foreldrepenger.fpsak;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.MottattDokumentDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamilieHendelseGrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.klage.MottattKlagedokumentDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.SaldoerDto;
import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;

public interface BehandlingRestKlient {

    BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto);

    Optional<BehandlingDto> hentOriginalBehandling(List<BehandlingResourceLink> resourceLinker);

    PersonopplysningDto hentPersonopplysninger(List<BehandlingResourceLink> resourceLinkDtos);

    VergeDto hentVerge(List<BehandlingResourceLink> resourceLinkDtos);

    FamilieHendelseGrunnlagDto hentFamiliehendelse(List<BehandlingResourceLink> resourceLinkDtos);

    SoknadDto hentSoknad(List<BehandlingResourceLink> resourceLinkDtos);

    BeregningsresultatEngangsstønadDto hentBeregningsresultatEngangsstønad(List<BehandlingResourceLink> resourceLinkDtos);

    BeregningsresultatMedUttaksplanDto hentBeregningsresultatForeldrepenger(List<BehandlingResourceLink> resourceLinkDtos);

    Optional<BeregningsresultatMedUttaksplanDto> hentBeregningsresultatForeldrepengerHvisFinnes(List<BehandlingResourceLink> resourceLinkDtos);

    InntektArbeidYtelseDto hentInntektArbeidYtelseDto(List<BehandlingResourceLink> resourceLinkDtos);

    KlagebehandlingDto hentKlagebehandling(List<BehandlingResourceLink> resourceLinkDtos);

    InnsynsbehandlingDto hentInnsynsbehandling(List<BehandlingResourceLink> resourceLinkDtos);

    BeregningsgrunnlagDto hentBeregningsgrunnlag(List<BehandlingResourceLink> resourceLinkDtos);

    Optional<BeregningsgrunnlagDto> hentBeregningsgrunnlagHvisFinnes(List<BehandlingResourceLink> resourceLinker);

    List<VilkårDto> hentVilkår(List<BehandlingResourceLink> resourceLinkDtos);

    FagsakDto hentFagsak(List<BehandlingResourceLink> resourceLinkDtos);

    UttakResultatPerioderDto hentUttaksresultat(List<BehandlingResourceLink> resourceLinkDtos);

    Optional<UttakResultatPerioderDto> hentUttaksresultatHvisFinnes(List<BehandlingResourceLink> resourceLinkDtos);

    YtelseFordelingDto hentYtelseFordeling(List<BehandlingResourceLink> resourceLinker);

    SaldoerDto hentSaldoer(List<BehandlingResourceLink> resourceLinker);

    List<AksjonspunktDto> hentAksjonspunkter(List<BehandlingResourceLink> resourceLinkDtos);

    MottattKlagedokumentDto hentKlagedokument(List<BehandlingResourceLink> resourceLinker);

    List<MottattDokumentDto> hentMottatteDokumenter(List<BehandlingResourceLink> resourceLinker);
}
