package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningMapperTest {

    private static final String ARBEIDSGIVER_1 = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER_2 = "Arbeidsgiver2 AS";
    private static final int BRUTTO_ÅR_ARBEIDSFORHOLD1 = 120000;
    private static final int BRUTTO_ÅR_ARBEIDSFORHOLD2 = 310100;

    @Test
    public void skal_mappe_arbeidsforhold_med_høyest_inntekt_først() {
        // Arrange
        Arbeidsgiver arbeidsgiver1 = new Arbeidsgiver("1", ARBEIDSGIVER_1);
        BGAndelArbeidsforhold bgAndelArbeidsforhold1 = new BGAndelArbeidsforhold(arbeidsgiver1, ArbeidsforholdRef.ref("1"),
                BigDecimal.ZERO, BigDecimal.ZERO);
        Arbeidsgiver arbeidsgiver2 = new Arbeidsgiver("1", ARBEIDSGIVER_2);
        BGAndelArbeidsforhold bgAndelArbeidsforhold2 = new BGAndelArbeidsforhold(arbeidsgiver2, ArbeidsforholdRef.ref("1"),
                BigDecimal.ZERO, BigDecimal.ZERO);
        List<BeregningsgrunnlagPrStatusOgAndel> andel = of(
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
        BeregningsgrunnlagPeriode beregningsgrunnlagPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(fraOgMedTilOgMed(LocalDate.now().minusDays(20), LocalDate.now().plusDays(20)))
                .medBeregningsgrunnlagPrStatusOgAndelList(andel)
                .build();
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .leggTilBeregningsgrunnlagPeriode(beregningsgrunnlagPeriode)
                .build();

        // Act
        List<Arbeidsforhold> resultat = BeregningMapper.mapArbeidsforhold(beregningsgrunnlag);

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_2);
        assertThat(resultat.get(0).getMånedsinntekt()).isEqualTo(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        assertThat(resultat.get(1).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_1);
        assertThat(resultat.get(1).getMånedsinntekt()).isEqualTo(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
    }

}