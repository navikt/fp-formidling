package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.fpformidling.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.fpformidling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksaktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksperiode;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpsak.mapper.UttakSvpDtoMapper;

public class UttaksperiodeMapperTest {

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
    private static final int UTBETALINGSGRAD_100 = 100;
    private static final int UTBETALINGSGRAD_50 = 50;
    private static final int UTBETALINGSGRAD_10 = 10;

    @Test
    public void skal_mappe_og_slå_sammen_sammenhengende_perioder_med_samme_utbetalingsgrad_innenfor_samme_aktivitetstype() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat();
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultat();

        // Act
        List<Uttaksaktivitet> resultat = UttaksperiodeMapper.mapUttaksaktivteterMedPerioder(svpUttaksresultat, beregningsresultatFP, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(3);
        assertThat(resultat.stream().map(Uttaksaktivitet::getAktivitetsbeskrivelse).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(ARBEIDSGIVER_NAVN, FORVENTET_FRILANSER_TEKST, FORVENTET_NÆRINGSDRIVENDE_TEKST);

        for (Uttaksaktivitet uttaksaktivitet : resultat) {
            List<Uttaksperiode> uttaksperioder = uttaksaktivitet.getUttaksperioder();
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

    private SvpUttaksresultat getSvpUttaksresultat() {
        // Arbeidstaker
        SvpUttakResultatPeriode uttakPeriode1 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_100)
                .build();
        SvpUttakResultatPeriode uttakPeriode2 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_100)
                .build();
        SvpUttakResultatPeriode uttakPeriode3 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_100)
                .build();
        SvpUttakResultatPeriode uttakPeriode4 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_50)
                .build();

        // Frilanser
        SvpUttakResultatPeriode uttakPeriode5 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medArbeidsgiverNavn(UttakSvpDtoMapper.FRILANSER)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_10)
                .build();
        SvpUttakResultatPeriode uttakPeriode6 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .medArbeidsgiverNavn(UttakSvpDtoMapper.FRILANSER)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_10)
                .build();
        SvpUttakResultatPeriode uttakPeriode7 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
                .medArbeidsgiverNavn(UttakSvpDtoMapper.FRILANSER)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_10)
                .build();

        // Næringsdrivende
        SvpUttakResultatPeriode uttakPeriode8 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medArbeidsgiverNavn(UttakSvpDtoMapper.NÆRINGSDRIVENDE)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(UTBETALINGSGRAD_10)
                .build();

        SvpUttakResultatArbeidsforhold svpUttakResultatArbeidsforhold = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(of(uttakPeriode1, uttakPeriode2, uttakPeriode3, uttakPeriode4, uttakPeriode5, uttakPeriode6,
                        uttakPeriode7, uttakPeriode8))
                .build();
        return SvpUttaksresultat.Builder.ny()
                .leggTilUttakResultatArbeidsforhold(svpUttakResultatArbeidsforhold)
                .build();
    }

    private BeregningsresultatFP getBeregningsresultat() {
        // Arbeidstaker
        BeregningsresultatAndel arbeidsgiverAndel = BeregningsresultatAndel.ny()
                .medArbeidsgiver(ARBEIDSGIVER)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .build();
        BeregningsresultatPeriode resultatPeriode1 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(arbeidsgiverAndel))
                .build();
        BeregningsresultatPeriode resultatPeriode2 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(arbeidsgiverAndel))
                .build();
        BeregningsresultatPeriode resultatPeriode3 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(arbeidsgiverAndel))
                .build();
        BeregningsresultatPeriode resultatPeriode4 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(arbeidsgiverAndel))
                .build();

        // Frilanser
        BeregningsresultatAndel frilanserAndel = BeregningsresultatAndel.ny()
                .medAktivitetStatus(AktivitetStatus.FRILANSER)
                .build();
        BeregningsresultatPeriode resultatPeriode5 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(frilanserAndel))
                .build();
        BeregningsresultatPeriode resultatPeriode6 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(frilanserAndel))
                .build();
        BeregningsresultatPeriode resultatPeriode7 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(frilanserAndel))
                .build();

        // Næringsdrivende
        BeregningsresultatAndel næringsdrivendeAndel = BeregningsresultatAndel.ny()
                .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                .build();
        BeregningsresultatPeriode resultatPeriode8 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(næringsdrivendeAndel))
                .build();

        // Periode 1 legges til to ganger for å simulere caset der samme arbeidsgiver har to oppføringer med lik FOM/TOM
        return BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(resultatPeriode1, resultatPeriode1, resultatPeriode2, resultatPeriode3,
                        resultatPeriode4, resultatPeriode5, resultatPeriode6, resultatPeriode7, resultatPeriode8))
                .build();
    }
}