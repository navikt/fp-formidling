package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp.of;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.SvangerskapspengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class SvangerskapspengerInnvilgelseDokumentdataMapperTest {

    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().minusDays(4);
    private static final LocalDate PERIODE2_FOM = LocalDate.now();
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(12);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(15);
    private static final LocalDate PERIODE4_FOM = LocalDate.now().plusDays(17);
    private static final LocalDate PERIODE4_TOM = LocalDate.now().plusDays(18);
    private static final String ARBEIDSGIVER1_NAVN = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER2_NAVN = "Arbeidsgiver2 AS";
    private static final String ARBEIDSGIVER3_NAVN = "Arbeidsgiver3 AS";
    private static final int BRUTTO_BERENINGSGRUNNLAG = 720000;
    private static final int BRUTTO_BERENINGSGRUNNLAG_SN = 100000;
    private static final int BRUTTO_BERENINGSGRUNNLAG_FL = 240000;
    private static final int GRUNNBELØP = 100000;
    private static final Long DAGSATS_PERIODE1 = 500L;
    private static final Long DAGSATS_PERIODE2 = 1000L;
    private static final Long DAGSATS_PERIODE3 = 0L;
    private static final Long NATURALYTELSE_TILKOMMET = 100L;
    private static final Long NATURALYTELSE_BORTFALT = 50L;
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);
    private static final int KLAGEFRIST = 6;
    private static final Long UTBETALINGSGRAD_PERIODE1 = 50L;
    private static final Long UTBETALINGSGRAD_PERIODE2 = 100L;
    private static final Long UTBETALINGSGRAD_PERIODE3 = 0L;
    private static final Long UTBETALINGSGRAD_PERIODE4 = 0L;
    private static final PeriodeIkkeOppfyltÅrsak PERIODE_IKKE_OPPFYLT_ÅRSAK = PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE;
    private static final ArbeidsforholdIkkeOppfyltÅrsak ARBEIDSFORHOLD_IKKE_OPPFYLT_ÅRSAK = ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private Arbeidsgiver ARBEIDSGIVER1 = new Arbeidsgiver("1", ARBEIDSGIVER1_NAVN);
    private Arbeidsgiver ARBEIDSGIVER3 = new Arbeidsgiver("3", ARBEIDSGIVER3_NAVN);

    private SvangerskapspengerInnvilgelseDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE);
        dokumentdataMapper = new SvangerskapspengerInnvilgelseDokumentdataMapper(domeneobjektProvider, brevParametere);

        MottattDokument mottattDokument = new MottattDokument(SØKNAD_DATO, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD);
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(of(mottattDokument));
        when(domeneobjektProvider.hentUttaksresultatSvp(any(Behandling.class))).thenReturn(opprettSvpUttaksresultat());
    }

    @Test
    public void skal_mappe_felter_for_førstegangsbehandling() {
        // Arrange
        Behandling behandling = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD).build();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class))).thenReturn(opprettTilkjentYtelse());

        // Act
        SvangerskapspengerInnvilgelseDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

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
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(Fritekst.fra(FRITEKST));
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getRevurdering()).isFalse();
        assertThat(dokumentdata.getRefusjonTilBruker()).isTrue();
        assertThat(dokumentdata.getAntallRefusjonerTilArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getStønadsperiodeTom()).isEqualTo(formaterDatoNorsk(PERIODE2_TOM));
        assertThat(dokumentdata.getMånedsbeløp()).isEqualTo(DAGSATS_PERIODE1 * 260 / 12);
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getAntallUttaksperioder()).isEqualTo(2);

        assertThat(dokumentdata.getUttaksaktiviteter()).hasSize(1);
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getAktivitetsbeskrivelse()).isEqualTo(ARBEIDSGIVER1_NAVN);
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getUttaksperioder()).hasSize(2);
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getUttaksperioder().get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getUttaksperioder().get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getUttaksperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(UTBETALINGSGRAD_PERIODE1.intValue()));
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getUttaksperioder().get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getUttaksperioder().get(1).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(dokumentdata.getUttaksaktiviteter().get(0).getUttaksperioder().get(1).getUtbetalingsgrad()).isEqualTo(Prosent.of(UTBETALINGSGRAD_PERIODE2.intValue()));

        assertThat(dokumentdata.getUtbetalingsperioder()).hasSize(2);
        assertThat(dokumentdata.getUtbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(dokumentdata.getUtbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(dokumentdata.getUtbetalingsperioder().get(0).getPeriodeDagsats()).isEqualTo(DAGSATS_PERIODE1);
        assertThat(dokumentdata.getUtbetalingsperioder().get(0).getUtbetaltTilSøker()).isEqualTo(DAGSATS_PERIODE1);
        assertThat(dokumentdata.getUtbetalingsperioder().get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(dokumentdata.getUtbetalingsperioder().get(1).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(dokumentdata.getUtbetalingsperioder().get(1).getPeriodeDagsats()).isEqualTo(DAGSATS_PERIODE2);
        assertThat(dokumentdata.getUtbetalingsperioder().get(1).getUtbetaltTilSøker()).isEqualTo(0);

        assertThat(dokumentdata.getAvslagsperioder()).hasSize(1);
        assertThat(dokumentdata.getAvslagsperioder().get(0).getPeriodeFom()).isEqualTo(PERIODE3_FOM);
        assertThat(dokumentdata.getAvslagsperioder().get(0).getPeriodeTom()).isEqualTo(PERIODE3_TOM);
        assertThat(dokumentdata.getAvslagsperioder().get(0).getÅrsak()).isEqualTo(Årsak.of(PERIODE_IKKE_OPPFYLT_ÅRSAK.getKode()));

        assertThat(dokumentdata.getAvslåtteAktiviteter()).hasSize(1);
        assertThat(dokumentdata.getAvslåtteAktiviteter().get(0).getÅrsak()).isEqualTo(Årsak.of(ARBEIDSFORHOLD_IKKE_OPPFYLT_ÅRSAK.getKode()));
        assertThat(dokumentdata.getAvslåtteAktiviteter().get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER3_NAVN);
        assertThat(dokumentdata.getAvslåtteAktiviteter().get(0).getErFL()).isFalse();
        assertThat(dokumentdata.getAvslåtteAktiviteter().get(0).getErSN()).isFalse();

        assertThat(dokumentdata.getInkludereBeregning()).isTrue();

        assertThat(dokumentdata.getArbeidsforhold()).hasSize(1);
        assertThat(dokumentdata.getArbeidsforhold().get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER1_NAVN);
        assertThat(dokumentdata.getArbeidsforhold().get(0).getMånedsinntekt()).isEqualTo(BRUTTO_BERENINGSGRUNNLAG / 12);

        assertThat(dokumentdata.getSelvstendigNæringsdrivende()).isNull();
        assertThat(dokumentdata.getFrilanser()).isNull();

        assertThat(dokumentdata.getNaturalytelser()).hasSize(1); //Naturalytelse i første periode skal ikke tas med
        assertThat(dokumentdata.getNaturalytelser().get(0).getStatus()).isEqualTo(Naturalytelse.NaturalytelseStatus.BORTFALLER);
        assertThat(dokumentdata.getNaturalytelser().get(0).getEndringsdato()).isEqualTo(formaterDatoNorsk(PERIODE2_FOM));
        assertThat(dokumentdata.getNaturalytelser().get(0).getNyDagsats()).isEqualTo(DAGSATS_PERIODE2);
        assertThat(dokumentdata.getNaturalytelser().get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER1_NAVN);

        assertThat(dokumentdata.getBruttoBeregningsgrunnlag()).isEqualTo(of(GRUNNBELØP * 6));
        assertThat(dokumentdata.getMilitærSivil()).isFalse();
        assertThat(dokumentdata.getInntektOver6G()).isTrue();
        assertThat(dokumentdata.getSeksG()).isEqualTo(GRUNNBELØP * 6);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4");
    }

    @Test
    public void skal_mappe_felter_for_revurdering() {
        // Arrange
        Behandling behandling = opprettBehandling(BehandlingType.REVURDERING)
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                        .medKonsekvenserForYtelsen(of(KonsekvensForYtelsen.ENDRING_I_BEREGNING))
                        .build())
                .build();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class))).thenReturn(opprettTilkjentYtelse());

        Behandling originalBehandling = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                        .build())
                .build();
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)).thenReturn(Optional.of(originalBehandling));

        when(domeneobjektProvider.hentFamiliehendelse(eq(originalBehandling)))
                .thenReturn(opprettFamilieHendelse(LocalDate.now().minusDays(1)));
        when(domeneobjektProvider.hentFamiliehendelse(eq(behandling)))
                .thenReturn(opprettFamilieHendelse(LocalDate.now().minusDays(2)));

        // Act
        SvangerskapspengerInnvilgelseDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getRevurdering()).isTrue();
        assertThat(dokumentdata.getEndretFraAvslag()).isTrue();
        assertThat(dokumentdata.getUtbetalingEndret()).isTrue();
        assertThat(dokumentdata.getTermindatoEndret()).isTrue();
    }

    @Test
    public void skal_mappe_selvstendig_næringsdrivende_når_det_er_i_første_periode() {
        // Arrange
        Behandling behandling = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD).build();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlagMedSNIFørstePeriode());
        when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class))).thenReturn(opprettTilkjentYtelseMedSNIFørstePeriode());

        // Act
        SvangerskapspengerInnvilgelseDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getSelvstendigNæringsdrivende()).isNotNull();
        assertThat(dokumentdata.getSelvstendigNæringsdrivende().getNyoppstartet()).isTrue();
        assertThat(dokumentdata.getSelvstendigNæringsdrivende().getÅrsinntekt()).isEqualTo(of(BRUTTO_BERENINGSGRUNNLAG_SN));
        assertThat(dokumentdata.getSelvstendigNæringsdrivende().getSistLignedeÅr()).isEqualTo(PERIODE1_TOM.getYear());
        assertThat(dokumentdata.getSelvstendigNæringsdrivende().getInntektLavere_AT_SN()).isFalse();
        assertThat(dokumentdata.getSelvstendigNæringsdrivende().getInntektLavere_AT_FL_SN()).isFalse();
        assertThat(dokumentdata.getSelvstendigNæringsdrivende().getInntektLavere_FL_SN()).isFalse();
    }

    @Test
    public void skal_mappe_frilanser_når_det_er_i_første_periode() {
        // Arrange
        Behandling behandling = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD).build();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();
        when(domeneobjektProvider.hentBeregningsgrunnlag(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlagMedFLIFørstePeriode());
        when(domeneobjektProvider.hentTilkjentYtelseForeldrepenger(any(Behandling.class))).thenReturn(opprettTilkjentYtelseMedFLIFørstePeriode());

        // Act
        SvangerskapspengerInnvilgelseDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFrilanser()).isNotNull();
        assertThat(dokumentdata.getFrilanser().getMånedsinntekt()).isEqualTo(of(BRUTTO_BERENINGSGRUNNLAG_FL / 12));
    }

    private Behandling.Builder opprettBehandling(BehandlingType behandlingType) {
        return Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(behandlingType)
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .medSpråkkode(Språkkode.NB);
    }

    private FamilieHendelse opprettFamilieHendelse(LocalDate termindato) {
        return new FamilieHendelse(BigInteger.ONE, 0,  true, true, FamilieHendelseType.TERMIN,
                new FamilieHendelse.OptionalDatoer(Optional.of(LocalDate.now()), Optional.of(termindato), Optional.empty(), Optional.empty()));
    }

    private Beregningsgrunnlag opprettBeregningsgrunnlag() {
        return Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode1())
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode2())
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode3())
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build();
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode1() {
        return BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medDagsats(DAGSATS_PERIODE1)
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG))
                .medAvkortetPrÅr(new BigDecimal(GRUNNBELØP * 6))
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListePeriode1())
                .medperiodeÅrsaker(of(PeriodeÅrsak.NATURALYTELSE_TILKOMMER))
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListePeriode1() {
        return of(BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG))
                .medDagsats(DAGSATS_PERIODE1)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .medBgAndelArbeidsforhold(lagBgAndelArbeidsforholdPeriode1())
                .build());
    }

    private BGAndelArbeidsforhold lagBgAndelArbeidsforholdPeriode1() {
        return new BGAndelArbeidsforhold(ARBEIDSGIVER1, ArbeidsforholdRef.ref("1"),
                BigDecimal.valueOf(NATURALYTELSE_TILKOMMET), BigDecimal.ZERO);
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode2() {
        return BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medDagsats(DAGSATS_PERIODE2)
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG))
                .medAvkortetPrÅr(new BigDecimal(GRUNNBELØP * 6))
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListePeriode2())
                .medperiodeÅrsaker(of(PeriodeÅrsak.NATURALYTELSE_BORTFALT))
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListePeriode2() {
        return of(BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG))
                .medDagsats(DAGSATS_PERIODE1)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .medBgAndelArbeidsforhold(lagBgAndelArbeidsforholdPeriode2())
                .build());
    }

    private BGAndelArbeidsforhold lagBgAndelArbeidsforholdPeriode2() {
        return new BGAndelArbeidsforhold(ARBEIDSGIVER1, ArbeidsforholdRef.ref("1"),
                BigDecimal.valueOf(NATURALYTELSE_BORTFALT), BigDecimal.ZERO);
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode3() {
        return BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .medDagsats(DAGSATS_PERIODE3)
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_SN))
                .medAvkortetPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_SN))
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListePeriode3())
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListePeriode3() {
        return of(BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_SN))
                .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                .medNyIArbeidslivet(true)
                .medBeregningsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .build());
    }

    private Beregningsgrunnlag opprettBeregningsgrunnlagMedSNIFørstePeriode() {
        return Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriodeSN())
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build();
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriodeSN() {
        return BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medDagsats(DAGSATS_PERIODE1)
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_SN))
                .medAvkortetPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_SN))
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListePeriodeSN())
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListePeriodeSN() {
        return of(BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_SN))
                .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                .medNyIArbeidslivet(true)
                .medBeregningsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .build());
    }

    private Beregningsgrunnlag opprettBeregningsgrunnlagMedFLIFørstePeriode() {
        return Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.FRILANSER))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriodeFL())
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build();
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriodeFL() {
        return BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medDagsats(DAGSATS_PERIODE1)
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_FL))
                .medAvkortetPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_FL))
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListePeriodeFL())
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListePeriodeFL() {
        return of(BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(new BigDecimal(BRUTTO_BERENINGSGRUNNLAG_FL))
                .medAktivitetStatus(AktivitetStatus.FRILANSER)
                .medNyIArbeidslivet(true)
                .medBeregningsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .build());
    }

    private TilkjentYtelseForeldrepenger opprettTilkjentYtelse() {
        return TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(DAGSATS_PERIODE1)
                                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medErBrukerMottaker(true)
                                        .medUtbetalesTilBruker(DAGSATS_PERIODE1.intValue())
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medStillingsprosent(BigDecimal.valueOf(100))
                                        .medArbeidsgiver(ARBEIDSGIVER1)
                                        .build()))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(DAGSATS_PERIODE2)
                                .medPeriode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medErArbeidsgiverMottaker(true)
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medStillingsprosent(BigDecimal.valueOf(100))
                                        .medArbeidsgiver(ARBEIDSGIVER1)
                                        .build()))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(DAGSATS_PERIODE3)
                                .medPeriode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                                        .medStillingsprosent(BigDecimal.valueOf(100))
                                        .build()))
                                .build()))
                .build();
    }

    private TilkjentYtelseForeldrepenger opprettTilkjentYtelseMedSNIFørstePeriode() {
        return TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(DAGSATS_PERIODE1)
                                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                                        .medStillingsprosent(BigDecimal.valueOf(100))
                                        .build()))
                                .build()))
                .build();
    }

    private TilkjentYtelseForeldrepenger opprettTilkjentYtelseMedFLIFørstePeriode() {
        return TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(DAGSATS_PERIODE1)
                                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medAktivitetStatus(AktivitetStatus.FRILANSER)
                                        .medStillingsprosent(BigDecimal.valueOf(100))
                                        .build()))
                                .build()))
                .build();
    }

    private SvpUttaksresultat opprettSvpUttaksresultat() {
        SvpUttakResultatPeriode uttakPeriode1 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER1_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_PERIODE1)
                .build();
        SvpUttakResultatPeriode uttakPeriode2 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER1_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_PERIODE2)
                .build();
        SvpUttakResultatPeriode uttakPeriode3 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER2_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medUtbetalingsgrad(UTBETALINGSGRAD_PERIODE3)
                .medPeriodeIkkeOppfyltÅrsak(PERIODE_IKKE_OPPFYLT_ÅRSAK)
                .build();
        SvpUttakResultatPeriode uttakPeriode4 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER3_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_PERIODE4)
                .build();
        SvpUttakResultatArbeidsforhold svpUttakResultatArbeidsforhold1 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(of(uttakPeriode1, uttakPeriode2))
                .build();
        SvpUttakResultatArbeidsforhold svpUttakResultatArbeidsforhold2 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(of(uttakPeriode3))
                .build();
        SvpUttakResultatArbeidsforhold svpUttakResultatArbeidsforhold3 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(of(uttakPeriode4))
                .medArbeidsforholdIkkeOppfyltÅrsak(ARBEIDSFORHOLD_IKKE_OPPFYLT_ÅRSAK)
                .medArbeidsgiver(ARBEIDSGIVER3)
                .build();
        return SvpUttaksresultat.Builder.ny()
                .leggTilUttakResultatArbeidsforhold(svpUttakResultatArbeidsforhold1)
                .leggTilUttakResultatArbeidsforhold(svpUttakResultatArbeidsforhold2)
                .leggTilUttakResultatArbeidsforhold(svpUttakResultatArbeidsforhold3)
                .build();
    }
}