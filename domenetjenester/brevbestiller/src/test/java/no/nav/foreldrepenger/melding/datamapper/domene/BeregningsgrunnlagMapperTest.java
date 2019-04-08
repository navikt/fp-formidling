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

public class BeregningsgrunnlagMapperTest {

    private Beregningsgrunnlag beregningsgrunnlag;
    private BigDecimal avkortetPrÅr = BigDecimal.ONE;
    private BigDecimal bruttoPrÅr = BigDecimal.TEN;
    private long standardPeriodeDagsats = 100L;

    @Before
    public void standard_setup() {
        beregningsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(lagBeregningsgrunnlagPeriode())
                .build();
    }

    private BeregningsgrunnlagPeriode lagBeregningsgrunnlagPeriode() {
        return BeregningsgrunnlagPeriode.ny()
                .medDagsats(standardPeriodeDagsats)
                .medBeregningsgrunnlagPrStatusOgAndelList(lagBgpsaListe())
                .build();
    }

    private List<BeregningsgrunnlagPrStatusOgAndel> lagBgpsaListe() {
        return List.of(lagBgpsaBruttoFrilanser(), lagBgpsaAvkortetArbeidstaker());
    }

    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaBruttoFrilanser() {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBruttoPrÅr(bruttoPrÅr)
                .medAktivitetStatus(AktivitetStatus.FRILANSER.getKode())
                .build();
    }


    private BeregningsgrunnlagPrStatusOgAndel lagBgpsaAvkortetArbeidstaker() {
        return BeregningsgrunnlagPrStatusOgAndel.ny()
                .medAvkortetPrÅr(avkortetPrÅr)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.getKode())
                .build();
    }

    @Test
    public void skal_finne_brutto() {
        assertThat(BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag)).isEqualTo(bruttoPrÅr.add(avkortetPrÅr).longValue());
    }

    @Test
    public void skal_identifsere_overbetalt() {
        Beregningsgrunnlag originaltBeregninsgrunnlag = Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(
                        BeregningsgrunnlagPeriode.ny()
                                .medDagsats(standardPeriodeDagsats + 5)
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
                                .medDagsats(standardPeriodeDagsats - 5)
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
                .finnAktivitetStatuserForAndeler(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.getKode()),
                        beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());
        List<BeregningsgrunnlagPrStatusOgAndel> frilansAndeler = BeregningsgrunnlagMapper
                .finnAktivitetStatuserForAndeler(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.FRILANSER.getKode()),
                        beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());

        List<BeregningsgrunnlagPrStatusOgAndel> selvstendigAndeler = BeregningsgrunnlagMapper
                .finnAktivitetStatuserForAndeler(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode()),
                        beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList());
        assertThat(arbeidstakerAndeler).hasSize(1);
        assertThat(frilansAndeler).hasSize(1);
        assertThat(selvstendigAndeler).hasSize(0);

        assertThat(arbeidstakerAndeler.get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.ARBEIDSTAKER.getKode());
        assertThat(frilansAndeler.get(0).getAktivitetStatus()).isEqualTo(AktivitetStatus.FRILANSER.getKode());
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
                                .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode())
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
                                .medAktivitetStatus(AktivitetStatus.FRILANSER.getKode())
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
        assertThat(BeregningsgrunnlagMapper.tellAntallArbeidsforholdIBeregning(
                beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList()))
                .isEqualTo(1);
    }
}
