package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.ArbeidsforholdInntektsmelding;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.domene.klage.Klage;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.domene.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.fpformidling.domene.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.YtelseFordeling;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.verge.Verge;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.StartdatoUtsattDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.ArbeidsforholdInntektsmeldingDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.BehandlingDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.BeregningsgrunnlagDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.FamiliehendelseDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.InnsynDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.InntektsmeldingDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.KlageDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.MottattDokumentDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.StønadskontoDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.SøknadDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.TilkjentYtelseDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.UttakDtoMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.UttakSvpDtoMapper;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;

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
        return TilkjentYtelseDtoMapper.mapTilkjentYtelseESFraDto(
            behandlingRestKlient.hentTilkjentYtelseEngangsstønad(behandling.getFormidlingRessurser()));
    }

    public Optional<TilkjentYtelseEngangsstønad> hentTilkjentYtelseESHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentTilkjentYtelseEngangsstønadHvisFinnes(behandling.getFormidlingRessurser())
            .map(TilkjentYtelseDtoMapper::mapTilkjentYtelseESFraDto);
    }

    public TilkjentYtelseForeldrepenger hentTilkjentYtelseForeldrepenger(Behandling behandling) {
        return TilkjentYtelseDtoMapper.mapTilkjentYtelseDagytelseFraDto(
            behandlingRestKlient.hentTilkjentYtelseForeldrepenger(behandling.getFormidlingRessurser()), arbeidsgiverTjeneste::hentArbeidsgiverNavn);
    }

    public Optional<TilkjentYtelseForeldrepenger> hentTilkjentYtelseFPHvisFinnes(Behandling behandling) {
        return behandlingRestKlient.hentTilkjentYtelseForeldrepengerHvisFinnes(behandling.getFormidlingRessurser())
            .map(r -> TilkjentYtelseDtoMapper.mapTilkjentYtelseDagytelseFraDto(r, arbeidsgiverTjeneste::hentArbeidsgiverNavn));
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

    public List<ArbeidsforholdInntektsmelding> hentArbeidsforholdInntektsmeldingerStatus(Behandling behandling) {
        return ArbeidsforholdInntektsmeldingDtoMapper.mapArbeidsforholdInntektsmeldingFraDto(behandlingRestKlient.hentArbeidsforholdInntektsmeldingerDto(behandling.getFormidlingRessurser()),
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
            .map(v -> new Verge(v.aktoerId(), v.organisasjonsnummer(), v.navn(), v.gyldigFom(), v.gyldigTom()));
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
    public LocalDate hentMottattDatoSøknad(Behandling behandling) {
        return behandlingRestKlient.hentMottattDatoSøknad(behandling.getResourceLinker());
    }
}
