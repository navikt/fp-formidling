package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.brevGrunnlag;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakArbeidsforhold;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.tilkjentYtelse;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.BrevGrunnlagBuilder;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

class SvangerskapspengerInnvilgelseDokumentdataMapperTest {

    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().minusDays(4);
    private static final LocalDate PERIODE2_FOM = LocalDate.now();
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(12);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(15);
    private static final String ARBEIDSGIVER1_ORGNR = "1";
    private static final String ARBEIDSGIVER2_ORGNR = "2";
    private static final String ARBEIDSGIVER3_ORGNR = "3";
    private static final String ARBEIDSGIVER1_NAVN = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER2_NAVN = "Arbeidsgiver2 AS";
    private static final String ARBEIDSGIVER3_NAVN = "Arbeidsgiver3 AS";
    private static final int BRUTTO_BERENINGSGRUNNLAG = 720000;
    private static final int GRUNNBELØP = 100000;
    private static final Long DAGSATS_PERIODE1 = 500L;
    private static final Long DAGSATS_PERIODE2 = 1000L;
    private static final Long DAGSATS_PERIODE3 = 0L;
    private static final Long NATURALYTELSE_TILKOMMET = 100L;
    private static final Long NATURALYTELSE_BORTFALT = 50L;
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);
    private static final int KLAGEFRIST = 6;
    private static final BigDecimal UTBETALINGSGRAD_PERIODE1 = BigDecimal.valueOf(50);
    private static final BigDecimal UTBETALINGSGRAD_PERIODE2 = BigDecimal.valueOf(100);
    private static final BigDecimal UTBETALINGSGRAD_PERIODE3 = BigDecimal.ZERO;

    @Mock
    private ArbeidsgiverTjeneste arbeidsgiverTjeneste = mock(ArbeidsgiverTjeneste.class);

    private SvangerskapspengerInnvilgelseDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentdataMapper = new SvangerskapspengerInnvilgelseDokumentdataMapper(brevParametere, arbeidsgiverTjeneste);

        when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(ARBEIDSGIVER1_ORGNR)).thenReturn(ARBEIDSGIVER1_NAVN);
        when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(ARBEIDSGIVER2_ORGNR)).thenReturn(ARBEIDSGIVER2_NAVN);
        when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(ARBEIDSGIVER3_ORGNR)).thenReturn(ARBEIDSGIVER3_NAVN);
    }

    @Test
    void skal_mappe_felter_for_førstegangsbehandling() {
        // Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD).tilkjentYtelse(
                tilkjentYtelse().dagytelse(opprettTilkjentYtelse()).build())
            .svangerskapspenger(opprettSvpUttaksresultat())
            .søknadMottattDato(SØKNAD_DATO)
            .beregningsgrunnlag(opprettBeregningsgrunnlag())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.SVP);
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getRevurdering()).isFalse();
        assertThat(dokumentdata.getRefusjonTilBruker()).isTrue();
        assertThat(dokumentdata.getAntallRefusjonerTilArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getStønadsperiodeTom()).isEqualTo(formaterDatoNorsk(PERIODE2_TOM));
        assertThat(dokumentdata.getMånedsbeløp()).isEqualTo(DAGSATS_PERIODE1 * 260 / 12);
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
    }

    private BrevGrunnlagBuilder opprettBehandling(BrevGrunnlagDto.BehandlingType behandlingType) {
        return brevGrunnlag().saksnummer(SAKSNUMMER)
            .behandlingType(behandlingType)
            .behandlingsresultat(
                behandlingsresultat().behandlingResultatType(BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.INNVILGET).build())
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.SVANGERSKAPSPENGER)
            .språkkode(BrevGrunnlagDto.Språkkode.BOKMÅL);
    }

    private BeregningsgrunnlagDto opprettBeregningsgrunnlag() {
        var periode1 = lagBeregningsgrunnlagPeriode1();
        var periode2 = lagBeregningsgrunnlagPeriode2();
        var periode3 = lagBeregningsgrunnlagPeriode3();

        return new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), null, BigDecimal.valueOf(GRUNNBELØP),
            List.of(periode1, periode2, periode3), false, false);
    }

    private BeregningsgrunnlagPeriodeDto lagBeregningsgrunnlagPeriode1() {
        var andel = new BeregningsgrunnlagAndelDto(DAGSATS_PERIODE1, AktivitetStatusDto.ARBEIDSTAKER, BigDecimal.valueOf(BRUTTO_BERENINGSGRUNNLAG),
            null, false, OpptjeningAktivitetDto.ARBEID, PERIODE1_FOM, PERIODE1_TOM,
            new BgAndelArbeidsforholdDto(ARBEIDSGIVER1_ORGNR, null, BigDecimal.valueOf(NATURALYTELSE_TILKOMMET), BigDecimal.ZERO), false);

        return new BeregningsgrunnlagPeriodeDto(DAGSATS_PERIODE1, BigDecimal.valueOf(BRUTTO_BERENINGSGRUNNLAG), null, List.of(), PERIODE1_FOM,
            PERIODE1_TOM, List.of(andel));
    }

    private BeregningsgrunnlagPeriodeDto lagBeregningsgrunnlagPeriode2() {
        var andel = new BeregningsgrunnlagAndelDto(DAGSATS_PERIODE2, AktivitetStatusDto.ARBEIDSTAKER, BigDecimal.valueOf(BRUTTO_BERENINGSGRUNNLAG),
            null, false, OpptjeningAktivitetDto.ARBEID, PERIODE2_FOM, PERIODE2_TOM,
            new BgAndelArbeidsforholdDto(ARBEIDSGIVER1_ORGNR, null, BigDecimal.valueOf(NATURALYTELSE_BORTFALT), BigDecimal.ZERO), false);

        return new BeregningsgrunnlagPeriodeDto(DAGSATS_PERIODE2, BigDecimal.valueOf(BRUTTO_BERENINGSGRUNNLAG), null, List.of(), PERIODE2_FOM,
            PERIODE2_TOM, List.of(andel));
    }

    private BeregningsgrunnlagPeriodeDto lagBeregningsgrunnlagPeriode3() {
        var andel = new BeregningsgrunnlagAndelDto(DAGSATS_PERIODE3, AktivitetStatusDto.ARBEIDSTAKER, BigDecimal.valueOf(BRUTTO_BERENINGSGRUNNLAG),
            null, false, OpptjeningAktivitetDto.ARBEID, PERIODE3_FOM, PERIODE3_TOM,
            new BgAndelArbeidsforholdDto(ARBEIDSGIVER1_ORGNR, null, BigDecimal.ZERO, BigDecimal.ZERO), false);

        return new BeregningsgrunnlagPeriodeDto(DAGSATS_PERIODE3, BigDecimal.valueOf(BRUTTO_BERENINGSGRUNNLAG), null, List.of(), PERIODE3_FOM,
            PERIODE3_TOM, List.of(andel));
    }

    private TilkjentYtelseDagytelseDto opprettTilkjentYtelse() {
        var periode1 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE1_FOM, PERIODE1_TOM, DAGSATS_PERIODE1.intValue(), List.of(
            new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER1_ORGNR, DAGSATS_PERIODE1.intValue(),
                UTBETALINGSGRAD_PERIODE1.intValue(), TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, null, BigDecimal.TEN)));

        var periode2 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE2_FOM, PERIODE2_TOM, DAGSATS_PERIODE2.intValue(), List.of(
            new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER1_ORGNR, DAGSATS_PERIODE2.intValue(),
                UTBETALINGSGRAD_PERIODE2.intValue(), TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, null, BigDecimal.TEN)));

        var periode3 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE3_FOM, PERIODE3_TOM, DAGSATS_PERIODE3.intValue(), List.of(
            new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(ARBEIDSGIVER1_ORGNR, DAGSATS_PERIODE3.intValue(),
                UTBETALINGSGRAD_PERIODE3.intValue(), TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null, null, BigDecimal.TEN)));

        return new TilkjentYtelseDagytelseDto(List.of(periode1, periode2, periode3));
    }

    private BrevGrunnlagDto.Svangerskapspenger opprettSvpUttaksresultat() {
        var periode1 = svangerskapspengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE1_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.INNVILGET)
            .build();

        var periode2 = svangerskapspengerUttakPeriode().fom(PERIODE2_FOM)
            .tom(PERIODE2_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.INNVILGET)
            .build();

        var periode3 = svangerskapspengerUttakPeriode().fom(PERIODE3_FOM)
            .tom(PERIODE3_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE.getKode())
            .build();

        var arbeidsforhold1 = svangerskapspengerUttakArbeidsforhold().arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .arbeidsgiverReferanse(ARBEIDSGIVER1_ORGNR)
            .perioder(List.of(periode1, periode2))
            .build();

        var arbeidsforhold2 = svangerskapspengerUttakArbeidsforhold().arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .arbeidsgiverReferanse(ARBEIDSGIVER2_ORGNR)
            .perioder(List.of(periode3))
            .build();

        return svangerskapspenger().uttakArbeidsforhold(List.of(arbeidsforhold1, arbeidsforhold2)).build();
    }
}
