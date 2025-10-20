package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.BeregningMapper.utledLovhjemmelForBeregning;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;

class BeregningMapperTest {

    private static final String ARBEIDSGIVER1_NAVN = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER1_ORGNR = "1";
    private static final String ARBEIDSGIVER2_NAVN = "Arbeidsgiver2 AS";
    private static final String ARBEIDSGIVER2_ORGNR = "2";
    public static final UnaryOperator<String> HENT_NAVN = ref -> {
        if (ARBEIDSGIVER1_ORGNR.equals(ref)) {
            return ARBEIDSGIVER1_NAVN;
        } else if (ARBEIDSGIVER2_ORGNR.equals(ref)) {
            return ARBEIDSGIVER2_NAVN;
        }
        throw new IllegalStateException("Ukjent orgnr " + ref);
    };
    private static final int BRUTTO_ÅR_ARBEIDSFORHOLD1 = 120000;
    private static final int BRUTTO_ÅR_ARBEIDSFORHOLD2 = 310100;

    @Test
    void skal_mappe_arbeidsforhold_med_høyest_inntekt_først() {
        // Arrange
        var beregningsperiodeFom = LocalDate.now().minusDays(20);
        var beregningsperiodeTom = LocalDate.now().plusDays(20);
        var andel1 = new BeregningsgrunnlagAndelDto(0L, AktivitetStatusDto.ARBEIDSTAKER, BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1), null, false,
            OpptjeningAktivitetDto.ARBEID, beregningsperiodeFom, beregningsperiodeTom,
            new BgAndelArbeidsforholdDto(ARBEIDSGIVER1_ORGNR, null, BigDecimal.ZERO, BigDecimal.ZERO), false);
        var andel2 = new BeregningsgrunnlagAndelDto(0L, AktivitetStatusDto.ARBEIDSTAKER, BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2), null, false,
            OpptjeningAktivitetDto.ARBEID, beregningsperiodeFom, beregningsperiodeTom,
            new BgAndelArbeidsforholdDto(ARBEIDSGIVER2_ORGNR, null, BigDecimal.ZERO, BigDecimal.ZERO), false); // Skal sorteres først
        var andel3 = new BeregningsgrunnlagAndelDto(0L, AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE, null, null, false,
            OpptjeningAktivitetDto.NÆRING, beregningsperiodeFom, beregningsperiodeTom,
            new BgAndelArbeidsforholdDto(ARBEIDSGIVER2_ORGNR, null, BigDecimal.ZERO, BigDecimal.ZERO), false); // Ignoreres

        var beregningsgrunnlagPeriode = new BeregningsgrunnlagPeriodeDto(0L,
            BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1).add(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2)), null, List.of(), beregningsperiodeFom,
            beregningsperiodeTom, List.of(andel1, andel2, andel3));

        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER, AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE),
            null, null, List.of(beregningsgrunnlagPeriode), false, false);

        // Act
        var resultat = BeregningMapper.mapArbeidsforhold(beregningsgrunnlag, HENT_NAVN);

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER2_NAVN);
        assertThat(resultat.get(0).getMånedsinntekt()).isEqualTo(
            BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        assertThat(resultat.get(1).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER1_NAVN);
        assertThat(resultat.get(1).getMånedsinntekt()).isEqualTo(
            BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
    }


    @Test
    void er_militær() {
        // Arrange
        var beregningsperiodeFom = LocalDate.now().minusDays(20);
        var beregningsperiodeTom = LocalDate.now().plusDays(20);

        var andel1 = new BeregningsgrunnlagAndelDto(500L, AktivitetStatusDto.ARBEIDSTAKER, BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1), null, false,
            OpptjeningAktivitetDto.ARBEID, beregningsperiodeFom, beregningsperiodeTom, null, false);
        var andel2 = new BeregningsgrunnlagAndelDto(1000L, AktivitetStatusDto.MILITÆR_ELLER_SIVIL, BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2),
            null, false, OpptjeningAktivitetDto.MILITÆR_ELLER_SIVILTJENESTE, beregningsperiodeFom, beregningsperiodeTom, null, false);

        var beregningsgrunnlagPeriode = new BeregningsgrunnlagPeriodeDto(0L,
            BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1).add(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2)), null, List.of(), beregningsperiodeFom,
            beregningsperiodeTom, List.of(andel1, andel2));

        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER, AktivitetStatusDto.MILITÆR_ELLER_SIVIL), null,
            null, List.of(beregningsgrunnlagPeriode), false, false);

        // Act & Assert
        assertThat(BeregningMapper.erMilitærSivil(beregningsgrunnlag)).isTrue();
    }

    @Test
    void er_ikke_militær() {
        // Arrange
        var beregningsperiodeFom = LocalDate.now().minusDays(20);
        var beregningsperiodeTom = LocalDate.now().plusDays(20);

        var andel1 = new BeregningsgrunnlagAndelDto(500L, AktivitetStatusDto.ARBEIDSTAKER, BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1), null, false,
            OpptjeningAktivitetDto.ARBEID, beregningsperiodeFom, beregningsperiodeTom, null, false);
        var andel2 = new BeregningsgrunnlagAndelDto(0L, AktivitetStatusDto.MILITÆR_ELLER_SIVIL, BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2), null,
            false, OpptjeningAktivitetDto.MILITÆR_ELLER_SIVILTJENESTE, beregningsperiodeFom, beregningsperiodeTom, null, false);

        var beregningsgrunnlagPeriode = new BeregningsgrunnlagPeriodeDto(0L,
            BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD1).add(BigDecimal.valueOf(BRUTTO_ÅR_ARBEIDSFORHOLD2)), null, List.of(), beregningsperiodeFom,
            beregningsperiodeTom, List.of(andel1, andel2));

        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER, AktivitetStatusDto.MILITÆR_ELLER_SIVIL), null,
            null, List.of(beregningsgrunnlagPeriode), false, false);

        // Act & Assert
        assertThat(BeregningMapper.erMilitærSivil(beregningsgrunnlag)).isFalse();
    }

    @Test
    void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7_og_8_30() {
        // Arrange
        var behandling = defaultBuilder().behandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .behandlingsresultat(
                behandlingsresultat().behandlingResultatType(Behandlingsresultat.BehandlingResultatType.INNVILGET).build())
            .build();
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), HjemmelDto.F_14_7_8_30,
            BigDecimal.valueOf(100000), List.of(), false, false);

        // Act
        var hjemmel = utledLovhjemmelForBeregning(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4 og 8-30");
    }

    @Test
    void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7_og_8_49() {
        // Arrange
        var behandling = defaultBuilder().behandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .behandlingsresultat(
                behandlingsresultat().behandlingResultatType(Behandlingsresultat.BehandlingResultatType.INNVILGET).build())
            .build();
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), HjemmelDto.F_14_7_8_49,
            BigDecimal.valueOf(100000), List.of(), false, false);

        // Act
        var hjemmel = utledLovhjemmelForBeregning(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§§ 14-4 og 8-49");
    }

    @Test
    void skal_utlede_SVP_hjemmel_for_beregning_når_fpsak_sender_14_7() {
        // Arrange
        var behandling = defaultBuilder().behandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .behandlingsresultat(
                behandlingsresultat().behandlingResultatType(Behandlingsresultat.BehandlingResultatType.INNVILGET).build())
            .build();
        var beregningsgrunnlag = new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), HjemmelDto.F_14_7, BigDecimal.valueOf(100000),
            List.of(), false, false);

        // Act
        var hjemmel = utledLovhjemmelForBeregning(beregningsgrunnlag, behandling);

        // Assert
        assertThat(hjemmel).containsOnlyOnce("§ 14-4");
    }
}
