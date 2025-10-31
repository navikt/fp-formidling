package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.familieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepengerUttakAktivitet;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.fritekst;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.tilkjentYtelse;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

@ExtendWith(MockitoExtension.class)
class ForeldrepengerAvslagDokumentdataMapperTest {

    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(4);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(7);
    private static final BigDecimal TREKKDAGER = BigDecimal.valueOf(10.5);
    private static final BigDecimal TREKKDAGER_FORRVENTET = BigDecimal.TEN;

    private ForeldrepengerAvslagDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentdataMapper = new ForeldrepengerAvslagDokumentdataMapper(brevParametere);
    }

    @Test
    void skal_mappe_felter_for_brev() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

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

        assertThat(dokumentdata.getRelasjonskode()).isEqualTo("MOR");
        assertThat(dokumentdata.getRelasjonsRolleType()).isEqualTo(RelasjonsRolleType.MORA);
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(dokumentdata.getGjelderFødsel()).isTrue();
        assertThat(dokumentdata.getBarnErFødt()).isFalse();
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(1);
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getGjelderMor()).isTrue();
        assertThat(dokumentdata.getLovhjemmelForAvslag()).isEqualTo("forvaltningsloven § 35");
        assertThat(dokumentdata.getAvslåttePerioder()).hasSize(2); // Periode 2 og 3 skal slås sammen
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAvslagsårsak().getKode()).isEqualTo("4007");
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue());
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAvslagsårsak().getKode()).isEqualTo("4005");
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getPeriodeTom()).isEqualTo(PERIODE3_TOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue() * 2);
    }

    @Test
    void SjekkAtTotilkjentPerioderMedEnUttaksperiodeFårRiktigTapteDager() {
        // Arrange
        var behandling = opprettBehandling2();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        assertThat(dokumentdata.getAvslåttePerioder()).hasSize(2);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue());
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue());
    }

    private BrevGrunnlagDto opprettBehandling() {
        var fh = familieHendelse().barn(List.of()).termindato(LocalDate.now()).antallBarn(1).build();

        var fritekstData = fritekst().brødtekst(FRITEKST).build();
        var behandlingsres = behandlingsresultat().behandlingResultatType(BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.AVSLÅTT)
            .konsekvenserForYtelsen(List.of(BrevGrunnlagDto.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING,
                BrevGrunnlagDto.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK))
            .fritekst(fritekstData)
            .build();

        var beregningsgrunnlag = opprettBeregningsgrunnlag();
        var tilkjentYtelse = opprettTilkjentYtelse();
        var fpUttak = opprettUttaksresultat();

        return defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .behandlingsresultat(behandlingsres)
            .relasjonsRolleType(BrevGrunnlagDto.RelasjonsRolleType.MORA)
            .familieHendelse(fh)
            .førsteSøknadMottattDato(SØKNAD_DATO)
            .beregningsgrunnlag(beregningsgrunnlag)
            .tilkjentYtelse(tilkjentYtelse)
            .foreldrepenger(fpUttak)
            .build();
    }

    private BrevGrunnlagDto opprettBehandling2() {
        var fh = familieHendelse().barn(List.of()).termindato(LocalDate.now()).antallBarn(1).build();

        var fritekstData = fritekst().brødtekst(FRITEKST).build();
        var behandlingsres = behandlingsresultat().behandlingResultatType(BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.AVSLÅTT)
            .konsekvenserForYtelsen(List.of(BrevGrunnlagDto.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING,
                BrevGrunnlagDto.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK))
            .fritekst(fritekstData)
            .build();

        var beregningsgrunnlag = opprettBeregningsgrunnlag();
        var tilkjentYtelse = opprettTilkjentYtelse();
        var fpUttak = opprettUttaksresultat2();

        return defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .behandlingsresultat(behandlingsres)
            .relasjonsRolleType(BrevGrunnlagDto.RelasjonsRolleType.MORA)
            .familieHendelse(fh)
            .førsteSøknadMottattDato(SØKNAD_DATO)
            .beregningsgrunnlag(beregningsgrunnlag)
            .tilkjentYtelse(tilkjentYtelse)
            .foreldrepenger(fpUttak)
            .build();
    }

    private BeregningsgrunnlagDto opprettBeregningsgrunnlag() {
        var periode = new BeregningsgrunnlagPeriodeDto((long) GRUNNBELØP, BigDecimal.valueOf(GRUNNBELØP), BigDecimal.valueOf(GRUNNBELØP), null, null,
            null, null);
        return new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), HjemmelDto.F_14_7, BigDecimal.valueOf(GRUNNBELØP),
            List.of(periode), true, false);
    }

    private BrevGrunnlagDto.TilkjentYtelse opprettTilkjentYtelse() {
        var dagytelse = new TilkjentYtelseDagytelseDto(
            List.of(new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE1_FOM, PERIODE1_TOM, 100, List.of()),
                new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE2_FOM, PERIODE2_TOM, 200, List.of()),
                new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE3_FOM, PERIODE3_TOM, 300, List.of())));

        return tilkjentYtelse().dagytelse(dagytelse).build();
    }

    private BrevGrunnlagDto.Foreldrepenger opprettUttaksresultat() {
        var aktivitet = foreldrepengerUttakAktivitet().trekkdager(TREKKDAGER)
            .utbetalingsgrad(BigDecimal.ZERO)
            .prosentArbeid(BigDecimal.valueOf(100))
            .trekkontoType(BrevGrunnlagDto.Foreldrepenger.TrekkontoType.FORELDREPENGER)
            .build();

        var periode1 = foreldrepengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE1_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, "4007")
            .aktiviteter(List.of(aktivitet))
            .build();

        var periode2 = foreldrepengerUttakPeriode().fom(PERIODE2_FOM)
            .tom(PERIODE2_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, "4005")
            .aktiviteter(List.of(aktivitet))
            .build();

        var periode3 = foreldrepengerUttakPeriode().fom(PERIODE3_FOM)
            .tom(PERIODE3_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, "4005")
            .aktiviteter(List.of(aktivitet))
            .build();

        return foreldrepenger().perioderSøker(List.of(periode1, periode2, periode3)).build();
    }

    private BrevGrunnlagDto.Foreldrepenger opprettUttaksresultat2() {
        var aktivitet = foreldrepengerUttakAktivitet().trekkdager(TREKKDAGER)
            .utbetalingsgrad(BigDecimal.ZERO)
            .prosentArbeid(BigDecimal.valueOf(100))
            .trekkontoType(BrevGrunnlagDto.Foreldrepenger.TrekkontoType.FORELDREPENGER)
            .build();

        var periode1 = foreldrepengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE2_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, "4007")
            .aktiviteter(List.of(aktivitet))
            .build();

        var periode3 = foreldrepengerUttakPeriode().fom(PERIODE3_FOM)
            .tom(PERIODE3_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, "4005")
            .aktiviteter(List.of(aktivitet))
            .build();

        return foreldrepenger().perioderSøker(List.of(periode1, periode3)).build();
    }
}
