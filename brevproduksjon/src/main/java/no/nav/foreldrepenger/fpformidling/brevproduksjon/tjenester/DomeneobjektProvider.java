package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.StartdatoUtsattDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.BehandlingDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.BeregningsgrunnlagDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.FamiliehendelseDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.InnsynDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.InntektsmeldingDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.KlageDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.MottattDokumentDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.StønadskontoDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.SøknadDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.TilkjentYtelseDiffSjekk;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.TilkjentYtelseDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.TilkjentYtelseDtoMapperV2;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.UttakDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.UttakSvpDtoMapper;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.fpformidling.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.fp.YtelseFordeling;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.verge.Verge;

@ApplicationScoped
public class DomeneobjektProvider {

    private Behandlinger behandlingRestKlient;
    private ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public DomeneobjektProvider(Behandlinger behandlingRestKlient, ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
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
        var fagsakDto = behandlingRestKlient.hentFagsak(behandling.getResourceLinker());
        var fagsak = FagsakBackend.ny()
            .medSaksnummer(fagsakDto.saksnummer())
            .medFagsakYtelseType(fagsakDto.fagsakYtelseType())
            .medBrukerRolle(fagsakDto.relasjonsRolleType())
            .medAktørId(new AktørId(fagsakDto.aktørId()))
            .medDekningsgrad(fagsakDto.dekningsgrad())
            .build();
        behandling.leggtilFagsakBackend(fagsak);
        return fagsak;
    }

    public Beregningsgrunnlag hentBeregningsgrunnlag(Behandling behandling) {
        return BeregningsgrunnlagDtoMapper.mapFraDto(behandlingRestKlient.hentBeregningsgrunnlag(behandling.getFormidlingRessurser()),
            arbeidsgiverTjeneste::hentArbeidsgiverNavn);
    }

    public Optional<Beregningsgrunnlag> hentBeregningsgrunnlagHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentFormidlingBeregningsgrunnlagHvisFinnes(behandling.getFormidlingRessurser())
            .map(dto -> BeregningsgrunnlagDtoMapper.mapFraDto(dto, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
    }

    public Behandling hentBehandling(UUID behandlingUuid) {
        return BehandlingDtoMapper.mapBehandlingFraDto((behandlingRestKlient.hentBehandling(behandlingUuid)));
    }

    public Optional<Behandling> hentOriginalBehandlingHvisFinnes(Behandling behandling) {
        return Optional.ofNullable(behandling.getOriginalBehandlingUuid()).map(this::hentBehandling);
    }

    public TilkjentYtelseEngangsstønad hentTilkjentYtelseEngangsstønad(Behandling behandling) {
        var res = TilkjentYtelseDtoMapper.mapTilkjentYtelseESFraDto(
            behandlingRestKlient.hentTilkjentYtelseEngangsstønad(behandling.getResourceLinker()));
        var resV2 = TilkjentYtelseDtoMapperV2.mapTilkjentYtelseESFraDto(
            behandlingRestKlient.hentTilkjentYtelseEngangsstønadV2(behandling.getFormidlingRessurser()));
        TilkjentYtelseDiffSjekk.loggDiff(res, resV2);
        return res;
    }

    public Optional<TilkjentYtelseEngangsstønad> hentTilkjentYtelseESHvisFinnes(Behandling behandling) {
        var res = behandlingRestKlient.hentTilkjentYtelseEngangsstønadHvisFinnes(behandling.getResourceLinker())
            .map(TilkjentYtelseDtoMapper::mapTilkjentYtelseESFraDto);
        var resV2 = behandlingRestKlient.hentTilkjentYtelseEngangsstønadHvisFinnesV2(behandling.getFormidlingRessurser())
            .map(TilkjentYtelseDtoMapperV2::mapTilkjentYtelseESFraDto);
        TilkjentYtelseDiffSjekk.loggDiff(res.orElse(null), resV2.orElse(null));
        return res;
    }

    public TilkjentYtelseForeldrepenger hentTilkjentYtelseForeldrepenger(Behandling behandling) {
        var res = TilkjentYtelseDtoMapper.mapTilkjentYtelseFPFraDto(
            behandlingRestKlient.hentTilkjentYtelseForeldrepenger(behandling.getResourceLinker()), arbeidsgiverTjeneste::hentArbeidsgiverNavn);
        var resV2 = TilkjentYtelseDtoMapperV2.mapTilkjentYtelseDagytelseFraDto(
            behandlingRestKlient.hentTilkjentYtelseForeldrepengerV2(behandling.getFormidlingRessurser()), arbeidsgiverTjeneste::hentArbeidsgiverNavn);
        TilkjentYtelseDiffSjekk.loggDiff(res, resV2);
        return res;
    }

    public Optional<TilkjentYtelseForeldrepenger> hentTilkjentYtelseFPHvisFinnes(Behandling behandling) {
        var res = behandlingRestKlient.hentTilkjentYtelseForeldrepengerHvisFinnes(behandling.getResourceLinker())
            .map(r -> TilkjentYtelseDtoMapper.mapTilkjentYtelseFPFraDto(r, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
        var resV2 = behandlingRestKlient.hentTilkjentYtelseForeldrepengerHvisFinnesV2(behandling.getFormidlingRessurser())
            .map(r -> TilkjentYtelseDtoMapperV2.mapTilkjentYtelseDagytelseFraDto(r, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
        TilkjentYtelseDiffSjekk.loggDiff(res.orElse(null), resV2.orElse(null));
        return res;
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
        var klagebehandlingDto = behandlingRestKlient.hentKlagebehandling(behandling.getResourceLinker());
        return KlageDtoMapper.mapKlagefraDto(klagebehandlingDto);
    }

    public KlageDokument hentKlageDokument(Behandling behandling) {
        return KlageDtoMapper.mapKlagedokumentFraDto(behandlingRestKlient.hentKlagedokument(behandling.getResourceLinker()));
    }

    public Optional<Søknad> hentSøknad(Behandling behandling) {
        return behandlingRestKlient.hentSoknadHvisFinnes(behandling.getResourceLinker()).map(SøknadDtoMapper::mapSøknadFraDto);
    }

    public Optional<ForeldrepengerUttak> hentForeldrepengerUttakHvisFinnes(Behandling behandling) {
        var uttakResultatPerioderDto = behandlingRestKlient.hentUttaksresultatFpHvisFinnes(behandling.getResourceLinker());
        return uttakResultatPerioderDto.map(r -> UttakDtoMapper.mapUttaksresultatPerioderFraDto(r, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
    }

    public ForeldrepengerUttak hentForeldrepengerUttak(Behandling behandling) {
        return hentForeldrepengerUttakHvisFinnes(behandling).orElseThrow(
            () -> new IllegalStateException("Klarte ikke hente fp uttak for behandling: " + behandling.getUuid()));
    }

    public SvangerskapspengerUttak hentSvangerskapspengerUttak(Behandling behandling) {
        return hentSvangerskapspengerUttakHvisFinnes(behandling).orElseThrow(
            () -> new IllegalStateException("Klarte ikke hente svp uttak for behandling: " + behandling.getUuid()));
    }

    public Optional<SvangerskapspengerUttak> hentSvangerskapspengerUttakHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentUttaksresultatSvpHvisFinnes(behandling.getResourceLinker())
            .map(svangerskapspengerUttakResultatDto -> UttakSvpDtoMapper.mapSvpUttaksresultatFraDto(svangerskapspengerUttakResultatDto,
                arbeidsgiverTjeneste::hentArbeidsgiverNavn));
    }

    public Saldoer hentSaldoer(Behandling behandling) {
        return StønadskontoDtoMapper.mapSaldoerFraDto(behandlingRestKlient.hentSaldoer(behandling.getResourceLinker()));
    }

    public Optional<Verge> hentVerge(Behandling behandling) {
        return behandlingRestKlient.hentVergeHvisFinnes(behandling.getResourceLinker())
            .map(v -> new Verge(v.getAktoerId(), v.getOrganisasjonsnummer(), v.getNavn()));
    }

    public List<MottattDokument> hentMottatteDokumenter(Behandling behandling) {
        return MottattDokumentDtoMapper.mapMottattedokumenterFraDto(behandlingRestKlient.hentMottatteDokumenter(behandling.getResourceLinker()));
    }

    public YtelseFordeling ytelseFordeling(Behandling behandling) {
        var dto = behandlingRestKlient.ytelseFordeling(behandling.getResourceLinker());
        return new YtelseFordeling(dto.ønskerJustertVedFødsel());
    }

    public StartdatoUtsattDto hentStartdatoUtsatt(Behandling behandling) {
        return behandlingRestKlient.hentStartdatoUtsatt(behandling.getResourceLinker());
    }
}
