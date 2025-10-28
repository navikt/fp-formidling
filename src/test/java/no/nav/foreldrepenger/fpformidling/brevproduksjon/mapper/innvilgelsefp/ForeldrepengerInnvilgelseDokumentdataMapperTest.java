package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnDagsats;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdataMapper.harVarierendeDagsats;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdataMapper.starterMedFullUtbetaling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp.of;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.brevGrunnlag;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.familieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepengerUttakAktivitet;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.kontoUtvidelser;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.skjæringstidspunkt;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.stønadskonto;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.tilkjentYtelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingÅrsakType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Dekningsgrad;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.FamilieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.PeriodeResultatType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.RelasjonsRolleType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Rettigheter;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.UttakArbeidType;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.BrevGrunnlagBuilder;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

@ExtendWith(MockitoExtension.class)
class ForeldrepengerInnvilgelseDokumentdataMapperTest {

    private static final int DEKNINGSGRAD = 100;
    private static final int DISPONIBLE_DAGER = 5;
    private static final int DISPONIBLE_DAGER_FELLES = 10;
    private static final int TAPTE_DAGER_FPFF = 2;
    private static final LocalDate PERIODE_FOM = LocalDate.now().plusMonths(4);
    private static final LocalDate PERIODE_TOM = LocalDate.now().plusMonths(4).plusDays(3);
    private static final int PREMATUR_DAGER = 2;
    private static final int KLAGEFRIST = 6;
    private static final long BRUTTO_BEREGNINGSGRUNNLAG = 52_000;
    private static final int GRUNNBELØP = 100000;
    private static final long DAGSATS = 200;
    private static final String ARBEIDSGIVER_ORGNR = "1";
    private static final String ARBEIDSGIVER_NAVN = "Arbeidsgiver AS";

    @Mock
    private ArbeidsgiverTjeneste arbeidsgiverTjeneste = mock(ArbeidsgiverTjeneste.class);

    private ForeldrepengerInnvilgelseDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentdataMapper = new ForeldrepengerInnvilgelseDokumentdataMapper(brevParametere, arbeidsgiverTjeneste);

        lenient().when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(ARBEIDSGIVER_ORGNR)).thenReturn(ARBEIDSGIVER_NAVN);
    }

    @Test
    void skal_mappe_felter_for_brev() {
        // Arrange
        var behandling = opprettBehandling(BehandlingType.REVURDERING,
            List.of(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING, BehandlingÅrsakType.RE_HENDELSE_FØDSEL),
            List.of(Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING,
                Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK)).tilkjentYtelse(
                tilkjentYtelse().dagytelse(opprettTilkjentYtelseFP()).build())
            .beregningsgrunnlag(opprettBeregningsgrunnlag(AktivitetStatusDto.ARBEIDSTAKER))
            .foreldrepenger(opprettUttaksresultat())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        var tilkjentYtelseFP = opprettTilkjentYtelseFP();

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.FP);
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
        assertThat(dokumentdata.getFelles().getErUtkast()).isTrue();

        assertThat(dokumentdata.getBehandlingType()).isEqualTo(BehandlingType.REVURDERING.name());
        assertThat(dokumentdata.getBehandlingResultatType()).isEqualTo(Behandlingsresultat.BehandlingResultatType.INNVILGET.name());
        assertThat(dokumentdata.getKonsekvensForInnvilgetYtelse()).isEqualTo("ENDRING_I_BEREGNING_OG_UTTAK");
        assertThat(dokumentdata.getDekningsgrad()).isEqualTo(DEKNINGSGRAD);
        assertThat(dokumentdata.getDagsats()).isEqualTo(finnDagsats(tilkjentYtelseFP));
        assertThat(dokumentdata.getMånedsbeløp()).isEqualTo(finnMånedsbeløp(tilkjentYtelseFP));
        assertThat(dokumentdata.getInntektMottattArbeidsgiver()).isTrue();
        assertThat(dokumentdata.getBarnErFødt()).isFalse();
        assertThat(dokumentdata.getÅrsakErFødselshendelse()).isTrue();
        assertThat(dokumentdata.getIkkeOmsorg()).isTrue();
        assertThat(dokumentdata.getGjelderMor()).isTrue();
        assertThat(dokumentdata.getGjelderFødsel()).isTrue();
        assertThat(dokumentdata.getDelvisRefusjon()).isTrue();
        assertThat(dokumentdata.getIngenRefusjon()).isFalse();
        assertThat(dokumentdata.getFbEllerRvInnvilget()).isTrue();
        assertThat(dokumentdata.getFullRefusjon()).isFalse();
        assertThat(dokumentdata.getAntallPerioder()).isEqualTo(4);
        assertThat(dokumentdata.getAntallInnvilgedePerioder()).isEqualTo(2);
        assertThat(dokumentdata.getAntallAvslåttePerioder()).isEqualTo(2);
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getDagerTaptFørTermin()).isEqualTo(TAPTE_DAGER_FPFF);
        assertThat(dokumentdata.getDisponibleDager()).isEqualTo(DISPONIBLE_DAGER);
        assertThat(dokumentdata.getDisponibleFellesDager()).isEqualTo(DISPONIBLE_DAGER_FELLES);
        assertThat(dokumentdata.getSisteDagAvSistePeriode()).isEqualTo(formaterDatoNorsk(PERIODE_TOM));
        assertThat(dokumentdata.getStønadsperiodeFom()).isEqualTo(formaterDatoNorsk(PERIODE_FOM));
        assertThat(dokumentdata.getStønadsperiodeTom()).isEqualTo(formaterDatoNorsk(PERIODE_TOM));
        assertThat(dokumentdata.getUtbetalingFom()).isEqualTo(formaterDatoNorsk(PERIODE_FOM));
        assertThat(dokumentdata.getFlerbarnsdagerUtvidetUker()).isZero();
        assertThat(dokumentdata.getFlerbarnsdagerUtvidetDager()).isZero();
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(1);
        assertThat(dokumentdata.getPrematurDager()).isEqualTo(PREMATUR_DAGER);
        assertThat(dokumentdata.getAntallDødeBarn()).isZero();
        assertThat(dokumentdata.getDødsdato()).isNull();
        assertThat(dokumentdata.getMorKanSøkeOmDagerFørFødsel()).isTrue();
        assertThat(dokumentdata.getPerioder()).hasSize(4);
        assertThat(dokumentdata.getPerioder().getFirst().getStønadskontoType()).isEqualTo(StønadskontoType.FORELDREPENGER);

        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getLovhjemlerUttak()).isEqualTo("forvaltningsloven § 35");
        assertThat(dokumentdata.getLovhjemlerBeregning()).isEqualTo("§ 14-7 og forvaltningsloven § 35");

        assertThat(dokumentdata.getInkludereInnvilget()).isTrue();
        assertThat(dokumentdata.getInkludereAvslag()).isTrue();

        assertThat(dokumentdata.getBeregningsgrunnlagregler()).hasSize(1);
        assertThat(dokumentdata.getBruttoBeregningsgrunnlag()).isEqualTo(of(BRUTTO_BEREGNINGSGRUNNLAG));
        assertThat(dokumentdata.getSeksG()).isEqualTo(GRUNNBELØP * 6);
        assertThat(dokumentdata.getInntektOverSeksG()).isFalse();
        assertThat(dokumentdata.getErBesteberegning()).isTrue();
        assertThat(dokumentdata.getSeksAvDeTiBeste()).isTrue();
        assertThat(dokumentdata.getHarBruktBruttoBeregningsgrunnlag()).isFalse();
        assertThat(dokumentdata.erUtenMinsterett()).isTrue();
        assertThat(dokumentdata.isØnskerJustertVedFødsel()).isTrue();
        assertThat(dokumentdata.isGraderingOgFulltUttakIAnnenAktivitet()).isFalse();
    }

    @Test
    void SjekkAtTotilkjentPerioderMedEnUttaksperiodeFårRiktigTapteDager() {
        // Arrange
        var behandling = opprettBehandling(BehandlingType.REVURDERING,
            List.of(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING, BehandlingÅrsakType.RE_HENDELSE_FØDSEL),
            List.of(Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING,
                Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK)).tilkjentYtelse(
                tilkjentYtelse().dagytelse(opprettTilkjentYtelseFP2()).build())
            .beregningsgrunnlag(opprettBeregningsgrunnlag(AktivitetStatusDto.ARBEIDSTAKER))
            .foreldrepenger(opprettUttaksresultat2())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        //Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        assertThat(dokumentdata.getPerioder()).hasSize(2);
        assertThat(dokumentdata.getPerioder().getFirst().getAntallTapteDager()).isEqualTo(23);
        assertThat(dokumentdata.getPerioder().get(1).getAntallTapteDager()).isEqualTo(23);
    }

    @Test
    void varierer_dagsats() {
        var hundreKronerDagsatsPeriode = Vedtaksperiode.ny().medPeriodeDagsats(100).build();
        var toHundreKronerDagsatsPeriode = Vedtaksperiode.ny().medPeriodeDagsats(200).build();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, hundreKronerDagsatsPeriode, toHundreKronerDagsatsPeriode))).isTrue();
        assertThat(harVarierendeDagsats(List.of(hundreKronerDagsatsPeriode, toHundreKronerDagsatsPeriode))).isTrue();
        assertThat(harVarierendeDagsats(List.of(hundreKronerDagsatsPeriode, hundreKronerDagsatsPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of())).isFalse();
    }

    @Test
    void starter_med_fullutbetaling() {
        var ingenUtbetaling = Vedtaksperiode.ny().medPrioritertUtbetalingsgrad(Prosent.NULL).medFullUtbetaling(false).build();
        var fullUtbetaling = Vedtaksperiode.ny().medFullUtbetaling(true).medPrioritertUtbetalingsgrad(Prosent.HUNDRE).build();
        assertThat(starterMedFullUtbetaling(List.of(ingenUtbetaling))).isFalse();
        assertThat(starterMedFullUtbetaling(List.of(ingenUtbetaling, fullUtbetaling))).isFalse();
        assertThat(starterMedFullUtbetaling(List.of(fullUtbetaling))).isTrue();
        assertThat(starterMedFullUtbetaling(List.of(fullUtbetaling, ingenUtbetaling))).isTrue();
    }

    @Test
    void ikke_varierende_dagsats_hvis_starter_med_avslag() {
        var avslåttPeriode = Vedtaksperiode.ny().medPeriodeDagsats(0).build();
        var toHundreKronerDagsatsPeriode = Vedtaksperiode.ny().medPeriodeDagsats(200).build();
        var hundreKronerDagsatsPeriode = Vedtaksperiode.ny().medPeriodeDagsats(100).build();
        assertThat(harVarierendeDagsats(List.of(avslåttPeriode, toHundreKronerDagsatsPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, avslåttPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, avslåttPeriode, toHundreKronerDagsatsPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, avslåttPeriode, hundreKronerDagsatsPeriode))).isTrue();
    }

    @Test
    void avslåtte_perioder_får_tidligstmottatt() {
        var tidligstMottatt = LocalDate.now();
        var avslåttUttaksperiode = foreldrepengerUttakPeriode().periodeResultatType(PeriodeResultatType.AVSLÅTT,
                PeriodeResultatÅrsak.SØKNADSFRIST.getKode())
            .fom(LocalDate.now())
            .tom(LocalDate.now().plusDays(10))
            .tidligstMottattDato(tidligstMottatt)
            .aktiviteter(List.of())
            .build();

        var resultatPerioder = VedtaksperiodeMapper.mapPerioderUtenBeregningsgrunnlag(List.of(avslåttUttaksperiode), Språkkode.NB);
        assertThat(resultatPerioder).hasSize(1);
        assertThat(resultatPerioder.getFirst().getTidligstMottattDato()).isEqualTo(formaterDato(tidligstMottatt, Språkkode.NB));
    }

    @Test
    void brutto_dagsats_når_aap() {
        var forventetMånedsinntekt = BRUTTO_BEREGNINGSGRUNNLAG / 12;
        var beregningsgrunnlagRegler = BeregningsgrunnlagMapper.mapRegelListe(opprettBeregningsgrunnlag(AktivitetStatusDto.ARBEIDSAVKLARINGSPENGER),
            arbeidsgiverTjeneste::hentArbeidsgiverNavn);

        assertThat(beregningsgrunnlagRegler).hasSize(1);
        assertThat(beregningsgrunnlagRegler.getFirst().getAndelListe()).hasSize(1);
        var andelsListe = beregningsgrunnlagRegler.getFirst().getAndelListe();

        assertThat(beregningsgrunnlagRegler.getFirst().getRegelStatus()).isEqualTo(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        assertThat(andelsListe.getFirst().getDagsats()).isEqualTo(DAGSATS);
        assertThat(andelsListe.getFirst().getAktivitetStatus()).isEqualTo(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        assertThat(andelsListe.getFirst().getMånedsinntekt()).isEqualTo(forventetMånedsinntekt);
        assertThat(andelsListe.getFirst().getÅrsinntekt()).isEqualTo(BRUTTO_BEREGNINGSGRUNNLAG);
    }

    @Test
    void revurdering_med_aap_praksis_endring() {
        var behandlingAap = opprettBehandling(BehandlingType.REVURDERING,
            List.of(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING, BehandlingÅrsakType.FEIL_PRAKSIS_BG_AAP_KOMBI),
            List.of(Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING, Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK)).build();

        var konsekvensForYtelseAAPPraksisendring = dokumentdataMapper.mapKonsekvensForInnvilgetYtelse(
            behandlingAap.behandlingsresultat().konsekvenserForYtelsen(), behandlingAap.behandlingÅrsakTyper());
        assertThat(konsekvensForYtelseAAPPraksisendring).isEqualTo("ENDRING_AAP_PRAKSISENDRING");
    }

    private BrevGrunnlagBuilder opprettBehandling(BehandlingType behandlingType,
                                                  List<BehandlingÅrsakType> behandlingÅrsaker,
                                                  List<Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen) {
        return brevGrunnlag().saksnummer(SAKSNUMMER)
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .behandlingType(behandlingType)
            .behandlingÅrsakTyper(behandlingÅrsaker)
            .behandlingsresultat(behandlingsresultat().behandlingResultatType(Behandlingsresultat.BehandlingResultatType.INNVILGET)
                .konsekvenserForYtelsen(konsekvenserForYtelsen)
                .skjæringstidspunkt(skjæringstidspunkt().utenMinsterett(true).build())
                .build())
            .relasjonsRolleType(RelasjonsRolleType.MORA)
            .språkkode(BrevGrunnlagDto.Språkkode.BOKMÅL)
            .familieHendelse(opprettFamiliehendelse());
    }

    private FamilieHendelse opprettFamiliehendelse() {
        return familieHendelse().barn(List.of()).termindato(LocalDate.now()).antallBarn(1).build();
    }

    private TilkjentYtelseDagytelseDto opprettTilkjentYtelseFP() {
        var andel1 = new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER_ORGNR, 0, 100,
            TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));
        var periode1 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE_FOM, PERIODE_FOM, 100, List.of(andel1));

        var andel2 = new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER_ORGNR, 200, 0,
            TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));
        var periode2 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE_TOM, PERIODE_TOM, 200, List.of(andel2));

        return new TilkjentYtelseDagytelseDto(List.of(periode1, periode2));
    }

    private TilkjentYtelseDagytelseDto opprettTilkjentYtelseFP2() {
        var andel1 = new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER_ORGNR, 100, 100,
            TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));
        var periode1 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(LocalDate.now(), LocalDate.now().plusDays(1), 100, List.of(andel1));

        var andel2 = new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER_ORGNR, 100, 100,
            TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));
        var periode2 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), 100,
            List.of(andel2));

        var andel3 = new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER_ORGNR, 200, 100,
            TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));
        var periode3 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), 200,
            List.of(andel3));

        return new TilkjentYtelseDagytelseDto(List.of(periode1, periode2, periode3));
    }

    private BeregningsgrunnlagDto opprettBeregningsgrunnlag(AktivitetStatusDto aktivitetStatus) {
        return new BeregningsgrunnlagDto(List.of(aktivitetStatus), HjemmelDto.F_14_7, BigDecimal.valueOf(GRUNNBELØP),
            List.of(lagBeregningsgrunnlagPeriode(aktivitetStatus)), true, true);
    }

    private BeregningsgrunnlagPeriodeDto lagBeregningsgrunnlagPeriode(AktivitetStatusDto aktivitetStatus) {
        var andel = new BeregningsgrunnlagAndelDto(DAGSATS, aktivitetStatus, BigDecimal.valueOf(BRUTTO_BEREGNINGSGRUNNLAG), null, false,
            OpptjeningAktivitetDto.ARBEID, LocalDate.now(), PERIODE_TOM,
            new BgAndelArbeidsforholdDto(ARBEIDSGIVER_ORGNR, null, BigDecimal.ZERO, BigDecimal.ZERO), false);

        return new BeregningsgrunnlagPeriodeDto(DAGSATS, BigDecimal.valueOf(BRUTTO_BEREGNINGSGRUNNLAG), BigDecimal.valueOf(30000), List.of(),
            LocalDate.now(), PERIODE_TOM, List.of(andel));
    }

    private Foreldrepenger opprettUttaksresultat() {
        var uttakAktivitet = foreldrepengerUttakAktivitet().trekkdager(BigDecimal.TEN)
            .utbetalingsgrad(BigDecimal.ZERO)
            .uttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
            .prosentArbeid(BigDecimal.valueOf(100))
            .trekkontoType(Foreldrepenger.TrekkontoType.FORELDREPENGER)
            .build();
        var uttakAktivitet1 = foreldrepengerUttakAktivitet().trekkdager(BigDecimal.TEN)
            .utbetalingsgrad(BigDecimal.ZERO)
            .uttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
            .prosentArbeid(BigDecimal.valueOf(200))
            .trekkontoType(Foreldrepenger.TrekkontoType.FORELDREPENGER_FØR_FØDSEL)
            .build();

        var uttakResultatPeriode1 = foreldrepengerUttakPeriode().aktiviteter(List.of(uttakAktivitet))
            .fom(LocalDate.now())
            .tom(LocalDate.now().plusDays(1))
            .periodeResultatType(PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.FOR_SEN_SØKNAD.getKode())
            .build();
        var uttakResultatPeriode2 = foreldrepengerUttakPeriode().aktiviteter(List.of(uttakAktivitet))
            .fom(PERIODE_FOM)
            .tom(PERIODE_TOM)
            .periodeResultatType(PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode())
            .build();
        var uttakResultatPeriode3 = foreldrepengerUttakPeriode().aktiviteter(List.of(uttakAktivitet1))
            .fom(LocalDate.now())
            .tom(LocalDate.now().plusDays(1))
            .periodeResultatType(PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.MOR_TAR_IKKE_ALLE_UKENE.getKode())
            .build();

        return foreldrepenger().stønadskontoer(List.of(stønadskonto().stønadskontotype(Foreldrepenger.Stønadskonto.Type.MØDREKVOTE)
                .maxDager(10)
                .saldo(DISPONIBLE_DAGER)
                .kontoUtvidelser(kontoUtvidelser().prematurdager(PREMATUR_DAGER).flerbarnsdager(0).build())
                .build(), stønadskonto().stønadskontotype(Foreldrepenger.Stønadskonto.Type.FELLESPERIODE)
                .maxDager(5)
                .saldo(DISPONIBLE_DAGER_FELLES)
                .kontoUtvidelser(kontoUtvidelser().prematurdager(0).flerbarnsdager(0).build())
                .build()))
            .tapteDagerFpff(TAPTE_DAGER_FPFF)
            .perioderSøker(List.of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3))
            .perioderAnnenpart(List.of())
            .ønskerJustertUttakVedFødsel(true)
            .dekningsgrad(Dekningsgrad.HUNDRE)
            .rettigheter(rettigheter())
            .build();
    }

    private static Rettigheter rettigheter() {
        return BrevGrunnlagBuilders.rettigheter().opprinnelig(Rettigheter.Rettighetstype.BEGGE_RETT).gjeldende(Rettigheter.Rettighetstype.BEGGE_RETT).build();
    }

    private Foreldrepenger opprettUttaksresultat2() {
        var uttakAktivitet = foreldrepengerUttakAktivitet().trekkdager(BigDecimal.valueOf(23L))
            .utbetalingsgrad(BigDecimal.ZERO)
            .uttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
            .prosentArbeid(BigDecimal.valueOf(100))
            .trekkontoType(Foreldrepenger.TrekkontoType.FORELDREPENGER)
            .build();

        var uttakResultatPeriode1 = foreldrepengerUttakPeriode().aktiviteter(List.of(uttakAktivitet))
            .fom(LocalDate.now())
            .tom(LocalDate.now().plusDays(4))
            .periodeResultatType(PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.FOR_SEN_SØKNAD.getKode())
            .build();
        var uttakResultatPeriode2 = foreldrepengerUttakPeriode().aktiviteter(List.of(uttakAktivitet))
            .fom(LocalDate.now().plusDays(5))
            .tom(LocalDate.now().plusDays(10))
            .periodeResultatType(PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.UKJENT.getKode())
            .build();

        return foreldrepenger().stønadskontoer(List.of(stønadskonto().stønadskontotype(Foreldrepenger.Stønadskonto.Type.MØDREKVOTE)
                .maxDager(10)
                .saldo(DISPONIBLE_DAGER)
                .kontoUtvidelser(kontoUtvidelser().prematurdager(PREMATUR_DAGER).flerbarnsdager(0).build())
                .build(), stønadskonto().stønadskontotype(Foreldrepenger.Stønadskonto.Type.FELLESPERIODE)
                .maxDager(5)
                .saldo(DISPONIBLE_DAGER_FELLES)
                .kontoUtvidelser(kontoUtvidelser().prematurdager(0).flerbarnsdager(0).build())
                .build()))
            .tapteDagerFpff(TAPTE_DAGER_FPFF)
            .perioderSøker(List.of(uttakResultatPeriode1, uttakResultatPeriode2))
            .perioderAnnenpart(List.of())
            .ønskerJustertUttakVedFødsel(true)
            .dekningsgrad(Dekningsgrad.HUNDRE)
            .rettigheter(rettigheter())
            .build();
    }
}
