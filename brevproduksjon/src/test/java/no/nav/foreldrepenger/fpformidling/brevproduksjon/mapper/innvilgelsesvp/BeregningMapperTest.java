package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.BeregningMapper.utledLovhjemmelForBeregning;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class BeregningMapperTest {

    private static final String ARBEIDSGIVER_1 = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER_2 = "Arbeidsgiver2 AS";
    private static final int BRUTTO_ÅR_ARBEIDSFORHOLD1 = 120000;
    private static final int BRUTTO_ÅR_ARBEIDSFORHOLD2 = 310100;

    @Test
    public void skal_mappe_arbeidsforhold_med_høyest_inntekt_først() {
        // Arrange
        var arbeidsgiver1 = new Arbeidsgiver("1", ARBEIDSGIVER_1);
        var bgAndelArbeidsforhold1 = new BGAndelArbeidsforhold(arbeidsgiver1, ArbeidsforholdRef.ref("1"),
                BigDecimal.ZERO, BigDecimal.ZERO);
        var arbeidsgiver2 = new Arbeidsgiver("1", ARBEIDSGIVER_2);
        var bgAndelArbeidsforhold2 = new BGAndelArbeidsforhold(arbeidsgiver2, ArbeidsforholdRef.ref("1"),
                BigDecimal.ZERO, BigDecimal.ZERO);
        var andel = of(
                BeregningsgrunnlagPrStatusOgAndel.ny()
                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                        .medBgAndelArbeidsforhold(bgAndelArbeidsforhold1)
                        .medBruttoPrÅr(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1))
                        .build(),
                BeregningsgrunnlagPrStatusOgAndel.ny() // Skal sorteres først
                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                        .medBgAndelArbeidsforhold(bgAndelArbeidsforhold2)
                        .medBruttoPrÅr(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2))
                        .build(),
                BeregningsgrunnlagPrStatusOgAndel.ny() // Ignoreres
                        .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                        .build()
        );
        var beregningsgrunnlagPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(LocalDate.now().minusDays(20), LocalDate.now().plusDays(20)))
                .medBeregningsgrunnlagPrStatusOgAndelList(andel)
                .build();
        var beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .leggTilBeregningsgrunnlagPeriode(beregningsgrunnlagPeriode)
                .build();

        // Act
        var resultat = BeregningMapper.mapArbeidsforhold(beregningsgrunnlag);

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_2);
        assertThat(resultat.get(0).getMånedsinntekt()).isEqualTo(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        assertThat(resultat.get(1).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_1);
        assertThat(resultat.get(1).getMånedsinntekt()).isEqualTo(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
    }


    @Test
    public void er_militær() {
        // Arrange
        var andel = of(
                BeregningsgrunnlagPrStatusOgAndel.ny()
                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                        .medBruttoPrÅr(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1))
                        .medDagsats(500L)
                        .build(),
                BeregningsgrunnlagPrStatusOgAndel.ny() // Skal sorteres først
                        .medAktivitetStatus(AktivitetStatus.MILITÆR_ELLER_SIVIL)
                        .medBruttoPrÅr(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2))
                        .medDagsats(1000L)
                        .build());

        var beregningsgrunnlagPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(LocalDate.now().minusDays(20), LocalDate.now().plusDays(20)))
                .medBeregningsgrunnlagPrStatusOgAndelList(andel)
                .build();
        var beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .leggTilBeregningsgrunnlagPeriode(beregningsgrunnlagPeriode)
                .build();
        // Act & Assert
        assertThat(BeregningMapper.erMilitærSivil(beregningsgrunnlag)).isTrue();
    }

    @Test
    public void er_ikke_militær() {
        // Arrange
        var andel = of(
                BeregningsgrunnlagPrStatusOgAndel.ny()
                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                        .medBruttoPrÅr(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1))
                        .medDagsats(500L)
                        .build(),
                BeregningsgrunnlagPrStatusOgAndel.ny() // Skal sorteres først
                        .medAktivitetStatus(AktivitetStatus.MILITÆR_ELLER_SIVIL)
                        .medBruttoPrÅr(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2))
                        .medDagsats(0L)
                        .build());

        var beregningsgrunnlagPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(LocalDate.now().minusDays(20), LocalDate.now().plusDays(20)))
                .medBeregningsgrunnlagPrStatusOgAndelList(andel)
                .build();
        var beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .leggTilBeregningsgrunnlagPeriode(beregningsgrunnlagPeriode)
                .build();
        // Act & Assert
        assertThat(BeregningMapper.erMilitærSivil(beregningsgrunnlag)).isFalse();
    }

    @Test
    public void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7_og_8_30() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();
        var beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medhHjemmel(Hjemmel.F_14_7_8_30)
                .build();

        // Act
        var hjemmel = utledLovhjemmelForBeregning(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4 og 8-30");
    }

    @Test
    public void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7_og_8_49() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();
        var beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medhHjemmel(Hjemmel.F_14_7_8_49)
                .build();

        // Act
        var hjemmel = utledLovhjemmelForBeregning(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4 og 8-49");
    }

    @Test
    public void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        var behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();
        var beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medhHjemmel(Hjemmel.F_14_7)
                .build();

        // Act
        var hjemmel = utledLovhjemmelForBeregning(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§ 14-4");
    }
}
