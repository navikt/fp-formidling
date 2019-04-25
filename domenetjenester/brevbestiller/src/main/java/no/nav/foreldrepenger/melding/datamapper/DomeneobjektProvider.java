package no.nav.foreldrepenger.melding.datamapper;

import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Innsyn;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.dto.BehandlingDtoMapper;
import no.nav.foreldrepenger.melding.datamapper.dto.BeregningsgrunnlagDtoMapper;
import no.nav.foreldrepenger.melding.datamapper.dto.BeregningsresultatDtoMapper;
import no.nav.foreldrepenger.melding.datamapper.dto.KlageDtoMapper;
import no.nav.foreldrepenger.melding.datamapper.dto.UttakDtoMapper;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;

//TODO kanskje rename denne?
@ApplicationScoped
public class DomeneobjektProvider {

    private BehandlingRestKlient behandlingRestKlient;
    private BeregningsgrunnlagDtoMapper beregningsgrunnlagDtoMapper;
    private BehandlingDtoMapper behandlingDtoMapper;
    private BeregningsresultatDtoMapper beregningsresultatDtoMapper;
    private KlageDtoMapper klageDtoMapper;
    private UttakDtoMapper uttakDtoMapper;

    @Inject
    public DomeneobjektProvider(BehandlingRestKlient behandlingRestKlient,
                                BeregningsgrunnlagDtoMapper beregningsgrunnlagDtoMapper,
                                BehandlingDtoMapper behandlingDtoMapper,
                                BeregningsresultatDtoMapper beregningsresultatDtoMapper,
                                KlageDtoMapper klageDtoMapper,
                                UttakDtoMapper uttakDtoMapper) {
        this.behandlingRestKlient = behandlingRestKlient;
        this.beregningsgrunnlagDtoMapper = beregningsgrunnlagDtoMapper;
        this.behandlingDtoMapper = behandlingDtoMapper;
        this.beregningsresultatDtoMapper = beregningsresultatDtoMapper;
        this.klageDtoMapper = klageDtoMapper;
        this.uttakDtoMapper = uttakDtoMapper;
    }

    public DomeneobjektProvider() {
        //CDI
    }

    public Beregningsgrunnlag hentBeregningsgrunnlag(Behandling behandling) {
        return beregningsgrunnlagDtoMapper.mapBeregningsgrunnlagFraDto(behandlingRestKlient.hentBeregningsgrunnlag(behandling.getResourceLinkDtos()));
    }


    public Behandling hentBehandling(long behandlingId) {
        return behandlingDtoMapper.mapBehandlingFraDto((behandlingRestKlient.hentBehandling(new BehandlingIdDto(behandlingId))));
    }

    public BeregningsresultatES hentBeregningsresultatES(Behandling behandling) {
        return new BeregningsresultatES(behandlingRestKlient.hentBeregningsresultatEngangsstønad(behandling.getResourceLinkDtos()));
    }

    public BeregningsresultatFP hentBeregningsresultatFP(Behandling behandling) {
        return beregningsresultatDtoMapper.mapBeregningsresultatFPFraDto(behandlingRestKlient.hentBeregningsresultatForeldrepenger(behandling.getResourceLinkDtos()));
    }

    public FamilieHendelse hentFamiliehendelse(Behandling behandling) {
        return FamilieHendelse.fraDto(behandlingRestKlient.hentFamiliehendelse(behandling.getResourceLinkDtos()));
    }

    public InntektArbeidYtelse hentInntektArbeidYtelse(Behandling behandling) {
        return new InntektArbeidYtelse(behandlingRestKlient.hentInntektArbeidYtelseDto(behandling.getResourceLinkDtos()));
    }

    public Innsyn hentInnsyn(Behandling behandling) {
        return new Innsyn(behandlingRestKlient.hentInnsynsbehandling(behandling.getResourceLinkDtos()));
    }


    public Klage hentKlagebehandling(Behandling behandling) {
        KlagebehandlingDto klagebehandlingDto = behandlingRestKlient.hentKlagebehandling(behandling.getResourceLinkDtos());
        return klageDtoMapper.mapKlagefraDto(klagebehandlingDto);
    }

    public Søknad hentSøknad(Behandling behandling) {
        return new Søknad(behandlingRestKlient.hentSoknad(behandling.getResourceLinkDtos()));
    }

    public Vilkår hentVilkår(Behandling behandling) {
        return new Vilkår(behandlingRestKlient.hentVilkår(behandling.getResourceLinkDtos()));
    }

    public UttakResultatPerioder hentUttaksresultat(Behandling behandling) {
        UttakResultatPerioderDto resultatPerioderDto = behandlingRestKlient.hentUttaksresultat(behandling.getResourceLinkDtos());
        return UttakResultatPerioder.ny()
                .medPerioder(resultatPerioderDto.getPerioderSøker().stream().map(uttakDtoMapper::periodeFraDto).collect(Collectors.toList()))
                .medPerioderAnnenPart(resultatPerioderDto.getPerioderAnnenpart().stream().map(uttakDtoMapper::periodeFraDto).collect(Collectors.toList()))
                .build();
    }

}
