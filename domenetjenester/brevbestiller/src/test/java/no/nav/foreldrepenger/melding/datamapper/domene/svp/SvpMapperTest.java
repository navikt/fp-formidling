package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static com.google.common.collect.ImmutableList.of;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class SvpMapperTest {

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
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(false);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, false);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), null, of(PeriodeÅrsak.NATURALYTELSE_BORTFALT.getKode()), false);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isEmpty();
    }

    @Test
    public void skal_utlede_at_naturalytelse_bortfaller_når_årsak_er_bortfaller() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(true);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), null, of(PeriodeÅrsak.NATURALYTELSE_BORTFALT.getKode()), true);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isNotNull();
        assertThat(naturalytelse).hasSize(1);
        assertThat(((boolean)naturalytelse.stream().findFirst().get().get(SvpMapper.BORTFALLER))).isTrue();
        assertThat(naturalytelse.stream().findFirst().get().get(SvpMapper.TILKOMMER)).isNull();
        assertThat(((String)naturalytelse.stream().findFirst().get().get("arbeidsgiverNavn"))).isEqualTo(ARBEIDSGIVER_NAVN);
        assertThat(((long)naturalytelse.stream().findFirst().get().get("nyDagsats"))).isEqualTo(DAGSATS);
        assertThat(((Dato)naturalytelse.stream().findFirst().get().get("endringsDato"))).isEqualTo(Dato.medFormatering(PERIODE2_FOM));
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_er_tilkommer() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(true);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, null, BigDecimal.valueOf(1000), of(PeriodeÅrsak.NATURALYTELSE_TILKOMMER.getKode()), true);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isNotNull();
        assertThat(naturalytelse).hasSize(1);
        assertThat(naturalytelse.stream().findFirst().get().get(SvpMapper.BORTFALLER)).isNull();
        assertThat(((boolean)naturalytelse.stream().findFirst().get().get(SvpMapper.TILKOMMER))).isTrue();
    }

    @Test
    public void skal_utlede_at_naturalytelse_bortfaller_når_årsak_ikke_er_angitt_og_bare_bortfaller_er_angitt() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(true);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), null, of(), true);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isNotNull();
        assertThat(naturalytelse).hasSize(1);
        assertThat(((boolean)naturalytelse.stream().findFirst().get().get(SvpMapper.BORTFALLER))).isTrue();
        assertThat(naturalytelse.stream().findFirst().get().get(SvpMapper.TILKOMMER)).isNull();
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bare_tilkommer_er_angitt() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(true);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, null, BigDecimal.valueOf(1000), of(), true);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isNotNull();
        assertThat(naturalytelse).hasSize(1);
        assertThat(naturalytelse.stream().findFirst().get().get(SvpMapper.BORTFALLER)).isNull();
        assertThat(((boolean)naturalytelse.stream().findFirst().get().get(SvpMapper.TILKOMMER))).isTrue();
    }

    @Test
    public void skal_utlede_at_naturalytelse_bortfaller_når_årsak_ikke_er_angitt_og_bortfaller_er_større_enn_tilkommer() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(true);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), BigDecimal.valueOf(500), of(), true);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isNotNull();
        assertThat(naturalytelse).hasSize(1);
        assertThat(((boolean)naturalytelse.stream().findFirst().get().get(SvpMapper.BORTFALLER))).isTrue();
        assertThat(naturalytelse.stream().findFirst().get().get(SvpMapper.TILKOMMER)).isNull();
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bortfaller_er_lik_tilkommer() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(true);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), of(), true);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isNotNull();
        assertThat(naturalytelse).hasSize(1);
        assertThat(naturalytelse.stream().findFirst().get().get(SvpMapper.BORTFALLER)).isNull();
        assertThat(((boolean)naturalytelse.stream().findFirst().get().get(SvpMapper.TILKOMMER))).isTrue();
    }

    @Test
    public void skal_utlede_at_naturalytelse_tilkommer_når_årsak_ikke_er_angitt_og_bortfaller_er_mindre_enn_tilkommer() {
        // Arrange
        SvpUttaksresultat svpUttaksresultat = getSvpUttaksresultat(true);
        BeregningsresultatFP beregningsresultatFP = getBeregningsresultatFP(ARBEIDSGIVER, true);
        Beregningsgrunnlag beregningsgrunnlag = getBeregningsgrunnlag(ARBEIDSGIVER, BigDecimal.valueOf(500), BigDecimal.valueOf(1000), of(), true);

        // Act
        Map<String, Object> resultat = SvpMapper.mapPerioder(svpUttaksresultat, beregningsresultatFP, beregningsgrunnlag);

        // Assert
        Set<Map> naturalytelse = (Set<Map>) resultat.get("naturalytelse");
        assertThat(naturalytelse).isNotNull();
        assertThat(naturalytelse).hasSize(1);
        assertThat(naturalytelse.stream().findFirst().get().get(SvpMapper.BORTFALLER)).isNull();
        assertThat(((boolean)naturalytelse.stream().findFirst().get().get(SvpMapper.TILKOMMER))).isTrue();
    }

    private SvpUttaksresultat getSvpUttaksresultat(boolean inkluderePeriode2) {
        SvpUttakResultatPeriode uttakPeriode1 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        SvpUttakResultatPeriode uttakPeriode2 = SvpUttakResultatPeriode.Builder.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        SvpUttakResultatArbeidsforhold svpUttakResultatArbeidsforhold = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(inkluderePeriode2 ? of(uttakPeriode1, uttakPeriode2) : of(uttakPeriode1))
                .build();
        SvpUttaksresultat svpUttaksresultat = SvpUttaksresultat.Builder.ny()
                .medUttakResultatArbeidsforhold(svpUttakResultatArbeidsforhold)
                .build();
        return svpUttaksresultat;
    }

    private BeregningsresultatFP getBeregningsresultatFP(Arbeidsgiver arbeidsgiver, boolean inkluderePeriode2) {
        BeregningsresultatAndel andel = BeregningsresultatAndel.ny()
                .medArbeidsgiver(arbeidsgiver)
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .build();
        BeregningsresultatPeriode resultatPeriode1 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(andel))
                .build();
        BeregningsresultatPeriode resultatPeriode2 = BeregningsresultatPeriode.ny()
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medDagsats(DAGSATS)
                .medBeregningsresultatAndel(of(andel))
                .build();
        return BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(inkluderePeriode2 ? of(resultatPeriode1, resultatPeriode2) : of(resultatPeriode1))
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