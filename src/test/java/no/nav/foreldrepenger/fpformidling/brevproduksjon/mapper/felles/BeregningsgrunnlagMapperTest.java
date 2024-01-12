package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;

class BeregningsgrunnlagMapperTest {

    private Beregningsgrunnlag beregningsgrunnlag;
    private static final BigDecimal AVKORTET_PR_ÅR = BigDecimal.valueOf(60);
    private static final BigDecimal BRUTTO_PR_ÅR = BigDecimal.valueOf(120);
    private static final BigDecimal GRUNNBELØP = BigDecimal.valueOf(50_000);
    private static final long STANDARD_PERIODE_DAGSATS = 100L;

    @BeforeEach
    void standard_setup() {
        beregningsgrunnlag = Beregningsgrunnlag.ny().leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode()).build();
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode() {
        return BeregningsgrunnlagPeriode.ny()
            .medDagsats(STANDARD_PERIODE_DAGSATS)
            .medBruttoPrÅr(BRUTTO_PR_ÅR)
            .medAvkortetPrÅr(AVKORTET_PR_ÅR)
            .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListe())
            .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListe() {
        return List.of(lagBgpsaBruttoFrilanser(), lagBgpsaAvkortetArbeidstaker());
    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaAap() {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
            .medDagsats(STANDARD_PERIODE_DAGSATS)
            .medBruttoPrÅr(BRUTTO_PR_ÅR)
            .medAktivitetStatus(AktivitetStatus.ARBEIDSAVKLARINGSPENGER)
            .build();
    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaBruttoFrilanser() {
        return BeregningsgrunnlagPrStatusOgAndel.ny().medBruttoPrÅr(BRUTTO_PR_ÅR).medAktivitetStatus(AktivitetStatus.FRILANSER).build();
    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaSN() {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
            .medAvkortetPrÅr(AVKORTET_PR_ÅR)
            .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
            .build();
    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaAvkortetArbeidstaker() {
        return BeregningsgrunnlagPrStatusOgAndel.ny().medAvkortetPrÅr(AVKORTET_PR_ÅR).medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER).build();
    }

    @Test
    void skal_identifsere_statuser() {
        var arbeidstakerAndeler = BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(
            new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER),
            beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());
        var frilansAndeler = BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(
            new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.FRILANSER),
            beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());

        assertThat(arbeidstakerAndeler).hasSize(1);
        assertThat(frilansAndeler).hasSize(1);

        assertThat(arbeidstakerAndeler.get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(frilansAndeler.get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.FRILANSER);
    }

    @Test
    void skal_matche_aap() {
        var bgAktivitetStatus = new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, List.of(lagBgpsaAap()))).isNotEmpty();
    }

    @Test
    void skal_matche_alt_på_kun_ytelse() {
        var bgAktivitetStatus = new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.KUN_YTELSE);
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, List.of(lagBgpsaAap()))).isNotEmpty();
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, List.of(lagBgpsaSN()))).isNotEmpty();
    }

    @Test
    void skal_kaste_exception_matcher_ikke() {
        var bgAktivitetStatus = new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.FRILANSER);
        var bgpsaListe = List.of(lagBgpsaAvkortetArbeidstaker(), lagBgpsaSN());
        assertThrows(IllegalStateException.class, () -> BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaListe));
    }

    @Test
    void skal_matche_AT_SN() {
        var bgAktivitetStatus = new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.KOMBINERT_AT_SN);
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus,
            List.of(lagBgpsaAvkortetArbeidstaker(), lagBgpsaSN()))).hasSize(2);
    }

    @Test
    void skal_ikke_finne_besteBeregning() {
        assertThat(beregningsgrunnlag.getErBesteberegnet()).isFalse();
    }

    @Test
    void skal_finne_besteBeregning() {
        beregningsgrunnlag = Beregningsgrunnlag.ny().medBesteberegnet(true).build();
        assertThat(beregningsgrunnlag.getErBesteberegnet()).isTrue();
    }
}
