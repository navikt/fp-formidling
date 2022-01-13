package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse.NaturalytelseStatus;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class NaturalytelseMapperTest {

    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().minusDays(1);
    private static final LocalDate PERIODE2_FOM = LocalDate.now();
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final String ARBEIDSGIVER_NAVN = "Arbeidsgiver AS";
    private static final Arbeidsgiver ARBEIDSGIVER = new Arbeidsgiver("1", ARBEIDSGIVER_NAVN);
    private static final long DAGSATS = 1000L;

    @Test
    public void skal_ikke_utlede_naturalytelse_som_starter_med_første_periode() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, false);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), null, of(PeriodeÅrsak.NATURALYTELSE_BORTFALT.getKode()), false);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isEmpty();
    }

    @Test
    public void skal_utlede_at_naturalytelse_bortfaller_når_årsak_er_bortfaller() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), null, of(PeriodeÅrsak.NATURALYTELSE_BORTFALT.getKode()), true);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.BORTFALLER);
        assertThat(resultat.get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_NAVN);
        assertThat(resultat.get(0).getNyDagsats()).isEqualTo(DAGSATS);
        assertThat(resultat.get(0).getEndringsdato()).isEqualTo(formaterDatoNorsk(PERIODE2_FOM));
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_er_tilkommer() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, null, BigDecimal.valueOf(1000), of(PeriodeÅrsak.NATURALYTELSE_TILKOMMER.getKode()), true);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    @Test
    public void skal_utlede_at_naturalytelse_bortfaller_når_årsak_ikke_er_angitt_og_bare_bortfaller_er_angitt() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), null, of(), true);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.BORTFALLER);
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bare_tilkommer_er_angitt() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, null, BigDecimal.valueOf(1000), of(), true);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    @Test
    public void skal_utlede_at_naturalytelse_bortfaller_når_årsak_ikke_er_angitt_og_bortfaller_er_større_enn_tilkommer() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), BigDecimal.valueOf(500), of(), true);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.BORTFALLER);
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bortfaller_er_lik_tilkommer() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), of(), true);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bortfaller_er_mindre_enn_tilkommer() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(500), BigDecimal.valueOf(1000), of(), true);

        // Act
        List<Naturalytelse> resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB);

        // Assert
        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    private TilkjentYtelseForeldrepenger gettilkjentYtelseFP(Arbeidsgiver arbeidsgiver, boolean inkluderePeriode2) {
        TilkjentYtelseAndel andel = TilkjentYtelseAndel.ny()
                .medArbeidsgiver(arbeidsgiver)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .build();
        TilkjentYtelsePeriode resultatPeriode1 = TilkjentYtelsePeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medDagsats(DAGSATS)
                .medAndeler(of(andel))
                .build();
        TilkjentYtelsePeriode resultatPeriode2 = TilkjentYtelsePeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medDagsats(DAGSATS)
                .medAndeler(of(andel))
                .build();
        return TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(inkluderePeriode2 ? of(resultatPeriode1, resultatPeriode2) : of(resultatPeriode1))
                .build();
    }

    private Beregningsgrunnlag getBeregningsgrunnlag(Arbeidsgiver arbeidsgiver, BigDecimal naturalytelseBortfaller, BigDecimal naturalytelseTilkommer, List<String> periodeÅrsaker, boolean inkluderePeriode2) {
        BGAndelArbeidsforhold bgAndelArbeidsforhold = new BGAndelArbeidsforhold(arbeidsgiver, ArbeidsforholdRef.ref("1"), naturalytelseBortfaller, naturalytelseTilkommer);
        BeregningsgrunnlagPrStatusOgAndel beregningsgrunnlagPrStatusOgAndel = BeregningsgrunnlagPrStatusOgAndel.ny()
                .medBgAndelArbeidsforhold(bgAndelArbeidsforhold)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .build();
        BeregningsgrunnlagPeriode grunnlagPeriode1 = BeregningsgrunnlagPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medBeregningsgrunnlagPrStatusOgAndelList(of(beregningsgrunnlagPrStatusOgAndel))
                .medperiodeÅrsaker(periodeÅrsaker)
                .medDagsats(DAGSATS)
                .build();
        BeregningsgrunnlagPeriode grunnlagPeriode2 = BeregningsgrunnlagPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medBeregningsgrunnlagPrStatusOgAndelList(of(beregningsgrunnlagPrStatusOgAndel))
                .medperiodeÅrsaker(periodeÅrsaker)
                .medDagsats(DAGSATS)
                .build();
        Beregningsgrunnlag.Builder builder = Beregningsgrunnlag.ny().leggTilBeregningsgrunnlagPeriode(grunnlagPeriode1);
        return inkluderePeriode2 ? builder.leggTilBeregningsgrunnlagPeriode(grunnlagPeriode2).build() : builder.build();
    }
}