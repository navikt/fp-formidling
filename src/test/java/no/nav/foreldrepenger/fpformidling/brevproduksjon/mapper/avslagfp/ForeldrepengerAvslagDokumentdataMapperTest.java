package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
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
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;

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
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;

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
    private static final PeriodeResultatÅrsak ÅRSAK_1 = PeriodeResultatÅrsak.FOR_SEN_SØKNAD;
    private static final PeriodeResultatÅrsak ÅRSAK_2_OG_3 = PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG;
    private static final BigDecimal TREKKDAGER = BigDecimal.valueOf(10.5);
    private static final BigDecimal TREKKDAGER_FORRVENTET = BigDecimal.TEN;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private ForeldrepengerAvslagDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.FORELDREPENGER_AVSLAG);
        dokumentdataMapper = new ForeldrepengerAvslagDokumentdataMapper(brevParametere, domeneobjektProvider);

        when(domeneobjektProvider.hentFagsakBackend(any(Behandling.class))).thenReturn(opprettFagsakBackend());
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(opprettMottattDokument());
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentTilkjentYtelseFPHvisFinnes(any(Behandling.class))).thenReturn(opprettTilkjentYtelseFP());
        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        lenient().when(domeneobjektProvider.hentForeldrepengerUttakHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat());
    }

    @Test
    void skal_mappe_felter_for_brev() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

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

        assertThat(dokumentdata.getRelasjonskode()).isEqualTo("MOR");
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(dokumentdata.getGjelderFødsel()).isTrue();
        assertThat(dokumentdata.getBarnErFødt()).isFalse();
        assertThat(dokumentdata.getAnnenForelderHarRett()).isTrue();
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(2);
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getKreverSammenhengendeUttak()).isFalse();
        assertThat(dokumentdata.getGjelderMor()).isTrue();
        assertThat(dokumentdata.getLovhjemmelForAvslag()).isEqualTo("forvaltningsloven § 35");
        assertThat(dokumentdata.getAvslåttePerioder()).hasSize(2); // Periode 2 og 3 skal slås sammen
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAvslagsårsak().getKode()).isEqualTo(ÅRSAK_1.getKode());
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue());
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAvslagsårsak().getKode()).isEqualTo(ÅRSAK_2_OG_3.getKode());
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getPeriodeTom()).isEqualTo(PERIODE3_TOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue() * 2);
    }

    @Test
    void SjekkAtTotilkjentPerioderMedEnUttaksperiodeFårRiktigTapteDager() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        when(domeneobjektProvider.hentForeldrepengerUttakHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat2());

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        assertThat(dokumentdata.getAvslåttePerioder()).hasSize(2);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue());
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAntallTapteDager()).isEqualTo(TREKKDAGER_FORRVENTET.intValue());

    }

    private List<MottattDokument> opprettMottattDokument() {
        return of(new MottattDokument(SØKNAD_DATO, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD));
    }

    private FagsakBackend opprettFagsakBackend() {
        return FagsakBackend.ny().medBrukerRolle(RelasjonsRolleType.MORA).build();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        return new FamilieHendelse(FamilieHendelseType.TERMIN, 2, 0, null, LocalDate.now(), null, null, false, true);
    }

    private Optional<TilkjentYtelseForeldrepenger> opprettTilkjentYtelseFP() {
        return Optional.of(TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny().medDagsats(100L).medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM)).build(),
                TilkjentYtelsePeriode.ny().medDagsats(100L * 2).medPeriode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM)).build(),
                TilkjentYtelsePeriode.ny().medDagsats(100L * 3).medPeriode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM)).build()))
            .build());
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
            .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
            .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
            .medhHjemmel(Hjemmel.F_14_7)
            .medBesteberegnet(true)
            .build());
    }

    private Optional<ForeldrepengerUttak> opprettUttaksresultat() {
        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
            .medTrekkdager(TREKKDAGER)
            .medUtbetalingsprosent(BigDecimal.ZERO)
            .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
            .medArbeidsprosent(BigDecimal.valueOf(100))
            .build();
        var uttakResultatPeriode1 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(ÅRSAK_1)
            .build();
        var uttakResultatPeriode2 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(ÅRSAK_2_OG_3)
            .build();
        var uttakResultatPeriode3 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(ÅRSAK_2_OG_3)
            .build();
        return Optional.of(
            new ForeldrepengerUttak(of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3), List.of(), false, true, false, false));
    }

    private Optional<ForeldrepengerUttak> opprettUttaksresultat2() {
        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
            .medTrekkdager(TREKKDAGER)
            .medUtbetalingsprosent(BigDecimal.ZERO)
            .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
            .medArbeidsprosent(BigDecimal.valueOf(100))
            .build();
        var uttakResultatPeriode1 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE2_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(ÅRSAK_1)
            .build();
        var uttakResultatPeriode3 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(ÅRSAK_2_OG_3)
            .build();
        return Optional.of(new ForeldrepengerUttak(of(uttakResultatPeriode1, uttakResultatPeriode3), List.of(), false, true, false, false));
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.REVURDERING)
            .medBehandlingsresultat(Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                .medKonsekvenserForYtelsen(of(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK))
                .build())
            .medFagsakBackend(FagsakBackend.ny().medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER).build())
            .medSpråkkode(Språkkode.NB)
            .build();
    }
}
