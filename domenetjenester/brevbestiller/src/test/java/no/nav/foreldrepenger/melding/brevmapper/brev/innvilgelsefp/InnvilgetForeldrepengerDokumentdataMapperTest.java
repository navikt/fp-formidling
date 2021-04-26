package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktStatus;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.Dekningsgrad;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.ForMyeUtbetalt;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.InnvilgelseForeldrepengerDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.KonsekvensForInnvilgetYtelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.ytelsefordeling.OppgittRettighet;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnDagsats;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static no.nav.foreldrepenger.melding.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InnvilgetForeldrepengerDokumentdataMapperTest {

    private static final LocalDate SØKNADSDATO = LocalDate.now().minusDays(1);
    private static final Dekningsgrad DEKNINGSGRAD = Dekningsgrad._100;
    private static final int DISPONIBLE_DAGER = 5;
    private static final int DISPONIBLE_DAGER_FELLES = 10;
    private static final int TAPTE_DAGER_FPFF = 2;
    private static final LocalDate PERIODE_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE_TOM = LocalDate.now().plusDays(3);
    private static final int PREMATUR_DAGER = 2;
    private static final int KLAGEFRIST = 6;
    private static final int BRUTTO_BERENINGSGRUNNLAG = 400;
    private static final int GRUNNBELØP = 100000;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private BeregningsresultatFP beregningsresultatFP = opprettBeregningsresultatFP();

    private DokumentData dokumentData;

    private InnvilgetForeldrepengerDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.INNVILGET_FORELDREPENGER);
        dokumentdataMapper = new InnvilgetForeldrepengerDokumentdataMapper(brevParametere, domeneobjektProvider);

        when(domeneobjektProvider.hentFagsakBackend(any(Behandling.class))).thenReturn(opprettFagsakBackend());
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentSøknad(any(Behandling.class))).thenReturn(opprettSøknad());
        when(domeneobjektProvider.hentBeregningsresultatFP(any(Behandling.class))).thenReturn(beregningsresultatFP);
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentUttaksresultat(any(Behandling.class))).thenReturn(opprettUttaksresultat());
        when(domeneobjektProvider.hentYtelseFordeling(any(Behandling.class))).thenReturn(opprettYtelseFordeling());
        when(domeneobjektProvider.hentSaldoer(any(Behandling.class))).thenReturn(opprettSaldoer());
        when(domeneobjektProvider.hentAksjonspunkter(any(Behandling.class))).thenReturn(opprettAksjonspunkter());
    }

    @Test
    public void skal_mappe_felter_for_brev() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        InnvilgelseForeldrepengerDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FRITEKST);

        assertThat(dokumentdata.getBehandlingType()).isEqualTo(behandling.getBehandlingType());
        assertThat(dokumentdata.getBehandlingResultatType()).isEqualTo(behandling.getBehandlingsresultat().getBehandlingResultatType());
        assertThat(dokumentdata.getKonsekvensForInnvilgetYtelse()).isEqualTo(KonsekvensForInnvilgetYtelse.ENDRING_I_BEREGNING_OG_UTTAK);
        assertThat(dokumentdata.getSøknadsdato()).isEqualTo(formaterDatoNorsk(SØKNADSDATO));
        assertThat(dokumentdata.getDekningsgrad()).isEqualTo(DEKNINGSGRAD.getVerdi());
        assertThat(dokumentdata.getDagsats()).isEqualTo(finnDagsats(beregningsresultatFP));
        assertThat(dokumentdata.getMånedsbeløp()).isEqualTo(finnMånedsbeløp(beregningsresultatFP));
        assertThat(dokumentdata.getForMyeUtbetalt()).isEqualTo(ForMyeUtbetalt.GENERELL);
        assertThat(dokumentdata.isInntektMottattArbeidsgiver()).isTrue();
        assertThat(dokumentdata.isAnnenForelderHarRettVurdert()).isEqualTo(VurderingsKode.JA);
        assertThat(dokumentdata.isAnnenForelderHarRett()).isTrue();
        assertThat(dokumentdata.getAleneomsorgKode()).isEqualTo(VurderingsKode.JA);
        assertThat(dokumentdata.barnErFødt()).isFalse();
        assertThat(dokumentdata.årsakErFødselshendelse()).isTrue();
        assertThat(dokumentdata.isIkkeOmsorg()).isFalse();
        assertThat(dokumentdata.isGjelderMor()).isTrue();
        assertThat(dokumentdata.isGjelderFødsel()).isTrue();
        assertThat(dokumentdata.delvisRefusjon()).isTrue();
        assertThat(dokumentdata.ingenRefusjon()).isFalse();
        assertThat(dokumentdata.erfbEllerRvInnvilget()).isTrue();
        assertThat(dokumentdata.fullRefusjon()).isFalse();
        assertThat(dokumentdata.getAntallPerioder()).isEqualTo(3);
        assertThat(dokumentdata.harInnvilgedePerioder()).isTrue();
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
        assertThat(dokumentdata.getPerioder()).hasSize(3);

        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);

        assertThat(dokumentdata.isInkludereUtbetaling()).isTrue();
        assertThat(dokumentdata.isInkludereGradering()).isFalse();
        assertThat(dokumentdata.isInkludereInnvilget()).isTrue();
        assertThat(dokumentdata.isInkludereAvslag()).isTrue();

        assertThat(dokumentdata.getBeregningsgrunnlagregler()).hasSize(1);
        assertThat(dokumentdata.getBruttoBeregningsgrunnlag()).isEqualTo(BRUTTO_BERENINGSGRUNNLAG);
        assertThat(dokumentdata.getSeksG()).isEqualTo(GRUNNBELØP * 6);
        assertThat(dokumentdata.isInntektOverSeksG()).isFalse();
        assertThat(dokumentdata.erBesteberegning()).isTrue();
        assertThat(dokumentdata.harBruktBruttoBeregningsgrunnlag()).isFalse();
    }

    private FagsakBackend opprettFagsakBackend() {
        return FagsakBackend.ny().medBrukerRolle(RelasjonsRolleType.MORA).build();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        FamilieHendelse.OptionalDatoer optionalDatoer = new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.of(LocalDate.now()), Optional.empty(), Optional.empty());
        return new FamilieHendelse(BigInteger.valueOf(1), false, true, FamilieHendelseType.TERMIN, optionalDatoer);
    }

    private Optional<Søknad> opprettSøknad() {
        return Optional.of(new Søknad(SØKNADSDATO, LocalDate.now(), new OppgittRettighet(true)));
    }

    private BeregningsresultatFP opprettBeregningsresultatFP() {
        Arbeidsgiver arbeidsgiver = new Arbeidsgiver("1", "navn");
        return BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
                                .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                        .medBrukerErMottaker(true)
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medStillingsprosent(BigDecimal.valueOf(100))
                                        .medArbeidsgiver(arbeidsgiver)
                                        .build()))
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
                                .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                        .medArbeidsgiverErMottaker(true)
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
                .medBesteberegnet(true)
                .build();
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode() {
        return BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
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

    private UttakResultatPerioder opprettUttaksresultat() {
        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
                .medArbeidsprosent(BigDecimal.valueOf(100))
                .build();
        UttakResultatPeriode uttakResultatPeriode1 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1)))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.FOR_SEN_SØKNAD)
                .medGraderingAvslagÅrsak(PeriodeResultatÅrsak.FOR_SEN_SØKNAD)
                .build();
        UttakResultatPeriode uttakResultatPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(fraOgMedTilOgMed(PERIODE_FOM, PERIODE_TOM))
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG)
                .build();
        return UttakResultatPerioder.ny()
                .medPerioder(of(uttakResultatPeriode1, uttakResultatPeriode2))
                .medAnnenForelderHarRett(true)
                .medAleneomsorg(true)
                .build();
    }

    private YtelseFordeling opprettYtelseFordeling() {
        return new YtelseFordeling(DEKNINGSGRAD);
    }

    private Saldoer opprettSaldoer() {
        Set<Stønadskonto> stønadskontoer = Set.of(
                new Stønadskonto(10, StønadskontoType.MØDREKVOTE, DISPONIBLE_DAGER, PREMATUR_DAGER, 0),
                new Stønadskonto(5, StønadskontoType.FELLESPERIODE, DISPONIBLE_DAGER_FELLES, 0, 0));
        return new Saldoer(stønadskontoer, TAPTE_DAGER_FPFF);
    }

    private List<Aksjonspunkt> opprettAksjonspunkter() {
        return of(Aksjonspunkt.ny()
                .medAksjonspunktDefinisjon(AksjonspunktDefinisjon.AVKLAR_FAKTA_ANNEN_FORELDER_HAR_IKKE_RETT)
                .medAksjonspunktStatus(AksjonspunktStatus.UTFØRT)
                .build());
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
                .medId(1L)
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingÅrsaker(of(
                        BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING).build(),
                        BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_HENDELSE_FØDSEL).build()))
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                        .medKonsekvenserForYtelsen(of(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK))
                        .build())
                .build();
    }
}