package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.finnBrutto;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.finnSeksG;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.harBruktBruttoBeregningsgrunnlag;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.inntektOverSeksG;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.mapRegelListe;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp.of;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.ARBEIDSTAKER;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.DAGPENGER;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.FRILANSER;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.MILITÆR_ELLER_SIVIL;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;

class BeregningsgrunnlagMapperTest {

    private static final BigDecimal AVKORTET_PR_ÅR = BigDecimal.valueOf(542987.4);
    private static final BigDecimal FRILANSER_BRUTTO_PR_ÅR = BigDecimal.valueOf(95406.6);
    private static final BigDecimal ARBEIDSTAKER_BRUTTO_PR_ÅR = BigDecimal.valueOf(1000000);
    private static final BigDecimal GRUNNBELØP = BigDecimal.valueOf(50_000);
    private static final long STANDARD_PERIODE_DAGSATS = 100L;
    private static final long FRILANSER_DAGSATS = 200L;
    private static final long ARBEIDSTAKER_DAGSATS = 300L;
    private static final LocalDate BER_PERIODE_FOM = LocalDate.of(2021, 1, 1);
    private static final LocalDate BER_PERIODE_TOM = LocalDate.of(2021, 9, 1);
    private static final UnaryOperator<String> HENT_NAVN = _ -> "Navn";

    @Test
    void skal_finne_brutto() {
        // Arrange
        var andel = lagBraListeFrilanser();
        var periode = lagBeregningsgrunnlagPeriode(andel);
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(), null, GRUNNBELØP, List.of(periode), false, false);

        // Act + Assert
        assertThat(finnBrutto(beregningsgrunnlag)).isEqualTo(of(ARBEIDSTAKER_BRUTTO_PR_ÅR.add(FRILANSER_BRUTTO_PR_ÅR).longValue()));
    }

    @Test
    void skal_finne_seksG() {
        // Arrange
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(), null, GRUNNBELØP, List.of(lagBeregningsgrunnlagPeriode(lagBraListeFrilanser())),
            false, false);

        // Act + Assert
        assertThat(finnSeksG(beregningsgrunnlag)).isEqualTo(GRUNNBELØP.multiply(BigDecimal.valueOf(6)));
    }

    @Test
    void skal_identifisere_brutto_over_6g() {
        // Arrange
        var andelsliste = lagBraListeFrilanser();
        var bruttoPrÅr = GRUNNBELØP.multiply(BigDecimal.valueOf(6)).add(BigDecimal.ONE);
        var periode = new BeregningsgrunnlagPeriodeDto(STANDARD_PERIODE_DAGSATS, bruttoPrÅr, AVKORTET_PR_ÅR, List.of(), null, null, andelsliste);
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(), null, GRUNNBELØP, List.of(periode), false, false);

        // Act + Assert
        assertThat(inntektOverSeksG(beregningsgrunnlag)).isTrue();
    }

    @Test
    void skal_identifisere_ikke_brutto_over_6g() {
        // Arrange
        var andelsliste = lagBraListeFrilanser();
        var bruttoPrÅr = GRUNNBELØP.multiply(BigDecimal.valueOf(6));
        var periode = new BeregningsgrunnlagPeriodeDto(STANDARD_PERIODE_DAGSATS, bruttoPrÅr, AVKORTET_PR_ÅR, List.of(), null, null, andelsliste);
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(), null, GRUNNBELØP, List.of(periode), false, false);

        // Act + Assert
        assertThat(inntektOverSeksG(beregningsgrunnlag)).isFalse();
    }

    @Test
    void skal_mappe_regelListe() {
        // Arrange

        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(FRILANSER, ARBEIDSTAKER), null, GRUNNBELØP,
            List.of(lagBeregningsgrunnlagPeriode(lagBraListeFrilanser())), false, false);

        // Act
        var regler = tilRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler).hasSize(2);

        assertThat(regler.getFirst().getRegelStatus()).isEqualTo(AktivitetStatus.FRILANSER.name());
        assertThat(regler.getFirst().getAndelListe()).hasSize(1);
        assertThat(regler.getFirst().getAndelListe().getFirst().getDagsats()).isEqualTo(FRILANSER_DAGSATS);
        assertThat(regler.get(0).getAndelListe().getFirst().getMånedsinntekt()).isEqualTo(
            FRILANSER_BRUTTO_PR_ÅR.divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        assertThat(regler.get(0).getAndelListe().getFirst().getÅrsinntekt()).isEqualTo(FRILANSER_BRUTTO_PR_ÅR.longValue());

        assertThat(regler.get(1).getRegelStatus()).isEqualTo(AktivitetStatus.ARBEIDSTAKER.name());
        assertThat(regler.get(1).getAndelListe()).hasSize(1);
        assertThat(regler.get(1).getAndelListe().getFirst().getDagsats()).isEqualTo(ARBEIDSTAKER_DAGSATS);
        assertThat(regler.get(1).getAndelListe().getFirst().getMånedsinntekt()).isEqualTo(
            ARBEIDSTAKER_BRUTTO_PR_ÅR.divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        assertThat(regler.get(1).getAndelListe().getFirst().getÅrsinntekt()).isEqualTo(ARBEIDSTAKER_BRUTTO_PR_ÅR.longValue());
    }

    @Test
    void skal_mappe_regelListe_for_dagpenger_med_tilkommet_arbforhold() {
        // Arrange

        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(DAGPENGER), null, null,
            List.of(lagBeregningsgrunnlagPeriode(lagBraListeDPOgTilkommetArbforhold())), false, false);


        // Act
        var regler = tilRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler).hasSize(1);
        assertThat(regler.getFirst().getRegelStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.getFirst().getAndelListe()).hasSize(1);
        assertThat(regler.getFirst().getAndelListe().getFirst().getAktivitetStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.getFirst().getAndelListe().getFirst().getDagsats()).isEqualTo(1002);

    }

    @Test
    void skal_mappe_regelListe_med_for_dagpenger_uten_tilkommet_arbforhold() {
        // Arrange
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(DAGPENGER), null, null,
            List.of(lagBeregningsgrunnlagPeriode(lagBraListeFor2Statuser(AktivitetStatusDto.DAGPENGER, 1002, AktivitetStatusDto.ARBEIDSTAKER))),
            false, false);


        // Act
        var regler = tilRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler).hasSize(1);
        assertThat(regler.getFirst().getRegelStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.getFirst().getAndelListe()).hasSize(1);
        assertThat(regler.getFirst().getAndelListe().getFirst().getAktivitetStatus()).isEqualTo(AktivitetStatus.DAGPENGER.name());
        assertThat(regler.getFirst().getAndelListe().getFirst().getDagsats()).isEqualTo(1002);

    }

    @Test
    void skal_mappe_sistLignedeÅr_når_selvstendig_næringsdrivende() {
        // Arrange
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(SELVSTENDIG_NÆRINGSDRIVENDE, ARBEIDSTAKER), null, null, List.of(
            lagBeregningsgrunnlagPeriode(
                of(lagBgpsandel(BigDecimal.valueOf(254232), null, 978, AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE, false),
                    lagBgpsandel(BigDecimal.valueOf(0), null, 24, AktivitetStatusDto.ARBEIDSTAKER, true)))), false, false);

        // Act
        var regler = tilRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(regler.get(0).getAndelListe().getFirst().getSistLignedeÅr()).isEqualTo(BER_PERIODE_TOM.getYear());
        assertThat(regler.get(1).getAndelListe().getFirst().getSistLignedeÅr()).isZero();
    }

    private static List<BeregningsgrunnlagRegel> tilRegelListe(BeregningsgrunnlagDto beregningsgrunnlag) {
        return mapRegelListe(beregningsgrunnlag, HENT_NAVN);
    }

    @Test
    void skal_finne_at_brutto_beregningsgrunnlag_er_brukt_fordi_det_er_mer_enn_en_regel() {
        // Arrange
        var regel1 = BeregningsgrunnlagRegel.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSAVKLARINGSPENGER.name()).build();
        var regel2 = BeregningsgrunnlagRegel.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name()).build();

        // Act
        var resultat = harBruktBruttoBeregningsgrunnlag(of(regel1, regel2));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_finne_at_brutto_beregningsgrunnlag_er_brukt_fordi_det_er_kombinert_status() {
        // Arrange
        var regel1 = BeregningsgrunnlagRegel.ny().medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name()).build();

        // Act
        var resultat = harBruktBruttoBeregningsgrunnlag(of(regel1));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_finne_at_brutto_beregningsgrunnlag_ikke_er_brukt() {
        // Arrange
        var regel1 = BeregningsgrunnlagRegel.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name()).build();

        // Act
        var resultat = harBruktBruttoBeregningsgrunnlag(of(regel1));

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    void beregningsgrunnlag_med_militærstatus_med_dagsats_skal_ignorere_andre_statuser() {
        // Arrange
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(ARBEIDSTAKER, MILITÆR_ELLER_SIVIL), null, null, List.of(
            lagBeregningsgrunnlagPeriode(lagBraListeFor2Statuser(AktivitetStatusDto.MILITÆR_ELLER_SIVIL, 1002, AktivitetStatusDto.ARBEIDSTAKER))),
            false, false);

        // Act
        var beregningsgrunnlagRegler = tilRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(beregningsgrunnlagRegler).hasSize(1);
        assertThat(beregningsgrunnlagRegler.getFirst().getRegelStatus()).isEqualTo("MILITÆR_ELLER_SIVIL");
        assertThat(beregningsgrunnlagRegler.getFirst().getAndelListe().getFirst().getDagsats()).isEqualTo(1002);
    }

    @Test
    void beregningsgrunnlag_med_militærstatus_uten_dagsats_skal_fungere_som_før() {
        // Arrange
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(ARBEIDSTAKER, MILITÆR_ELLER_SIVIL), null, null, List.of(
            lagBeregningsgrunnlagPeriode(lagBraListeFor2Statuser(AktivitetStatusDto.MILITÆR_ELLER_SIVIL, 0, AktivitetStatusDto.ARBEIDSTAKER))), false,
            false);

        // Act
        var beregningsgrunnlagRegler = tilRegelListe(beregningsgrunnlag);

        // Assert
        assertThat(beregningsgrunnlagRegler).hasSize(2);
        assertThat(beregningsgrunnlagRegler.get(0).getRegelStatus()).isEqualTo("ARBEIDSTAKER");
        assertThat(beregningsgrunnlagRegler.get(0).getAndelListe().getFirst().getDagsats()).isEqualTo(24);
        assertThat(beregningsgrunnlagRegler.get(1).getRegelStatus()).isEqualTo("MILITÆR_ELLER_SIVIL");
        assertThat(beregningsgrunnlagRegler.get(1).getAndelListe().getFirst().getDagsats()).isZero();
    }


    private BeregningsgrunnlagPeriodeDto lagBeregningsgrunnlagPeriode(List<BeregningsgrunnlagAndelDto> andelsliste) {
        return new BeregningsgrunnlagPeriodeDto(STANDARD_PERIODE_DAGSATS, FRILANSER_BRUTTO_PR_ÅR, AVKORTET_PR_ÅR, List.of(), null, null, andelsliste);
    }

    private List<BeregningsgrunnlagAndelDto> lagBraListeFrilanser() {
        return of(lagBgpsandel(FRILANSER_BRUTTO_PR_ÅR, null, FRILANSER_DAGSATS, AktivitetStatusDto.FRILANSER, false), lagBgpsaAvkortetArbeidstaker());
    }

    private List<BeregningsgrunnlagAndelDto> lagBraListeFor2Statuser(AktivitetStatusDto aktivitetStatus1,
                                                                     long dagsats,
                                                                     AktivitetStatusDto aktivitetStatus2) {
        return of(lagBgpsandel(BigDecimal.valueOf(254232), AVKORTET_PR_ÅR, dagsats, aktivitetStatus1, false),
            lagBgpsandel(BigDecimal.valueOf(254232), AVKORTET_PR_ÅR, 24, aktivitetStatus2, false));

    }

    private List<BeregningsgrunnlagAndelDto> lagBraListeDPOgTilkommetArbforhold() {
        return of(lagBgpsandel(BigDecimal.valueOf(254232), AVKORTET_PR_ÅR, 978, AktivitetStatusDto.DAGPENGER, false),
            lagBgpsandel(BigDecimal.valueOf(0), AVKORTET_PR_ÅR, 24, AktivitetStatusDto.ARBEIDSTAKER, true));

    }

    private BeregningsgrunnlagAndelDto lagBgpsandel(BigDecimal brPrÅr,
                                                    BigDecimal avkortetPrÅr,
                                                    long dagsats,
                                                    AktivitetStatusDto aktivitetStatus,
                                                    Boolean erTilkommetAndeler) {

        return new BeregningsgrunnlagAndelDto(dagsats, aktivitetStatus, brPrÅr, avkortetPrÅr, null, null, BER_PERIODE_FOM,
            BER_PERIODE_TOM, null, erTilkommetAndeler);
    }

    private BeregningsgrunnlagAndelDto lagBgpsaAvkortetArbeidstaker() {
        return new BeregningsgrunnlagAndelDto(ARBEIDSTAKER_DAGSATS, ARBEIDSTAKER, ARBEIDSTAKER_BRUTTO_PR_ÅR, AVKORTET_PR_ÅR, null, null,
            BER_PERIODE_FOM, BER_PERIODE_TOM, null, false);
    }
}
