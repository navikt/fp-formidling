package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnDagsats;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdataMapper.harVarierendeDagsats;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdataMapper.starterMedFullUtbetaling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp.of;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Stønadskonto;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.YtelseFordeling;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForMyeUtbetalt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;

@ExtendWith(MockitoExtension.class)
class ForeldrepengerInnvilgelseDokumentdataMapperTest {

    private static final LocalDate SØKNADSDATO = LocalDate.now().minusDays(1);
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

    private Behandling behandling;
    private DokumentFelles dokumentFelles;
    private DokumentHendelse dokumentHendelse;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private ForeldrepengerInnvilgelseDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        var dokumentData = lagStandardDokumentData(DokumentMalType.FORELDREPENGER_INNVILGELSE);
        dokumentdataMapper = new ForeldrepengerInnvilgelseDokumentdataMapper(brevParametere, domeneobjektProvider);

        behandling = opprettBehandling();
        dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        dokumentHendelse = lagStandardHendelseBuilder().build();
    }

    @Test
    void skal_mappe_felter_for_brev() {
        when(domeneobjektProvider.hentFagsakBackend(any(Behandling.class))).thenReturn(opprettFagsakBackend());
        when(domeneobjektProvider.hentSøknad(any(Behandling.class))).thenReturn(opprettSøknad());
        when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class))).thenReturn(opprettTilkjentYtelseFP());
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag(AktivitetStatus.ARBEIDSTAKER));
        when(domeneobjektProvider.hentForeldrepengerUttak(any(Behandling.class))).thenReturn(opprettUttaksresultat());
        when(domeneobjektProvider.hentSaldoer(any(Behandling.class))).thenReturn(opprettSaldoer());
        when(domeneobjektProvider.ytelseFordeling(any(Behandling.class))).thenReturn(new YtelseFordeling(true));
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        var tilkjentYtelseFP = opprettTilkjentYtelseFP();

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(dokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
        assertThat(dokumentdata.getFelles().getErUtkast()).isTrue();

        assertThat(dokumentdata.getBehandlingType()).isEqualTo(behandling.getBehandlingType().name());
        assertThat(dokumentdata.getBehandlingResultatType()).isEqualTo(behandling.getBehandlingsresultat().getBehandlingResultatType().name());
        assertThat(dokumentdata.getKonsekvensForInnvilgetYtelse()).isEqualTo("ENDRING_I_BEREGNING_OG_UTTAK");
        assertThat(dokumentdata.getDekningsgrad()).isEqualTo(DEKNINGSGRAD);
        assertThat(dokumentdata.getDagsats()).isEqualTo(finnDagsats(tilkjentYtelseFP));
        assertThat(dokumentdata.getMånedsbeløp()).isEqualTo(finnMånedsbeløp(tilkjentYtelseFP));
        assertThat(dokumentdata.getForMyeUtbetalt()).isEqualTo(ForMyeUtbetalt.GENERELL);
        assertThat(dokumentdata.getInntektMottattArbeidsgiver()).isTrue();
        assertThat(dokumentdata.getAnnenForelderHarRettVurdert()).isEqualTo(VurderingsKode.JA);
        assertThat(dokumentdata.getAnnenForelderHarRett()).isTrue();
        assertThat(dokumentdata.getAleneomsorgKode()).isEqualTo(VurderingsKode.JA);
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
        assertThat(dokumentdata.getPerioder().get(0).getStønadskontoType()).isEqualTo(StønadskontoType.FORELDREPENGER);

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
        assertThat(dokumentdata.isAnnenForelderRettEØS()).isTrue();
        assertThat(dokumentdata.isOppgittAnnenForelderRettEØS()).isTrue();
        assertThat(dokumentdata.isØnskerJustertVedFødsel()).isTrue();
        assertThat(dokumentdata.isGraderingOgFulltUttakIAnnenAktivitet()).isFalse();
    }

    @Test
    void SjekkAtTotilkjentPerioderMedEnUttaksperiodeFårRiktigTapteDager() {
        when(domeneobjektProvider.hentFagsakBackend(any(Behandling.class))).thenReturn(opprettFagsakBackend());
        when(domeneobjektProvider.hentSøknad(any(Behandling.class))).thenReturn(opprettSøknad());
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag(AktivitetStatus.ARBEIDSTAKER));
        when(domeneobjektProvider.hentSaldoer(any(Behandling.class))).thenReturn(opprettSaldoer());
        when(domeneobjektProvider.ytelseFordeling(any(Behandling.class))).thenReturn(new YtelseFordeling(true));
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class))).thenReturn(opprettTilkjentYtelseFP2());
        when(domeneobjektProvider.hentForeldrepengerUttak(any(Behandling.class))).thenReturn(opprettUttaksresultat2());

        //Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        assertThat(dokumentdata.getPerioder()).hasSize(2);
        assertThat(dokumentdata.getPerioder().get(0).getAntallTapteDager()).isEqualTo(23);
        assertThat(dokumentdata.getPerioder().get(1).getAntallTapteDager()).isEqualTo(23);
    }

    @Test
    void varierer_dagsats() {
        var hundreKronerDagsatsPeriode = Vedtaksperiode.ny()
            .medPeriodeDagsats(100)
            .build();
        var toHundreKronerDagsatsPeriode = Vedtaksperiode.ny()
            .medPeriodeDagsats(200)
            .build();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, hundreKronerDagsatsPeriode, toHundreKronerDagsatsPeriode))).isTrue();
        assertThat(harVarierendeDagsats(List.of(hundreKronerDagsatsPeriode, toHundreKronerDagsatsPeriode))).isTrue();
        assertThat(harVarierendeDagsats(List.of(hundreKronerDagsatsPeriode, hundreKronerDagsatsPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of())).isFalse();
    }

    @Test
    void starter_med_fullutbetaling() {
        var ingenUtbetaling = Vedtaksperiode.ny()
            .medPrioritertUtbetalingsgrad(Prosent.NULL)
            .medFullUtbetaling(false)
            .build();
        var fullUtbetaling = Vedtaksperiode.ny()
            .medFullUtbetaling(true)
            .medPrioritertUtbetalingsgrad(Prosent.HUNDRE)
            .build();
        assertThat(starterMedFullUtbetaling(List.of(ingenUtbetaling))).isFalse();
        assertThat(starterMedFullUtbetaling(List.of(ingenUtbetaling, fullUtbetaling))).isFalse();
        assertThat(starterMedFullUtbetaling(List.of(fullUtbetaling))).isTrue();
        assertThat(starterMedFullUtbetaling(List.of(fullUtbetaling, ingenUtbetaling))).isTrue();
    }

    @Test
    void ikke_varierende_dagsats_hvis_starter_med_avslag() {
        var avslåttPeriode = Vedtaksperiode.ny()
            .medPeriodeDagsats(0)
            .build();
        var toHundreKronerDagsatsPeriode = Vedtaksperiode.ny()
            .medPeriodeDagsats(200)
            .build();
        var hundreKronerDagsatsPeriode = Vedtaksperiode.ny()
            .medPeriodeDagsats(100)
            .build();
        assertThat(harVarierendeDagsats(List.of(avslåttPeriode, toHundreKronerDagsatsPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, avslåttPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, avslåttPeriode, toHundreKronerDagsatsPeriode))).isFalse();
        assertThat(harVarierendeDagsats(List.of(toHundreKronerDagsatsPeriode, avslåttPeriode, hundreKronerDagsatsPeriode))).isTrue();
    }
    @Test
    void avslåtte_perioder_får_tidligstmottatt() {
        var tidligstMottatt = LocalDate.now();
        var avslåttUttaksperiode = UttakResultatPeriode.ny()
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(10)))
            .medTidligstMottattDato(tidligstMottatt)
            .build();

        var resultatPerioder = VedtaksperiodeMapper.mapPerioderUtenBeregningsgrunnlag(List.of(avslåttUttaksperiode), Språkkode.NB);
        assertThat(resultatPerioder).hasSize(1);
        assertThat(resultatPerioder.getFirst().getTidligstMottattDato()).isEqualTo(formaterDato(tidligstMottatt, Språkkode.NB));

    }

    @Test
    void brutto_dagsats_når_aap() {
        var forventetMånedsinntekt = BRUTTO_BEREGNINGSGRUNNLAG / 12;
        var beregningsgrunnlagRegler = BeregningsgrunnlagMapper.mapRegelListe(opprettBeregningsgrunnlag(AktivitetStatus.ARBEIDSAVKLARINGSPENGER));


        assertThat(beregningsgrunnlagRegler).hasSize(1);
        assertThat(beregningsgrunnlagRegler.getFirst().getAndelListe()).hasSize(1);
        var andelsListe = beregningsgrunnlagRegler.getFirst().getAndelListe();

        assertThat(beregningsgrunnlagRegler.getFirst().getRegelStatus()).isEqualTo(AktivitetStatus.ARBEIDSAVKLARINGSPENGER.name());
        assertThat(andelsListe.getFirst().getDagsats()).isEqualTo(DAGSATS);
        assertThat(andelsListe.getFirst().getAktivitetStatus()).isEqualTo(AktivitetStatus.ARBEIDSAVKLARINGSPENGER.name());
        assertThat(andelsListe.getFirst().getMånedsinntekt()).isEqualTo(forventetMånedsinntekt);
        assertThat(andelsListe.getFirst().getÅrsinntekt()).isEqualTo(BRUTTO_BEREGNINGSGRUNNLAG);
    }

    private FagsakBackend opprettFagsakBackend() {
        return FagsakBackend.ny().medBrukerRolle(RelasjonsRolleType.MORA).medDekningsgrad(DEKNINGSGRAD).medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER).build();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        return new FamilieHendelse(1, 0, null, LocalDate.now(), null, null, false, true);
    }

    private Optional<Søknad> opprettSøknad() {
        return Optional.of(new Søknad(SØKNADSDATO, true));
    }

    private TilkjentYtelseForeldrepenger opprettTilkjentYtelseFP() {
        var arbeidsgiver = new Arbeidsgiver("1", "navn");
        return TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medDagsats(100L)
                .medPeriode(fraOgMedTilOgMed(PERIODE_FOM, PERIODE_FOM))
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medErBrukerMottaker(true)
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medStillingsprosent(BigDecimal.valueOf(100))
                    .medArbeidsgiver(arbeidsgiver)
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medDagsats(100L * 2)
                .medPeriode(fraOgMedTilOgMed(PERIODE_TOM, PERIODE_TOM))
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medErArbeidsgiverMottaker(true)
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medStillingsprosent(BigDecimal.valueOf(100))
                    .medArbeidsgiver(arbeidsgiver)
                    .build()))
                .build()))
            .build();
    }

    private TilkjentYtelseForeldrepenger opprettTilkjentYtelseFP2() {
        var arbeidsgiver = new Arbeidsgiver("1", "navn");
        return TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medDagsats(100L)
                .medPeriode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medErBrukerMottaker(true)
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medStillingsprosent(BigDecimal.valueOf(100))
                    .medArbeidsgiver(arbeidsgiver)
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medDagsats(100L)
                .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4)))
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medErArbeidsgiverMottaker(true)
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medStillingsprosent(BigDecimal.valueOf(100))
                    .medArbeidsgiver(arbeidsgiver)
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medDagsats(100L * 2)
                .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10)))
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medErArbeidsgiverMottaker(true)
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medStillingsprosent(BigDecimal.valueOf(100))
                    .medArbeidsgiver(arbeidsgiver)
                    .build()))
                .build()))
            .build();
    }

    private Beregningsgrunnlag opprettBeregningsgrunnlag(AktivitetStatus aktivitetStatus) {
        return Beregningsgrunnlag.ny()
            .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(aktivitetStatus))
            .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode(aktivitetStatus))
            .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
            .medhHjemmel(Hjemmel.F_14_7)
            .medBesteberegnet(true)
            .medSeksAvDeTiBeste(true)
            .build();


    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode(AktivitetStatus aktivitetStatus) {
        return BeregningsgrunnlagPeriode.ny()
            .medPeriode(fraOgMedTilOgMed(LocalDate.now(), PERIODE_TOM))
            .medDagsats(DAGSATS)
            .medBruttoPrÅr(new BigDecimal(BRUTTO_BEREGNINGSGRUNNLAG))
            .medAvkortetPrÅr(new BigDecimal(30000))
            .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListe(aktivitetStatus))
            .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListe(AktivitetStatus aktivitetStatus) {
        return of(BeregningsgrunnlagPrStatusOgAndel.ny()
            .medBruttoPrÅr(new BigDecimal(BRUTTO_BEREGNINGSGRUNNLAG))
            .medDagsats(DAGSATS)
            .medAktivitetStatus(aktivitetStatus)
            .build());
    }

    private ForeldrepengerUttak opprettUttaksresultat() {
        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
            .medTrekkdager(BigDecimal.TEN)
            .medUtbetalingsprosent(BigDecimal.ZERO)
            .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
            .medArbeidsprosent(BigDecimal.valueOf(100))
            .medTrekkonto(StønadskontoType.FORELDREPENGER)
            .build();
        var uttakAktivitet1 = UttakResultatPeriodeAktivitet.ny()
            .medTrekkdager(BigDecimal.TEN)
            .medUtbetalingsprosent(BigDecimal.ZERO)
            .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
            .medArbeidsprosent(BigDecimal.valueOf(200))
            .medTrekkonto(StønadskontoType.FORELDREPENGER_FØR_FØDSEL)
            .build();
        var uttakResultatPeriode1 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.FOR_SEN_SØKNAD)
            .medGraderingAvslagÅrsak(PeriodeResultatÅrsak.FOR_SEN_SØKNAD)
            .build();
        var uttakResultatPeriode2 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE_FOM, PERIODE_TOM))
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG)
            .build();
        var uttakResultatPeriode3 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet1))
            .medTidsperiode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.MOR_TAR_IKKE_ALLE_UKENE)
            .build();
        return new ForeldrepengerUttak(of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3), of(), true, true, true, true);
    }

    private ForeldrepengerUttak opprettUttaksresultat2() {
        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
            .medTrekkdager(BigDecimal.valueOf(23L))
            .medUtbetalingsprosent(BigDecimal.ZERO)
            .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
            .medArbeidsprosent(BigDecimal.valueOf(100))
            .medTrekkonto(StønadskontoType.FORELDREPENGER)
            .build();

        var uttakResultatPeriode1 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(4)))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.FOR_SEN_SØKNAD)
            .medGraderingAvslagÅrsak(PeriodeResultatÅrsak.FOR_SEN_SØKNAD)
            .build();
        var uttakResultatPeriode2 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10)))
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG)
            .build();

        return new ForeldrepengerUttak(of(uttakResultatPeriode1, uttakResultatPeriode2), of(), true, true, false, false);
    }


    private Saldoer opprettSaldoer() {
        var stønadskontoer = Set.of(new Stønadskonto(10, SaldoVisningStønadskontoType.MØDREKVOTE, DISPONIBLE_DAGER, PREMATUR_DAGER, 0),
            new Stønadskonto(5, SaldoVisningStønadskontoType.FELLESPERIODE, DISPONIBLE_DAGER_FELLES, 0, 0));
        return new Saldoer(stønadskontoer, TAPTE_DAGER_FPFF);
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.REVURDERING)
            .medBehandlingÅrsaker(of(BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING).build(),
                BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_HENDELSE_FØDSEL).build()))
            .medBehandlingsresultat(Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .medKonsekvenserForYtelsen(of(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK))
                .build())
            .medHarAvklartAnnenForelderRett(true)
            .medFagsakBackend(FagsakBackend.ny().medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER).build())
            .build();
    }
}
