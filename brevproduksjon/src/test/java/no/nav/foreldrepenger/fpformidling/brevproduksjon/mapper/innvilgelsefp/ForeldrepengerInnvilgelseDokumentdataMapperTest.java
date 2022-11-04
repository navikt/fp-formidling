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
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp.of;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForMyeUtbetalt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.SaldoVisningStønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.Stønadskonto;
import no.nav.foreldrepenger.fpformidling.uttak.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.YtelseFordeling;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

@ExtendWith(MockitoExtension.class)
public class ForeldrepengerInnvilgelseDokumentdataMapperTest {

    private static final LocalDate SØKNADSDATO = LocalDate.now().minusDays(1);
    private static final int DEKNINGSGRAD = 100;
    private static final int DISPONIBLE_DAGER = 5;
    private static final int DISPONIBLE_DAGER_FELLES = 10;
    private static final int TAPTE_DAGER_FPFF = 2;
    private static final LocalDate PERIODE_FOM = LocalDate.now().plusMonths(4);
    private static final LocalDate PERIODE_TOM = LocalDate.now().plusMonths(4).plusDays(3);
    private static final int PREMATUR_DAGER = 2;
    private static final int KLAGEFRIST = 6;
    private static final int BRUTTO_BERENINGSGRUNNLAG = 400;
    private static final int GRUNNBELØP = 100000;

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

        when(domeneobjektProvider.hentFagsakBackend(any(Behandling.class))).thenReturn(opprettFagsakBackend());
        when(domeneobjektProvider.hentSøknad(any(Behandling.class))).thenReturn(opprettSøknad());
        lenient().when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class)))
                .thenReturn(opprettTilkjentYtelseFP());
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        lenient().when(domeneobjektProvider.hentForeldrepengerUttak(any(Behandling.class))).thenReturn(opprettUttaksresultat());
        when(domeneobjektProvider.hentSaldoer(any(Behandling.class))).thenReturn(opprettSaldoer());
        when(domeneobjektProvider.utenMinsterett(any(Behandling.class))).thenReturn(true);
        when(domeneobjektProvider.ytelseFordeling(any(Behandling.class))).thenReturn(new YtelseFordeling(true));
    }

    @Test
    void skal_mappe_felter_for_brev() {
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
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(dokumentdata.getFelles().getErUtkast()).isTrue();

        assertThat(dokumentdata.getBehandlingType()).isEqualTo(behandling.getBehandlingType().name());
        assertThat(dokumentdata.getBehandlingResultatType()).isEqualTo(
                behandling.getBehandlingsresultat().getBehandlingResultatType().name());
        assertThat(dokumentdata.getKonsekvensForInnvilgetYtelse()).isEqualTo("ENDRING_I_BEREGNING_OG_UTTAK");
        assertThat(dokumentdata.getSøknadsdato()).isEqualTo(formaterDatoNorsk(SØKNADSDATO));
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
        assertThat(dokumentdata.getAntallInnvilgedePerioder()).isEqualTo(1);
        assertThat(dokumentdata.getAntallAvslåttePerioder()).isEqualTo(3);
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getDagerTaptFørTermin()).isEqualTo(TAPTE_DAGER_FPFF);
        assertThat(dokumentdata.getDisponibleDager()).isEqualTo(DISPONIBLE_DAGER);
        assertThat(dokumentdata.getDisponibleFellesDager()).isEqualTo(DISPONIBLE_DAGER_FELLES);
        assertThat(dokumentdata.getSisteDagAvSistePeriode()).isEqualTo(formaterDatoNorsk(PERIODE_TOM));
        assertThat(dokumentdata.getStønadsperiodeFom()).isEqualTo(formaterDatoNorsk(PERIODE_FOM));
        assertThat(dokumentdata.getStønadsperiodeTom()).isEqualTo(formaterDatoNorsk(PERIODE_TOM));
        assertThat(dokumentdata.getForeldrepengeperiodenUtvidetUker()).isEqualTo(0);
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(1);
        assertThat(dokumentdata.getPrematurDager()).isEqualTo(PREMATUR_DAGER);
        assertThat(dokumentdata.getAntallDødeBarn()).isEqualTo(0);
        assertThat(dokumentdata.getDødsdato()).isNull();
        assertThat(dokumentdata.getMorKanSøkeOmDagerFørFødsel()).isTrue();
        assertThat(dokumentdata.getPerioder()).hasSize(4);
        assertThat(dokumentdata.getPerioder().get(0).getStønadskontoType()).isEqualTo(StønadskontoType.FORELDREPENGER);

        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getLovhjemlerUttak()).isEqualTo("forvaltningsloven § 35");
        assertThat(dokumentdata.getLovhjemlerBeregning()).isEqualTo("§ 14-7 og forvaltningsloven § 35");

        assertThat(dokumentdata.getInkludereUtbetaling()).isTrue();
        assertThat(dokumentdata.getInkludereUtbetNårGradering()).isFalse();
        assertThat(dokumentdata.getInkludereInnvilget()).isTrue();
        assertThat(dokumentdata.getInkludereAvslag()).isTrue();
        assertThat(dokumentdata.getInkludereNyeOpplysningerUtbet()).isFalse();

        assertThat(dokumentdata.getBeregningsgrunnlagregler()).hasSize(1);
        assertThat(dokumentdata.getBruttoBeregningsgrunnlag()).isEqualTo(of(BRUTTO_BERENINGSGRUNNLAG));
        assertThat(dokumentdata.getSeksG()).isEqualTo(GRUNNBELØP * 6);
        assertThat(dokumentdata.getInntektOverSeksG()).isFalse();
        assertThat(dokumentdata.getErBesteberegning()).isTrue();
        assertThat(dokumentdata.getHarBruktBruttoBeregningsgrunnlag()).isFalse();
        assertThat(dokumentdata.erUtenMinsterett()).isTrue();
        assertThat(dokumentdata.isAnnenForelderRettEØS()).isTrue();
        assertThat(dokumentdata.isOppgittAnnenForelderRettEØS()).isTrue();
        assertThat(dokumentdata.isØnskerJustertVedFødsel()).isTrue();
        assertThat(dokumentdata.isGraderingOgFulltUttakIAnnenAktivitet()).isFalse();
    }

    @Test
    void SjekkAtTotilkjentPerioderMedEnUttaksperiodeFårRiktigTapteDager() {

        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class))).thenReturn(opprettTilkjentYtelseFP2());
        when(domeneobjektProvider.hentForeldrepengerUttak(any(Behandling.class))).thenReturn(opprettUttaksresultat2());

        //Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        assertThat(dokumentdata.getPerioder()).hasSize(2);
        assertThat(dokumentdata.getPerioder().get(0).getAntallTapteDager()).isEqualTo(23);
        assertThat(dokumentdata.getPerioder().get(1).getAntallTapteDager()).isEqualTo(23);
    }

    private FagsakBackend opprettFagsakBackend() {
        return FagsakBackend.ny().medBrukerRolle(RelasjonsRolleType.MORA).medDekningsgrad(DEKNINGSGRAD).build();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        return new FamilieHendelse(FamilieHendelseType.TERMIN, 1, 0, null, LocalDate.now(), null, null, false, true);
    }

    private Optional<Søknad> opprettSøknad() {
        return Optional.of(new Søknad(SØKNADSDATO, true));
    }

    private TilkjentYtelseForeldrepenger opprettTilkjentYtelseFP() {
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
                        .medDagsats(100L * 2)
                        .medPeriode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
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

    private Beregningsgrunnlag opprettBeregningsgrunnlag() {
        return Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode())
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build();


    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode() {
        return BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(LocalDate.now(), PERIODE_TOM))
                .medDagsats(100L)
                .medBruttoPrÅr(new BigDecimal(200))
                .medAvkortetPrÅr(new BigDecimal(300))
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListe())
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListe() {
        return of(BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG))
                .medDagsats(500L)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
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
        return new ForeldrepengerUttak(of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3), of(), true, true, true,
                true);
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
                .medBehandlingÅrsaker(
                        of(BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING).build(),
                                BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_HENDELSE_FØDSEL).build()))
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                        .medKonsekvenserForYtelsen(of(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK))
                        .build())
                .medHarAvklartAnnenForelderRett(true)
                .build();
    }
}
