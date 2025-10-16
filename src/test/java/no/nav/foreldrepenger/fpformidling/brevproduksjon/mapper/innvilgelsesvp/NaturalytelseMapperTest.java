package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse.NaturalytelseStatus;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.PeriodeÅrsakDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

class NaturalytelseMapperTest {

    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().minusDays(1);
    private static final LocalDate PERIODE2_FOM = LocalDate.now();
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final String ARBEIDSGIVER_NAVN = "Arbeidsgiver AS";
    private static final String ARBEIDSGIVER_ORGNR = "1";
    private static final long DAGSATS = 1000L;

    @Test
    void skal_ikke_utlede_naturalytelse_som_starter_med_første_periode() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, false);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, BigDecimal.valueOf(1000), null, of(PeriodeÅrsakDto.NATURALYTELSE_BORTFALT),
            false);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, s -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isEmpty();
    }

    @Test
    void skal_utlede_at_naturalytelse_bortfaller_når_årsak_er_bortfaller() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, true);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, BigDecimal.valueOf(1000), null, of(PeriodeÅrsakDto.NATURALYTELSE_BORTFALT),
            true);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, s -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.BORTFALLER);
        assertThat(resultat.get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_NAVN);
        assertThat(resultat.get(0).getNyDagsats()).isEqualTo(DAGSATS);
        assertThat(resultat.get(0).getEndringsdato()).isEqualTo(formaterDatoNorsk(PERIODE2_FOM));
    }

    @Test
    void skal_utlede_at_naturalytelse_tilkommer_når_årsak_er_tilkommer() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, true);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, null, BigDecimal.valueOf(1000),
            of(PeriodeÅrsakDto.NATURALYTELSE_TILKOMMER), true);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, _ -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    @Test
    void skal_utlede_at_naturalytelse_bortfaller_når_årsak_ikke_er_angitt_og_bare_bortfaller_er_angitt() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, true);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, BigDecimal.valueOf(1000), null, of(), true);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, _ -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.BORTFALLER);
    }

    @Test
    void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bare_tilkommer_er_angitt() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, true);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, null, BigDecimal.valueOf(1000), of(), true);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, _ -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    @Test
    void skal_utlede_at_naturalytelse_bortfaller_når_årsak_ikke_er_angitt_og_bortfaller_er_større_enn_tilkommer() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, true);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, BigDecimal.valueOf(1000), BigDecimal.valueOf(500), of(), true);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, _ -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.BORTFALLER);
    }

    @Test
    void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bortfaller_er_lik_tilkommer() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, true);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), of(), true);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, _ -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    @Test
    void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bortfaller_er_mindre_enn_tilkommer() {
        // Arrange
        var tilkjentYtelseFP = gettilkjentYtelseFP(ARBEIDSGIVER_ORGNR, true);
        var beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER_ORGNR, BigDecimal.valueOf(500), BigDecimal.valueOf(1000), of(), true);

        // Act
        var resultat = NaturalytelseMapper.mapNaturalytelser(tilkjentYtelseFP, beregningsgrunnlag, Språkkode.NB, _ -> ARBEIDSGIVER_NAVN);

        // Assert
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getStatus()).isEqualTo(NaturalytelseStatus.TILKOMMER);
    }

    private TilkjentYtelseDagytelseDto gettilkjentYtelseFP(String orgnr, boolean inkluderePeriode2) {
        var andel = new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto(orgnr, 0, 10, TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, null,
            null, null);

        var resultatPeriode1 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE1_FOM, PERIODE1_TOM, (int) DAGSATS, of(andel));
        var resultatPeriode2 = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(PERIODE2_FOM, PERIODE2_TOM, (int) DAGSATS, of(andel));

        var perioder = new ArrayList<TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto>();
        perioder.add(resultatPeriode1);
        if (inkluderePeriode2) {
            perioder.add(resultatPeriode2);
        }
        return new TilkjentYtelseDagytelseDto(perioder);
    }

    private BeregningsgrunnlagDto getBeregningsgrunnlag(String orgnr,
                                                        BigDecimal naturalytelseBortfaller,
                                                        BigDecimal naturalytelseTilkommer,
                                                        List<PeriodeÅrsakDto> periodeÅrsaker,
                                                        boolean inkluderePeriode2) {
        var andel = new BeregningsgrunnlagAndelDto(DAGSATS, AktivitetStatusDto.ARBEIDSTAKER, null, null, false, OpptjeningAktivitetDto.ARBEID,
            PERIODE1_FOM, PERIODE2_TOM, new BgAndelArbeidsforholdDto(orgnr, null, naturalytelseBortfaller, naturalytelseTilkommer), false);
        var periode1 = new BeregningsgrunnlagPeriodeDto(DAGSATS, null, null, periodeÅrsaker, PERIODE1_FOM, PERIODE1_TOM, List.of(andel));
        var periode2 = new BeregningsgrunnlagPeriodeDto(DAGSATS, null, null, periodeÅrsaker, PERIODE2_FOM, PERIODE2_TOM, List.of(andel));

        var perioder = new ArrayList<BeregningsgrunnlagPeriodeDto>();
        perioder.add(periode1);
        if (inkluderePeriode2) {
            perioder.add(periode2);
        }
        return new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), null, BigDecimal.valueOf(DAGSATS * 6), perioder, false, false);
    }
}
