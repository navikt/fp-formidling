package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.finnBrutto;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.finnSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.harBruktBruttoBeregningsgrunnlag;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.inntektOverSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.mapRegelListe;
import static org.assertj.core.api.Assertions.assertThat;

public class BeregningsgrunnlagMapperTest {

    private static final BigDecimal AVKORTET_PR_ÅR = BigDecimal.valueOf(542987.4);
    private static final BigDecimal FRILANSER_BRUTTO_PR_ÅR = BigDecimal.valueOf(95406.6);
    private static final BigDecimal ARBEIDSTAKER_BRUTTO_PR_ÅR = BigDecimal.valueOf(1000000);
    private static final BigDecimal GRUNNBELØP = BigDecimal.valueOf(50_000);
    private static final long STANDARD_PERIODE_DAGSATS = 100L;
    private static final long FRILANSER_DAGSATS = 200L;
    private static final long ARBEIDSTAKER_DAGSATS = 300L;
    private static final DatoIntervall BER_PERIODE = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 9, 1));

    @Test
    public void skal_finne_brutto() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode(lagBraListeFrilanser()))
                .build();

        // Act + Assert
        assertThat(finnBrutto(beregningsgrunnlag)).isEqualTo(AVKORTET_PR_ÅR.add(FRILANSER_BRUTTO_PR_ÅR).longValue());
    }

    @Test
    public void skal_finne_seksG() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medGrunnbeløp(new Beløp(GRUNNBELØP))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode(lagBraListeFrilanser()))
                .build();

        // Act + Assert
        assertThat(finnSeksG(beregningsgrunnlag)).isEqualTo(GRUNNBELØP.multiply(BigDecimal.valueOf(6)));
    }

    @Test
    public void skal_identifisere_brutto_over_6g() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medGrunnbeløp(new Beløp(GRUNNBELØP))
                .leggTilBeregningsgrunnlagPeriode(
                        BeregningsgrunnlagPeriode.ny()
                                .medBruttoPrÅr(GRUNNBELØP.multiply(BigDecimal.valueOf(6)).add(BigDecimal.ONE))
                                .build())
                .build();

        // Act + Assert
        assertThat(inntektOverSeksG(beregningsgrunnlag)).isTrue();
    }

    @Test
    public void skal_identifisere_ikke_brutto_over_6g() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medGrunnbeløp(new Beløp(GRUNNBELØP))
                .leggTilBeregningsgrunnlagPeriode(
                        BeregningsgrunnlagPeriode.ny()
                                .medBruttoPrÅr(GRUNNBELØP.multiply(BigDecimal.valueOf(6)))
                                .build())
                .build();

        // Act + Assert
        assertThat(inntektOverSeksG(beregningsgrunnlag)).isFalse();
    }

    @Test
    public void skal_mappe_regelListe() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.FRILANSER))
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode(lagBraListeFrilanser()))
                .build();

        // Act
        List<BeregningsgrunnlagRegel> regler = mapRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler).hasSize(2);

        assertThat(regler.get(0).getRegelStatus()).isEqualTo(AktivitetStatus.FRILANSER.name());
        assertThat(regler.get(0).getAndelListe()).hasSize(1);
        assertThat(regler.get(0).getAndelListe().get(0).getDagsats()).isEqualTo(FRILANSER_DAGSATS);
        assertThat(regler.get(0).getAndelListe().get(0).getMånedsinntekt()).isEqualTo(FRILANSER_BRUTTO_PR_ÅR.divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        assertThat(regler.get(0).getAndelListe().get(0).getÅrsinntekt()).isEqualTo(FRILANSER_BRUTTO_PR_ÅR.longValue());

        assertThat(regler.get(1).getRegelStatus()).isEqualTo(AktivitetStatus.ARBEIDSTAKER.name());
        assertThat(regler.get(1).getAndelListe()).hasSize(1);
        assertThat(regler.get(1).getAndelListe().get(0).getDagsats()).isEqualTo(ARBEIDSTAKER_DAGSATS);
        assertThat(regler.get(1).getAndelListe().get(0).getMånedsinntekt()).isEqualTo(ARBEIDSTAKER_BRUTTO_PR_ÅR.divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        assertThat(regler.get(1).getAndelListe().get(0).getÅrsinntekt()).isEqualTo(ARBEIDSTAKER_BRUTTO_PR_ÅR.longValue());
    }

    @Test
    public void skal_mappe_regelListe_for_dagpenger_med_tilkommet_arbforhold() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.DAGPENGER))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode(lagBraListeDPOgTilkommetArbforhold()))
                .build();

        // Act
        List<BeregningsgrunnlagRegel> regler = mapRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler).hasSize(1);
        assertThat(regler.get(0).getRegelStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.get(0).getAndelListe()).hasSize(1);
        assertThat(regler.get(0).getAndelListe().get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.get(0).getAndelListe().get(0).getDagsats()).isEqualTo(1002);

    }

    @Test
    public void skal_mappe_regelListe_med_for_dagpenger_uten_tilkommet_arbforhold() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.DAGPENGER))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode(lagBraListeDPogUtenTilkommetArbforhold()))
                .build();

        // Act
        List<BeregningsgrunnlagRegel> regler = mapRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler).hasSize(1);
        assertThat(regler.get(0).getRegelStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.get(0).getAndelListe()).hasSize(1);
        assertThat(regler.get(0).getAndelListe().get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.get(0).getAndelListe().get(0).getDagsats()).isEqualTo(1002);

    }

    @Test
    public void skal_mappe_sistLignedeÅr_når_selvstendig_næringsdrivende() {
        // Arrange
        Beregningsgrunnlag beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE))
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode( of(lagBgpsandel(BigDecimal.valueOf(254232), null, 978,  AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, false ),
                        lagBgpsandel(BigDecimal.valueOf(0), null, 24, AktivitetStatus.ARBEIDSTAKER, true ))))
                .build();

        // Act
        List<BeregningsgrunnlagRegel> regler = mapRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler.get(0).getAndelListe().get(0).getSistLignedeÅr()).isEqualTo(BER_PERIODE.getTomDato().getYear());
        assertThat(regler.get(1).getAndelListe().get(0).getSistLignedeÅr()).isEqualTo(0);
    }

    @Test
    public void skal_finne_at_brutto_beregningsgrunnlag_er_brukt_fordi_det_er_mer_enn_en_regel() {
        // Arrange
        BeregningsgrunnlagRegel regel1 = BeregningsgrunnlagRegel.ny()
                .medAktivitetStatus(AktivitetStatus.ARBEIDSAVKLARINGSPENGER.name())
                .build();
        BeregningsgrunnlagRegel regel2 = BeregningsgrunnlagRegel.ny()
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name())
                .build();

        // Act
        boolean resultat = harBruktBruttoBeregningsgrunnlag(of(regel1, regel2));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_finne_at_brutto_beregningsgrunnlag_er_brukt_fordi_det_er_kombinert_status() {
        // Arrange
        BeregningsgrunnlagRegel regel1 = BeregningsgrunnlagRegel.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
                .build();

        // Act
        boolean resultat = harBruktBruttoBeregningsgrunnlag(of(regel1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_finne_at_brutto_beregningsgrunnlag_ikke_er_brukt() {
        // Arrange
        BeregningsgrunnlagRegel regel1 = BeregningsgrunnlagRegel.ny()
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name())
                .build();

        // Act
        boolean resultat = harBruktBruttoBeregningsgrunnlag(of(regel1));

        // Assert
        assertThat(resultat).isFalse();
    }



    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode(List<BeregningsgrunnlagPrStatusOgAndel> andelsliste) {
        return BeregningsgrunnlagPeriode.ny()
                .medDagsats(STANDARD_PERIODE_DAGSATS)
                .medBruttoPrÅr(FRILANSER_BRUTTO_PR_ÅR)
                .medAvkortetPrÅr(AVKORTET_PR_ÅR)
                .medBeregningsgrunnlagPrStatusOgAndelList(andelsliste)
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBraListeFrilanser() {
        return of(lagBgpsandel(FRILANSER_BRUTTO_PR_ÅR, null, FRILANSER_DAGSATS, AktivitetStatus.FRILANSER, false),
                lagBgpsaAvkortetArbeidstaker());
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBraListeDPOgTilkommetArbforhold() {
        return of(lagBgpsandel(BigDecimal.valueOf(254232), AVKORTET_PR_ÅR, 978,  AktivitetStatus.DAGPENGER, false ),
                lagBgpsandel(BigDecimal.valueOf(0), AVKORTET_PR_ÅR,24, AktivitetStatus.ARBEIDSTAKER, true ));

    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBraListeDPogUtenTilkommetArbforhold() {
        return of(lagBgpsandel(BigDecimal.valueOf(254232), AVKORTET_PR_ÅR, 1002,  AktivitetStatus.DAGPENGER, false ),
                lagBgpsandel(BigDecimal.valueOf(254232),  AVKORTET_PR_ÅR, 24,  AktivitetStatus.ARBEIDSTAKER, false ));

    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsandel(BigDecimal brPrÅr, BigDecimal avkortetPrÅr, long dagsats, AktivitetStatus aktivitetStatus, Boolean erTilkommetAndeler ) {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(brPrÅr)
                .medAvkortetPrÅr(avkortetPrÅr)
                .medDagsats(dagsats)
                .medAktivitetStatus(aktivitetStatus)
                .medErTilkommetAndel(erTilkommetAndeler)
                .medBeregningsperiode(BER_PERIODE)
                .build();
    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaAvkortetArbeidstaker() {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(ARBEIDSTAKER_BRUTTO_PR_ÅR)
                .medAvkortetPrÅr(AVKORTET_PR_ÅR)
                .medDagsats(ARBEIDSTAKER_DAGSATS)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .medErTilkommetAndel(Boolean.FALSE)
                .build();
    }
}