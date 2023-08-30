package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksaktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.UttakSvpDtoMapper;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

class UttaksperiodeMapperTest {

    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().minusDays(5);
    private static final LocalDate PERIODE2_FOM = LocalDate.now();
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(11);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(14);
    private static final LocalDate PERIODE4_FOM = LocalDate.now().plusDays(15);
    private static final LocalDate PERIODE4_TOM = LocalDate.now().plusDays(16);
    private static final String ARBEIDSGIVER_NAVN = "Arbeidsgiver AS";
    private static final String FORVENTET_FRILANSER_TEKST = "Som " + UttakSvpDtoMapper.FRILANSER;
    private static final String FORVENTET_NÆRINGSDRIVENDE_TEKST = "Som " + UttakSvpDtoMapper.NÆRINGSDRIVENDE;
    private static final Arbeidsgiver ARBEIDSGIVER = new Arbeidsgiver("1", ARBEIDSGIVER_NAVN);
    private static final long DAGSATS = 1000L;
    private static final BigDecimal UTBETALINGSGRAD_100 = BigDecimal.valueOf(100);
    private static final BigDecimal UTBETALINGSGRAD_50 = BigDecimal.valueOf(50);
    private static final BigDecimal UTBETALINGSGRAD_10 = BigDecimal.valueOf(10);

    @Test
    void skal_mappe_og_slå_sammen_sammenhengende_perioder_med_samme_utbetalingsgrad_innenfor_samme_aktivitetstype() {
        // Arrange
        var svangerskapspengerUttak = getSvpUttaksresultat();
        var tilkjentYtelseFP = getTilkjentYtelse();

        // Act
        var resultat = UttaksperiodeMapper.mapUttaksaktivteterMedPerioder(svangerskapspengerUttak, tilkjentYtelseFP, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(3);
        assertThat(resultat.stream().map(Uttaksaktivitet::getAktivitetsbeskrivelse).toList()).containsExactlyInAnyOrder(ARBEIDSGIVER_NAVN,
            FORVENTET_FRILANSER_TEKST, FORVENTET_NÆRINGSDRIVENDE_TEKST);

        for (var uttaksaktivitet : resultat) {
            var uttaksperioder = uttaksaktivitet.getUttaksperioder();
            if (ARBEIDSGIVER_NAVN.equals(uttaksaktivitet.getAktivitetsbeskrivelse())) {
                assertThat(uttaksperioder).hasSize(3);
                assertThat(uttaksperioder.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
                assertThat(uttaksperioder.get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
                assertThat(uttaksperioder.get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(UTBETALINGSGRAD_100));
                assertThat(uttaksperioder.get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
                assertThat(uttaksperioder.get(1).getPeriodeTom()).isEqualTo(PERIODE3_TOM);
                assertThat(uttaksperioder.get(1).getUtbetalingsgrad()).isEqualTo(Prosent.of(UTBETALINGSGRAD_100));
                assertThat(uttaksperioder.get(2).getPeriodeFom()).isEqualTo(PERIODE4_FOM);
                assertThat(uttaksperioder.get(2).getPeriodeTom()).isEqualTo(PERIODE4_TOM);
                assertThat(uttaksperioder.get(2).getUtbetalingsgrad()).isEqualTo(Prosent.of(UTBETALINGSGRAD_50));
            } else if (FORVENTET_FRILANSER_TEKST.equals(uttaksaktivitet.getAktivitetsbeskrivelse())) {
                assertThat(uttaksperioder).hasSize(1);
                assertThat(uttaksperioder.get(0).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
                assertThat(uttaksperioder.get(0).getPeriodeTom()).isEqualTo(PERIODE4_TOM);
                assertThat(uttaksperioder.get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(UTBETALINGSGRAD_10));
            } else if (FORVENTET_NÆRINGSDRIVENDE_TEKST.equals(uttaksaktivitet.getAktivitetsbeskrivelse())) {
                assertThat(uttaksperioder).hasSize(1);
                assertThat(uttaksperioder.get(0).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
                assertThat(uttaksperioder.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
                assertThat(uttaksperioder.get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(UTBETALINGSGRAD_10));
            }
        }
    }

    private SvangerskapspengerUttak getSvpUttaksresultat() {
        // Arbeidstaker
        var uttakPeriode1 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_100)
            .build();
        var uttakPeriode2 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_100)
            .build();
        var uttakPeriode3 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_100)
            .build();
        var uttakPeriode4 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_50)
            .build();

        // Frilanser
        var uttakPeriode5 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medArbeidsgiverNavn(UttakSvpDtoMapper.FRILANSER)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_10)
            .build();
        var uttakPeriode6 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medArbeidsgiverNavn(UttakSvpDtoMapper.FRILANSER)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_10)
            .build();
        var uttakPeriode7 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
            .medArbeidsgiverNavn(UttakSvpDtoMapper.FRILANSER)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_10)
            .build();

        // Næringsdrivende
        var uttakPeriode8 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medArbeidsgiverNavn(UttakSvpDtoMapper.NÆRINGSDRIVENDE)
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medUtbetalingsgrad(UTBETALINGSGRAD_10)
            .build();

        var svpUttakResultatArbeidsforhold = SvpUttakResultatArbeidsforhold.Builder.ny()
            .leggTilPerioder(
                of(uttakPeriode1, uttakPeriode2, uttakPeriode3, uttakPeriode4, uttakPeriode5, uttakPeriode6, uttakPeriode7, uttakPeriode8))
            .build();
        return SvangerskapspengerUttak.Builder.ny().leggTilUttakResultatArbeidsforhold(svpUttakResultatArbeidsforhold).build();
    }

    private TilkjentYtelseForeldrepenger getTilkjentYtelse() {
        // Arbeidstaker
        var arbeidsgiverAndel = TilkjentYtelseAndel.ny().medArbeidsgiver(ARBEIDSGIVER).medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER).build();
        var resultatPeriode1 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(arbeidsgiverAndel))
            .build();
        var resultatPeriode2 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(arbeidsgiverAndel))
            .build();
        var resultatPeriode3 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(arbeidsgiverAndel))
            .build();
        var resultatPeriode4 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(arbeidsgiverAndel))
            .build();

        // Frilanser
        var frilanserAndel = TilkjentYtelseAndel.ny().medAktivitetStatus(AktivitetStatus.FRILANSER).build();
        var resultatPeriode5 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(frilanserAndel))
            .build();
        var resultatPeriode6 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(frilanserAndel))
            .build();
        var resultatPeriode7 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(frilanserAndel))
            .build();

        // Næringsdrivende
        var næringsdrivendeAndel = TilkjentYtelseAndel.ny().medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE).build();
        var resultatPeriode8 = TilkjentYtelsePeriode.ny()
            .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medDagsats(DAGSATS)
            .medAndeler(of(næringsdrivendeAndel))
            .build();

        // Periode 1 legges til to ganger for å simulere caset der samme arbeidsgiver har to oppføringer med lik FOM/TOM
        return TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(
                of(resultatPeriode1, resultatPeriode1, resultatPeriode2, resultatPeriode3, resultatPeriode4, resultatPeriode5, resultatPeriode6,
                    resultatPeriode7, resultatPeriode8))
            .build();
    }
}
