package no.nav.foreldrepenger.melding.datamapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.anke.AnkebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.dto.AksjonspunktDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.AnkeDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.BehandlingDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.BeregningsgrunnlagDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.BeregningsresultatDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.FagsakDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.FamiliehendelseDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.IAYDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.InnsynDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.KlageDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.MottattDokumentDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.StønadskontoDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.SøknadDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.UttakDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.UttakSvpDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.VilkårDtoMapper;
import no.nav.foreldrepenger.melding.dtomapper.YtelseFordelingDtoMapper;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.verge.Verge;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

@ApplicationScoped
public class DomeneobjektProvider {

    private BehandlingRestKlient behandlingRestKlient;

    @Inject
    public DomeneobjektProvider(BehandlingRestKlient behandlingRestKlient) {
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public DomeneobjektProvider() {
        //CDI
    }


    public FagsakBackend hentFagsakBackend(Behandling behandling) {
        if (behandling.harFagsakBackend()) {
            return behandling.getFagsakBackend();
        }
        var fagsak = FagsakDtoMapper.mapFagsakBackendFraDto(behandlingRestKlient.hentFagsakBackend(behandling.getResourceLinker()));
        behandling.leggtilFagsakBackend(fagsak);
        return fagsak;
    }

    public Beregningsgrunnlag hentBeregningsgrunnlag(Behandling behandling) {
        return BeregningsgrunnlagDtoMapper.mapBeregningsgrunnlagFraDto(behandlingRestKlient.hentBeregningsgrunnlag(behandling.getResourceLinker()));
    }

    public Optional<Beregningsgrunnlag> hentBeregningsgrunnlagHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentBeregningsgrunnlagHvisFinnes(behandling.getResourceLinker()).map(BeregningsgrunnlagDtoMapper::mapBeregningsgrunnlagFraDto);
    }

    public Behandling hentBehandling(UUID behandlingUuid) {
        return BehandlingDtoMapper.mapBehandlingFraDto((behandlingRestKlient.hentBehandling(new BehandlingIdDto(behandlingUuid))));
    }

    public Optional<Behandling> hentOriginalBehandlingHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentOriginalBehandling(behandling.getResourceLinker())
                .map(dto -> behandlingRestKlient.hentBehandling(new BehandlingIdDto(dto.getId()))) //TODO - Endre til UUID..
                .map(BehandlingDtoMapper::mapBehandlingFraDto);
    }

    public BeregningsresultatES hentBeregningsresultatES(Behandling behandling) {
        return BeregningsresultatDtoMapper.mapBeregningsresultatESFraDto(behandlingRestKlient.hentBeregningsresultatEngangsstønad(behandling.getResourceLinker()));
    }

    public Optional<BeregningsresultatES> hentBeregningsresultatESHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentBeregningsresultatEngangsstønadHvisFinnes(behandling.getResourceLinker()).map(BeregningsresultatDtoMapper::mapBeregningsresultatESFraDto);
    }

    public BeregningsresultatFP hentBeregningsresultatFP(Behandling behandling) {
        return BeregningsresultatDtoMapper.mapBeregningsresultatFPFraDto(behandlingRestKlient.hentBeregningsresultatForeldrepenger(behandling.getResourceLinker()));
    }

    public Optional<BeregningsresultatFP> hentBeregningsresultatFPHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentBeregningsresultatForeldrepengerHvisFinnes(behandling.getResourceLinker()).map(BeregningsresultatDtoMapper::mapBeregningsresultatFPFraDto);
    }

    public FamilieHendelse hentFamiliehendelse(Behandling behandling) {
        return FamiliehendelseDtoMapper.mapFamiliehendelsefraDto(behandlingRestKlient.hentFamiliehendelse(behandling.getResourceLinker()));
    }

    public InntektArbeidYtelse hentInntektArbeidYtelse(Behandling behandling) {
        return IAYDtoMapper.mapIAYFraDto(behandlingRestKlient.hentInntektArbeidYtelseDto(behandling.getResourceLinker()));
    }

    public Innsyn hentInnsyn(Behandling behandling) {
        return InnsynDtoMapper.mapInnsynFraDto(behandlingRestKlient.hentInnsynsbehandling(behandling.getResourceLinker()));
    }

    public Klage hentKlagebehandling(Behandling behandling) {
        KlagebehandlingDto klagebehandlingDto = behandlingRestKlient.hentKlagebehandling(behandling.getResourceLinker());
        return KlageDtoMapper.mapKlagefraDto(klagebehandlingDto);
    }

    public Optional<Anke> hentAnkebehandling(Behandling behandling) {
        AnkebehandlingDto ankebehandlingDto = behandlingRestKlient.hentAnkebehandling(behandling.getResourceLinker());
        return AnkeDtoMapper.mapAnkeFraDto(ankebehandlingDto);
    }

    public KlageDokument hentKlageDokument(Behandling behandling) {
        return KlageDtoMapper.mapKlagedokumentFraDto(behandlingRestKlient.hentKlagedokument(behandling.getResourceLinker()));
    }

    public Optional<Søknad> hentSøknad(Behandling behandling) {
        return behandlingRestKlient.hentSoknadHvisFinnes(behandling.getResourceLinker()).map(SøknadDtoMapper::mapSøknadFraDto);
    }

    public List<Vilkår> hentVilkår(Behandling behandling) {
        return VilkårDtoMapper.mapVilkårFraDto(behandlingRestKlient.hentVilkår(behandling.getResourceLinker()));
    }

    public Optional<UttakResultatPerioder> hentUttaksresultatHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentUttaksresultatHvisFinnes(behandling.getResourceLinker()).map(UttakDtoMapper::mapUttaksresultatPerioderFraDto);
    }

    public UttakResultatPerioder hentUttaksresultat(Behandling behandling) {
        return UttakDtoMapper.mapUttaksresultatPerioderFraDto(behandlingRestKlient.hentUttaksresultat(behandling.getResourceLinker()));
    }

    public SvpUttaksresultat hentUttaksresultatSvp(Behandling behandling) {
        return UttakSvpDtoMapper.mapSvpUttaksresultatFraDto(behandlingRestKlient.hentUttaksresultatSvp(behandling.getResourceLinker()));
    }

    public YtelseFordeling hentYtelseFordeling(Behandling behandling) {
        return YtelseFordelingDtoMapper.mapYtelseFordelingFraDto(behandlingRestKlient.hentYtelseFordeling(behandling.getResourceLinker()));
    }

    public Saldoer hentSaldoer(Behandling behandling) {
        return StønadskontoDtoMapper.mapSaldoerFraDto(behandlingRestKlient.hentSaldoer(behandling.getResourceLinker()));
    }

    public List<Aksjonspunkt> hentAksjonspunkter(Behandling behandling) {
        return AksjonspunktDtoMapper.mapAksjonspunktFraDto(behandlingRestKlient.hentAksjonspunkter(behandling.getResourceLinker()));
    }

    public Optional<Verge> hentVerge(Behandling behandling) {
        return behandlingRestKlient.hentVergeHvisfinnes(behandling.getResourceLinker()).map(v -> new Verge(v.getAktoerId(), v.getOrganisasjonsnummer(), v.getNavn()));
    }

    public List<MottattDokument> hentMottatteDokumenter(Behandling behandling) {
        return MottattDokumentDtoMapper.mapMottattedokumenterFraDto(behandlingRestKlient.hentMottatteDokumenter(behandling.getResourceLinker()));
    }

    public String getJsonTestdata() {
        return behandlingRestKlient.getJsonTestdata();
    }

}
