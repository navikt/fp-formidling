package no.nav.foreldrepenger.melding.datamapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.dto.AksjonspunktDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.BehandlingDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.BeregningsgrunnlagDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.BeregningsresultatDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.FamiliehendelseDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.IAYDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.InnsynDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.KlageDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.MottattDokumentDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.StønadskontoDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.SøknadDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.UttakDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.VergeDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.VilkårDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.YtelseFordelingDtoMapper;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.verge.Verge;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

@ApplicationScoped
public class DomeneobjektProvider {

    private BehandlingRestKlient behandlingRestKlient;
    private BeregningsgrunnlagDtoMapper beregningsgrunnlagDtoMapper;
    private BehandlingDtoMapper behandlingDtoMapper;
    private BeregningsresultatDtoMapper beregningsresultatDtoMapper;
    private KlageDtoMapper klageDtoMapper;
    private UttakDtoMapper uttakDtoMapper;
    private IAYDtoMapper iayDtoMapper;
    private InnsynDtoMapper innsynDtoMapper;
    private VilkårDtoMapper vilkårDtoMapper;
    private FamiliehendelseDtoMapper familiehendelseDtoMapper;
    private StønadskontoDtoMapper stønadskontoDtoMapper;
    private AksjonspunktDtoMapper aksjonspunktDtoMapper;
    private MottattDokumentDtoMapper mottattDokumentDtoMapper;

    @Inject
    public DomeneobjektProvider(BehandlingRestKlient behandlingRestKlient,
                                BeregningsgrunnlagDtoMapper beregningsgrunnlagDtoMapper,
                                BehandlingDtoMapper behandlingDtoMapper,
                                BeregningsresultatDtoMapper beregningsresultatDtoMapper,
                                KlageDtoMapper klageDtoMapper,
                                UttakDtoMapper uttakDtoMapper,
                                IAYDtoMapper iayDtoMapper,
                                InnsynDtoMapper innsynDtoMapper,
                                VilkårDtoMapper vilkårDtoMapper,
                                FamiliehendelseDtoMapper familiehendelseDtoMapper,
                                StønadskontoDtoMapper stønadskontoDtoMapper,
                                AksjonspunktDtoMapper aksjonspunktDtoMapper,
                                MottattDokumentDtoMapper mottattDokumentDtoMapper) {
        this.behandlingRestKlient = behandlingRestKlient;
        this.beregningsgrunnlagDtoMapper = beregningsgrunnlagDtoMapper;
        this.behandlingDtoMapper = behandlingDtoMapper;
        this.beregningsresultatDtoMapper = beregningsresultatDtoMapper;
        this.klageDtoMapper = klageDtoMapper;
        this.uttakDtoMapper = uttakDtoMapper;
        this.iayDtoMapper = iayDtoMapper;
        this.innsynDtoMapper = innsynDtoMapper;
        this.vilkårDtoMapper = vilkårDtoMapper;
        this.familiehendelseDtoMapper = familiehendelseDtoMapper;
        this.stønadskontoDtoMapper = stønadskontoDtoMapper;
        this.aksjonspunktDtoMapper = aksjonspunktDtoMapper;
        this.mottattDokumentDtoMapper = mottattDokumentDtoMapper;
    }

    public DomeneobjektProvider() {
        //CDI
    }

    public Beregningsgrunnlag hentBeregningsgrunnlag(Behandling behandling) {
        return beregningsgrunnlagDtoMapper.mapBeregningsgrunnlagFraDto(behandlingRestKlient.hentBeregningsgrunnlag(behandling.getResourceLinker()));
    }

    public Optional<Beregningsgrunnlag> hentBeregningsgrunnlagHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentBeregningsgrunnlagHvisFinnes(behandling.getResourceLinker()).map(beregningsgrunnlagDtoMapper::mapBeregningsgrunnlagFraDto);
    }

    public Behandling hentBehandling(UUID behandlingUuid) {
        return behandlingDtoMapper.mapBehandlingFraDto((behandlingRestKlient.hentBehandling(new BehandlingIdDto(behandlingUuid))));
    }

    public BeregningsresultatES hentBeregningsresultatES(Behandling behandling) {
        return BeregningsresultatDtoMapper.mapBeregningsresultatESFraDto(behandlingRestKlient.hentBeregningsresultatEngangsstønad(behandling.getResourceLinker()));
    }

    public Optional<BeregningsresultatES> hentBeregningsresultatESHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentBeregningsresultatEngangsstønadHvisFinnes(behandling.getResourceLinker()).map(BeregningsresultatDtoMapper::mapBeregningsresultatESFraDto);
    }

    public BeregningsresultatFP hentBeregningsresultatFP(Behandling behandling) {
        return beregningsresultatDtoMapper.mapBeregningsresultatFPFraDto(behandlingRestKlient.hentBeregningsresultatForeldrepenger(behandling.getResourceLinker()));
    }


    public Optional<BeregningsresultatFP> hentBeregningsresultatFPHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentBeregningsresultatForeldrepengerHvisFinnes(behandling.getResourceLinker()).map(beregningsresultatDtoMapper::mapBeregningsresultatFPFraDto);
    }

    public FamilieHendelse hentFamiliehendelse(Behandling behandling) {
        return familiehendelseDtoMapper.mapFamiliehendelsefraDto(behandlingRestKlient.hentFamiliehendelse(behandling.getResourceLinker()));
    }

    public InntektArbeidYtelse hentInntektArbeidYtelse(Behandling behandling) {
        return iayDtoMapper.mapIAYFraDto(behandlingRestKlient.hentInntektArbeidYtelseDto(behandling.getResourceLinker()));
    }

    public Innsyn hentInnsyn(Behandling behandling) {
        return innsynDtoMapper.mapInnsynFraDto(behandlingRestKlient.hentInnsynsbehandling(behandling.getResourceLinker()));
    }

    public Klage hentKlagebehandling(Behandling behandling) {
        KlagebehandlingDto klagebehandlingDto = behandlingRestKlient.hentKlagebehandling(behandling.getResourceLinker());
        return klageDtoMapper.mapKlagefraDto(klagebehandlingDto);
    }

    public KlageDokument hentKlageDokument(Behandling behandling) {
        return klageDtoMapper.mapKlagedokumentFraDto(behandlingRestKlient.hentKlagedokument(behandling.getResourceLinker()));
    }

    public Søknad hentSøknad(Behandling behandling) {
        return SøknadDtoMapper.mapSøknadFraDto(behandlingRestKlient.hentSoknad(behandling.getResourceLinker()));
    }

    public List<Vilkår> hentVilkår(Behandling behandling) {
        return vilkårDtoMapper.mapVilkårFraDto(behandlingRestKlient.hentVilkår(behandling.getResourceLinker()));
    }


    public Optional<UttakResultatPerioder> hentUttaksresultatHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentUttaksresultatHvisFinnes(behandling.getResourceLinker()).map(uttakDtoMapper::mapUttaksresultatPerioderFraDto);
    }

    public UttakResultatPerioder hentUttaksresultat(Behandling behandling) {
        return uttakDtoMapper.mapUttaksresultatPerioderFraDto(behandlingRestKlient.hentUttaksresultat(behandling.getResourceLinker()));
    }

    public YtelseFordeling hentYtelseFordeling(Behandling behandling) {
        return YtelseFordelingDtoMapper.mapYtelseFordelingFraDto(behandlingRestKlient.hentYtelseFordeling(behandling.getResourceLinker()));
    }

    public Saldoer hentSaldoer(Behandling behandling) {
        return stønadskontoDtoMapper.mapSaldoerFraDto(behandlingRestKlient.hentSaldoer(behandling.getResourceLinker()));
    }

    public List<Aksjonspunkt> hentAksjonspunkter(Behandling behandling) {
        return aksjonspunktDtoMapper.mapAksjonspunktFraDto(behandlingRestKlient.hentAksjonspunkter(behandling.getResourceLinker()));
    }

    public Verge hentVerge(Behandling behandling) {
        return VergeDtoMapper.mapVergeFraDto(behandlingRestKlient.hentVerge(behandling.getResourceLinker()));
    }

    public List<MottattDokument> hentMottatteDokumenter(Behandling behandling) {
        return mottattDokumentDtoMapper.mapMottattedokumenterFraDto(behandlingRestKlient.hentMottatteDokumenter(behandling.getResourceLinker()));
    }

}
