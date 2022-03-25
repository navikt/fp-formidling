package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.fpformidling.anke.Anke;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.fpformidling.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.uttak.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.fpformidling.verge.Verge;
import no.nav.foreldrepenger.fpformidling.vilkår.Vilkår;
import no.nav.foreldrepenger.fpformidling.ytelsefordeling.YtelseFordeling;
import no.nav.foreldrepenger.fpsak.Behandlinger;
import no.nav.foreldrepenger.fpsak.dto.anke.AnkebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.StartdatoUtsattDto;
import no.nav.foreldrepenger.fpsak.mapper.AnkeDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.BehandlingDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.BeregningsgrunnlagDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.BeregningsgrunnlagV2DtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.FagsakDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.FamiliehendelseDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.InnsynDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.InntektsmeldingDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.KlageDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.MottattDokumentDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.StønadskontoDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.SøknadDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.TilkjentYtelseDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.UttakDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.UttakSvpDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.VilkårDtoMapper;
import no.nav.foreldrepenger.fpsak.mapper.YtelseFordelingDtoMapper;

@ApplicationScoped
public class DomeneobjektProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomeneobjektProvider.class);

    private Behandlinger behandlingRestKlient;
    private ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public DomeneobjektProvider(Behandlinger behandlingRestKlient,
                                ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        this.behandlingRestKlient = behandlingRestKlient;
        this.arbeidsgiverTjeneste = arbeidsgiverTjeneste;
    }

    public DomeneobjektProvider() {
        // CDI
    }

    public FagsakBackend hentFagsakBackend(Behandling behandling) {
        if (behandling.harFagsakBackend()) {
            return behandling.getFagsakBackend();
        }
        var fagsak = FagsakDtoMapper.mapFagsakBackendFraDto(behandlingRestKlient.hentFagsak(behandling.getResourceLinker()));
        behandling.leggtilFagsakBackend(fagsak);
        return fagsak;
    }

    public Beregningsgrunnlag hentBeregningsgrunnlag(Behandling behandling) {
        var beregningsgrunnlag = BeregningsgrunnlagDtoMapper.mapBeregningsgrunnlagFraDto(
                behandlingRestKlient.hentBeregningsgrunnlag(behandling.getFormidlingRessurser()), arbeidsgiverTjeneste::hentArbeidsgiverNavn);

        Beregningsgrunnlag beregningsgrunnlagV2;
        try {
            beregningsgrunnlagV2 = hentBeregningsgrunnlagV2(behandling);

            if (!beregningsgrunnlag.equals(beregningsgrunnlagV2)) {
                LOGGER.info("FORMIDLING-BG: beregningsgrunnlagV2 {} er ulik beregningsgrunnlag: {}", beregningsgrunnlagV2, beregningsgrunnlag);
            }
        } catch (Exception ex) {
            LOGGER.warn("FORMIDLING-BG: Feil ved innhenting av beregningsgrunnlagV2");
        }

        return beregningsgrunnlag;
    }

    public Optional<Beregningsgrunnlag> hentBeregningsgrunnlagHvisFinnes(Behandling behandling) {
        var beregningsgrunnlag = behandlingRestKlient.hentFormidlingBeregningsgrunnlagHvisFinnes(behandling.getFormidlingRessurser())
                .map(dto -> BeregningsgrunnlagDtoMapper.mapBeregningsgrunnlagFraDto(dto, arbeidsgiverTjeneste::hentArbeidsgiverNavn));

        Optional<Beregningsgrunnlag> beregningsgrunnlagV2;
        try {
            beregningsgrunnlagV2 = hentBeregningsgrunnlagV2HvisFinnes(behandling);

            if (!beregningsgrunnlag.equals(beregningsgrunnlagV2)) {
                LOGGER.info("FORMIDLING-BG: beregningsgrunnlagV2 {} er ulik beregningsgrunnlag: {}", beregningsgrunnlagV2, beregningsgrunnlag);
            }
        } catch (Exception ex) {
            LOGGER.info("FORMIDLING-BG: Feil ved innhenting av beregningsgrunnlagV2");
        }
        return beregningsgrunnlag;
    }

    public Beregningsgrunnlag hentBeregningsgrunnlagV2(Behandling behandling) {
        return BeregningsgrunnlagV2DtoMapper.mapBeregningsgrunnlagFraDto(
                behandlingRestKlient.hentBeregningsgrunnlagV2(behandling.getUuid()), arbeidsgiverTjeneste::hentArbeidsgiverNavn);
    }

    public Optional<Beregningsgrunnlag> hentBeregningsgrunnlagV2HvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentBeregningsgrunnlagV2HvisFinnes(behandling.getUuid())
                .map(dto -> BeregningsgrunnlagV2DtoMapper.mapBeregningsgrunnlagFraDto(dto, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
    }

    public Behandling hentBehandling(UUID behandlingUuid) {
        return BehandlingDtoMapper.mapBehandlingFraDto((behandlingRestKlient.hentBehandling(behandlingUuid)));
    }

    public Optional<Behandling> hentOriginalBehandlingHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentOriginalBehandling(behandling.getResourceLinker())
                .map(dto -> behandlingRestKlient.hentBehandling(dto.getUuid()))
                .map(BehandlingDtoMapper::mapBehandlingFraDto);
    }

    public TilkjentYtelseEngangsstønad hentTilkjentYtelseEngangsstønad(Behandling behandling) {
        return TilkjentYtelseDtoMapper
                .mapTilkjentYtelseESFraDto(behandlingRestKlient.hentTilkjentYtelseEngangsstønad(behandling.getResourceLinker()));
    }

    public Optional<TilkjentYtelseEngangsstønad> hentTilkjentYtelseESHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentTilkjentYtelseEngangsstønadHvisFinnes(behandling.getResourceLinker())
                .map(TilkjentYtelseDtoMapper::mapTilkjentYtelseESFraDto);
    }

    public TilkjentYtelseForeldrepenger hentTilkjentYtelseForeldrepenger(Behandling behandling) {
        return TilkjentYtelseDtoMapper.mapTilkjentYtelseFPFraDto(
                behandlingRestKlient.hentTilkjentYtelseForeldrepenger(behandling.getResourceLinker()),
                arbeidsgiverTjeneste::hentArbeidsgiverNavn);
    }

    public Optional<TilkjentYtelseForeldrepenger> hentTilkjentYtelseFPHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentTilkjentYtelseForeldrepengerHvisFinnes(behandling.getResourceLinker())
                .map(r -> TilkjentYtelseDtoMapper.mapTilkjentYtelseFPFraDto(r, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
    }

    public FamilieHendelse hentFamiliehendelse(Behandling behandling) {
        return FamiliehendelseDtoMapper.mapFamiliehendelsefraDto(behandlingRestKlient.hentFamiliehendelse(behandling.getResourceLinker()));
    }

    public Optional<FamilieHendelse> hentFamiliehendelseHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentFamiliehendelseHvisFinnes(behandling.getResourceLinker())
                .map(FamiliehendelseDtoMapper::mapFamiliehendelsefraDto);
    }

    public Inntektsmeldinger hentInntektsmeldinger(Behandling behandling) {
        return InntektsmeldingDtoMapper.mapIAYFraDto(behandlingRestKlient.hentInntektsmeldingerDto(behandling.getResourceLinker()),
                arbeidsgiverTjeneste::hentArbeidsgiverNavn);
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
        return behandlingRestKlient.hentUttaksresultatHvisFinnes(behandling.getResourceLinker())
                .map(r -> UttakDtoMapper.mapUttaksresultatPerioderFraDto(r, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
    }

    public UttakResultatPerioder hentUttaksresultat(Behandling behandling) {
        return UttakDtoMapper.mapUttaksresultatPerioderFraDto(behandlingRestKlient.hentUttaksresultat(behandling.getResourceLinker()),
                arbeidsgiverTjeneste::hentArbeidsgiverNavn);
    }

    public SvpUttaksresultat hentUttaksresultatSvp(Behandling behandling) {
        return UttakSvpDtoMapper.mapSvpUttaksresultatFraDto(behandlingRestKlient.hentUttaksresultatSvp(behandling.getResourceLinker()),
                arbeidsgiverTjeneste::hentArbeidsgiverNavn);
    }

    public Optional<SvpUttaksresultat> hentUttaksresultatSvpHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentUttaksresultatSvpHvisFinnes(behandling.getResourceLinker())
                .map(svangerskapspengerUttakResultatDto -> UttakSvpDtoMapper.mapSvpUttaksresultatFraDto(svangerskapspengerUttakResultatDto, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
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
        return behandlingRestKlient.hentVergeHvisFinnes(behandling.getResourceLinker())
                .map(v -> new Verge(v.getAktoerId(), v.getOrganisasjonsnummer(), v.getNavn()));
    }

    public List<MottattDokument> hentMottatteDokumenter(Behandling behandling) {
        return MottattDokumentDtoMapper.mapMottattedokumenterFraDto(behandlingRestKlient.hentMottatteDokumenter(behandling.getResourceLinker()));
    }

    public boolean kreverSammenhengendeUttak(Behandling behandling) {
        return behandlingRestKlient.kreverSammenhengendeUttak(behandling.getResourceLinker()).kreverSammenhengendeUttak();
    }

    public StartdatoUtsattDto hentStartdatoUtsatt(Behandling behandling) {
        return behandlingRestKlient.hentStartdatoUtsatt(behandling.getResourceLinker());
    }
}
