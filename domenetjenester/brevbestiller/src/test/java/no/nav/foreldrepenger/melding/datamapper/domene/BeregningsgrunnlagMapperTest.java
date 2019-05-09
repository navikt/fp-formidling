package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.typer.Beløp;

public class BeregningsgrunnlagMapperTest {

    private Beregningsgrunnlag beregningsgrunnlag;
    private static final BigDecimal AVKORTET_PR_ÅR = BigDecimal.valueOf(60);
    private static final BigDecimal BRUTTO_PR_ÅR = BigDecimal.valueOf(120);
    private static final BigDecimal GRUNNBELØP = BigDecimal.valueOf(50_000);
    private static final long STANDARD_PERIODE_DAGSATS = 100L;

    @Before
    public void standard_setup() {
        beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode())
                .build();
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode() {
        return BeregningsgrunnlagPeriode.ny()
                .medDagsats(STANDARD_PERIODE_DAGSATS)
                .medBruttoPrÅr(BRUTTO_PR_ÅR)
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListe())
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListe() {
        return List.of(lagBgpsaBruttoFrilanser(), lagBgpsaAvkortetArbeidstaker());
    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaBruttoFrilanser() {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(BRUTTO_PR_ÅR)
                .medAktivitetStatus(AktivitetStatus.FRILANSER)
                .build();
    }


    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaAvkortetArbeidstaker() {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
                .medAvkortetPrÅr(AVKORTET_PR_ÅR)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .build();
    }

    @Test
    public void skal_finne_brutto() {
        assertThat(BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag)).isEqualTo(BRUTTO_PR_ÅR.add(AVKORTET_PR_ÅR).longValue());
    }

    @Test
    public void skal_identifsere_overbetalt() {
        Beregningsgrunnlag originaltBeregninsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(
                        BeregningsgrunnlagPeriode.ny()
                                .medDagsats(STANDARD_PERIODE_DAGSATS + 5)
                                .build()
                )
                .build();
        assertThat(BeregningsgrunnlagMapper.erOverbetalt(beregningsgrunnlag, originaltBeregninsgrunnlag)).isTrue();
    }

    @Test
    public void skal_identifsere_ikke_overbetalt() {
        Beregningsgrunnlag originaltBeregninsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(
                        BeregningsgrunnlagPeriode.ny()
                                .medDagsats(STANDARD_PERIODE_DAGSATS - 5)
                                .build()
                )
                .build();
        assertThat(BeregningsgrunnlagMapper.erOverbetalt(beregningsgrunnlag, originaltBeregninsgrunnlag)).isFalse();
    }

    @Test
    public void skal_identifsere_ikke_overbetalt_ingen_original() {
        assertThat(BeregningsgrunnlagMapper.erOverbetalt(beregningsgrunnlag, null)).isFalse();
    }

    @Test
    public void skal_identifsere_statuser() {
        List<BeregningsgrunnlagPrStatusOgAndel> arbeidstakerAndeler = BeregningsgrunnlagMapper
                .finnAktivitetStatuserForAndeler(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER),
                        beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());
        List<BeregningsgrunnlagPrStatusOgAndel> frilansAndeler = BeregningsgrunnlagMapper
                .finnAktivitetStatuserForAndeler(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.FRILANSER),
                        beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());

        List<BeregningsgrunnlagPrStatusOgAndel> selvstendigAndeler = BeregningsgrunnlagMapper
                .finnAktivitetStatuserForAndeler(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE),
                        beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());
        assertThat(arbeidstakerAndeler).hasSize(1);
        assertThat(frilansAndeler).hasSize(1);
        assertThat(selvstendigAndeler).hasSize(0);

        assertThat(arbeidstakerAndeler.get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(frilansAndeler.get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.FRILANSER);
    }

    @Test
    public void skal_ikke_finne_besteBeregning() {
        assertThat(BeregningsgrunnlagMapper
                .harNoenAvAndeleneBesteberegning(beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                        .getBeregningsgrunnlagPrStatusOgAndelList()))
                .isFalse();
    }

    @Test
    public void skal_finne_besteBeregning() {
        beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(BeregningsgrunnlagPeriode.ny()
                        .medBeregningsgrunnlagPrStatusOgAndelList(List.of(BeregningsgrunnlagPrStatusOgAndel.ny()
                                .medBesteberegningPrÅr(BigDecimal.TEN)
                                .build()))
                        .build())
                .build();
        assertThat(BeregningsgrunnlagMapper
                .harNoenAvAndeleneBesteberegning(beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                        .getBeregningsgrunnlagPrStatusOgAndelList()))
                .isTrue();
    }

    @Test
    public void skal_identifsere_nyoppstartet_sn() {
        assertThat(BeregningsgrunnlagMapper.nyoppstartetSelvstendingNæringsdrivende(beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                .getBeregningsgrunnlagPrStatusOgAndelList()))
                .isFalse();

        beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(BeregningsgrunnlagPeriode.ny()
                        .medBeregningsgrunnlagPrStatusOgAndelList(List.of(BeregningsgrunnlagPrStatusOgAndel.ny()
                                .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
                                .medNyIArbeidslivet(true)
                                .build()))
                        .build())
                .build();

        assertThat(BeregningsgrunnlagMapper.nyoppstartetSelvstendingNæringsdrivende(beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                .getBeregningsgrunnlagPrStatusOgAndelList()))
                .isTrue();

        beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(BeregningsgrunnlagPeriode.ny()
                        .medBeregningsgrunnlagPrStatusOgAndelList(List.of(BeregningsgrunnlagPrStatusOgAndel.ny()
                                .medAktivitetStatus(AktivitetStatus.FRILANSER)
                                .medNyIArbeidslivet(true)
                                .build()))
                        .build())
                .build();

        assertThat(BeregningsgrunnlagMapper.nyoppstartetSelvstendingNæringsdrivende(beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                .getBeregningsgrunnlagPrStatusOgAndelList()))
                .isFalse();
    }

    @Test
    public void skal_telle_antall_arbeidsforhold() {
        assertThat(BeregningsgrunnlagMapper.tellAntallArbeidsforholdIBeregningUtenSluttpakke(
                beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList()))
                .isEqualTo(1);
    }

    @Test
    public void skal_identifisere_brutto_over_6g() {

        beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medGrunnbeløp(new Beløp(GRUNNBELØP))
                .leggTilBeregningsgrunnlagPeriode(
                        BeregningsgrunnlagPeriode.ny()
                                .medBruttoPrÅr(GRUNNBELØP.multiply(BigDecimal.valueOf(6)).add(BigDecimal.ONE))
                                .build())
                .build();
        assertThat(BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag)).isTrue();
    }

    @Test
    public void skal_identifisere_ikke_brutto_over_6g() {

        beregningsgrunnlag = Beregningsgrunnlag.ny()
                .medGrunnbeløp(new Beløp(GRUNNBELØP))
                .leggTilBeregningsgrunnlagPeriode(
                        BeregningsgrunnlagPeriode.ny()
                                .medBruttoPrÅr(GRUNNBELØP.multiply(BigDecimal.valueOf(6)))
                                .build())
                .build();
        assertThat(BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag)).isFalse();
    }

}
