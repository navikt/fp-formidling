package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;

class BeregningsgrunnlagMapperTest {

    private BeregningsgrunnlagDto beregningsgrunnlag;
    private static final BigDecimal AVKORTET_PR_ÅR = BigDecimal.valueOf(60);
    private static final BigDecimal BRUTTO_PR_ÅR = BigDecimal.valueOf(120);
    private static final long STANDARD_PERIODE_DAGSATS = 100L;
    private static final LocalDate PERIODE_FOM = LocalDate.now();
    private static final LocalDate PERIODE_TOM = LocalDate.now().plusDays(30);

    @BeforeEach
    void standard_setup() {
        beregningsgrunnlag = lagBeregningsgrunnlag();
    }

    private BeregningsgrunnlagDto lagBeregningsgrunnlag() {
        var periode = lagBeregningsgrunnlagPeriode();
        return new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER, AktivitetStatusDto.FRILANSER), null, BigDecimal.valueOf(100000),
            List.of(periode), false, false);
    }

    private BeregningsgrunnlagPeriodeDto lagBeregningsgrunnlagPeriode() {
        return new BeregningsgrunnlagPeriodeDto(STANDARD_PERIODE_DAGSATS, BRUTTO_PR_ÅR, AVKORTET_PR_ÅR, List.of(), PERIODE_FOM, PERIODE_TOM,
            lagBgpsaListe());
    }

    private List<BeregningsgrunnlagAndelDto> lagBgpsaListe() {
        return List.of(lagBgpsaBruttoFrilanser(), lagBgpsaAvkortetArbeidstaker());
    }

    private BeregningsgrunnlagAndelDto lagBgpsaAap() {
        return new BeregningsgrunnlagAndelDto(STANDARD_PERIODE_DAGSATS, AktivitetStatusDto.ARBEIDSAVKLARINGSPENGER, BRUTTO_PR_ÅR, null, false,
            OpptjeningAktivitetDto.ARBEIDSAVKLARING, PERIODE_FOM, PERIODE_TOM, null, false);
    }

    private BeregningsgrunnlagAndelDto lagBgpsaBruttoFrilanser() {
        return new BeregningsgrunnlagAndelDto(0L, AktivitetStatusDto.FRILANSER, BRUTTO_PR_ÅR, null, false, OpptjeningAktivitetDto.FRILANS,
            PERIODE_FOM, PERIODE_TOM, null, false);
    }

    private BeregningsgrunnlagAndelDto lagBgpsaSN() {
        return new BeregningsgrunnlagAndelDto(0L, AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE, null, AVKORTET_PR_ÅR, false,
            OpptjeningAktivitetDto.NÆRING, PERIODE_FOM, PERIODE_TOM, null, false);
    }

    private BeregningsgrunnlagAndelDto lagBgpsaAvkortetArbeidstaker() {
        return new BeregningsgrunnlagAndelDto(0L, AktivitetStatusDto.ARBEIDSTAKER, null, AVKORTET_PR_ÅR, false, OpptjeningAktivitetDto.ARBEID,
            PERIODE_FOM, PERIODE_TOM, new BgAndelArbeidsforholdDto("123", null, BigDecimal.ZERO, BigDecimal.ZERO), false);
    }

    @Test
    void skal_identifsere_statuser() {
        var arbeidstakerAndeler = BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(AktivitetStatusDto.ARBEIDSTAKER,
            beregningsgrunnlag.beregningsgrunnlagperioder().get(0).beregningsgrunnlagandeler());
        var frilansAndeler = BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(AktivitetStatusDto.FRILANSER,
            beregningsgrunnlag.beregningsgrunnlagperioder().get(0).beregningsgrunnlagandeler());

        assertThat(arbeidstakerAndeler).hasSize(1);
        assertThat(frilansAndeler).hasSize(1);

        assertThat(arbeidstakerAndeler.get(0).aktivitetStatus()).isEqualTo(AktivitetStatusDto.ARBEIDSTAKER);
        assertThat(frilansAndeler.get(0).aktivitetStatus()).isEqualTo(AktivitetStatusDto.FRILANSER);
    }

    @Test
    void skal_matche_aap() {
        var bgAktivitetStatus = AktivitetStatusDto.ARBEIDSAVKLARINGSPENGER;
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, List.of(lagBgpsaAap()))).isNotEmpty();
    }

    @Test
    void skal_matche_alt_på_kun_ytelse() {
        var bgAktivitetStatus = AktivitetStatusDto.KUN_YTELSE;
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, List.of(lagBgpsaAap()))).isNotEmpty();
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, List.of(lagBgpsaSN()))).isNotEmpty();
    }

    @Test
    void skal_kaste_exception_matcher_ikke() {
        var bgAktivitetStatus = AktivitetStatusDto.FRILANSER;
        var bgpsaListe = List.of(lagBgpsaAvkortetArbeidstaker(), lagBgpsaSN());
        assertThrows(IllegalStateException.class, () -> BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaListe));
    }

    @Test
    void skal_matche_AT_SN() {
        var bgAktivitetStatus = AktivitetStatusDto.KOMBINERT_AT_SN;
        assertThat(BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus,
            List.of(lagBgpsaAvkortetArbeidstaker(), lagBgpsaSN()))).hasSize(2);
    }

    @Test
    void skal_ikke_finne_besteBeregning() {
        assertThat(beregningsgrunnlag.erBesteberegnet()).isFalse();
    }

    @Test
    void skal_finne_besteBeregning() {
        beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(), null, BigDecimal.ZERO, List.of(), true, false);
        assertThat(beregningsgrunnlag.erBesteberegnet()).isTrue();
    }
}
