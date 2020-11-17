package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;

public class SvpUtledAvslagPerioderTest {

    private static final LocalDate DAGENS_DATO = LocalDate.now();

    @Test
    public void skal_returnere_en_tom_liste_når_input_er_tom() {

        // Arrange
        List<SvpUttakResultatArbeidsforhold> avslagArbeidsforhold = List.of();

        // Act
        Set<SvpAvslagPeriode> avslagPerioder = SvpUtledAvslagPerioder.utled(avslagArbeidsforhold);

        // Assert
        assertThat(avslagPerioder).hasSize(0);

    }

    @Test
    public void skal_returnere_en_tom_liste_når_URA_kun_har_innvilget_perioder() {

        // Arrange
        SvpUttakResultatPeriode periode1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO,
                        DAGENS_DATO.plusWeeks(1)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.INGEN)
                .build();

        SvpUttakResultatPeriode periode2 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO.plusWeeks(1),
                        DAGENS_DATO.plusWeeks(2)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.INGEN)
                .build();

        List<SvpUttakResultatPeriode> perioder = List.of(periode1, periode2);

        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(perioder)
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.INGEN)
                .build();

        List<SvpUttakResultatArbeidsforhold> avslagArbeidsforhold = List.of(ura);

        // Act
        Set<SvpAvslagPeriode> avslagPerioder = SvpUtledAvslagPerioder.utled(avslagArbeidsforhold);

        // Assert
        assertThat(avslagPerioder).hasSize(0);

    }

    @Test
    public void skal_returnere_en_tom_liste_når_URA_kun_har_perioder_med_årsaker_som_ikke_er_relevante() {

        // Arrange
        SvpUttakResultatPeriode periode1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO,
                        DAGENS_DATO.plusWeeks(1)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.OPPTJENINGSVILKÅRET_IKKE_OPPFYLT)
                .build();

        SvpUttakResultatPeriode periode2 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO.plusWeeks(1),
                        DAGENS_DATO.plusWeeks(2)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.OPPTJENINGSVILKÅRET_IKKE_OPPFYLT)
                .build();

        List<SvpUttakResultatPeriode> perioder = List.of(periode1, periode2);

        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(perioder)
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.INGEN)
                .build();

        List<SvpUttakResultatArbeidsforhold> avslagArbeidsforhold = List.of(ura);

        // Act
        Set<SvpAvslagPeriode> avslagPerioder = SvpUtledAvslagPerioder.utled(avslagArbeidsforhold);

        // Assert
        assertThat(avslagPerioder).hasSize(0);

    }

    @Test
    public void skal_filterer_og_slå_sammen_perioder_fra_2_URAer() {

        // Arrange
        SvpUttakResultatPeriode periode_A1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO,
                        DAGENS_DATO.plusWeeks(1)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();

        SvpUttakResultatPeriode periode_A2 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO.plusWeeks(1),
                        DAGENS_DATO.plusWeeks(2)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();

        List<SvpUttakResultatPeriode> perioder_A = List.of(periode_A1, periode_A2);

        SvpUttakResultatArbeidsforhold ura_A = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(perioder_A)
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.INGEN)
                .build();

        SvpUttakResultatPeriode periode_B1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO.plusWeeks(5),
                        DAGENS_DATO.plusWeeks(6)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .build();

        SvpUttakResultatPeriode periode_B2 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO.plusWeeks(6),
                        DAGENS_DATO.plusWeeks(7)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .build();

        SvpUttakResultatPeriode periode_B3 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO.plusWeeks(5),
                        DAGENS_DATO.plusWeeks(7)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKER_ER_DØD)
                .build();

        List<SvpUttakResultatPeriode> perioder_B = List.of(periode_B1, periode_B2, periode_B3);

        SvpUttakResultatArbeidsforhold ura_B = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(perioder_B)
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.INGEN)
                .build();

        List<SvpUttakResultatArbeidsforhold> avslagArbeidsforhold = List.of(ura_A, ura_B);

        // Act
        Set<SvpAvslagPeriode> avslagPerioder = SvpUtledAvslagPerioder.utled(avslagArbeidsforhold);

        // Assert
        assertThat(avslagPerioder).hasSize(2);
        assertThat(avslagPerioder).anySatisfy(p -> {
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
            assertThat(p.getFom().toLocalDate()).isEqualTo(DAGENS_DATO);
            assertThat(p.getTom().toLocalDate()).isEqualTo(DAGENS_DATO.plusWeeks(2));
        });
        assertThat(avslagPerioder).anySatisfy(p -> {
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE.getKode()));
            assertThat(p.getFom().toLocalDate()).isEqualTo(DAGENS_DATO.plusWeeks(5));
            assertThat(p.getTom().toLocalDate()).isEqualTo(DAGENS_DATO.plusWeeks(7));
        });

    }


    @Test
    public void skal_slå_sammen_to_identiske_perioder() {

        // Arrange
        SvpUttakResultatPeriode periode_A1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO,
                        DAGENS_DATO.plusWeeks(1)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();

        List<SvpUttakResultatPeriode> perioder_A = List.of(periode_A1);

        SvpUttakResultatArbeidsforhold ura_A = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(perioder_A)
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.INGEN)
                .build();

        SvpUttakResultatPeriode periode_B1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(
                        DAGENS_DATO,
                        DAGENS_DATO.plusWeeks(1)))
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();

        List<SvpUttakResultatPeriode> perioder_B = List.of(periode_B1);

        SvpUttakResultatArbeidsforhold ura_B = SvpUttakResultatArbeidsforhold.Builder.ny()
                .leggTilPerioder(perioder_B)
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.INGEN)
                .build();

        List<SvpUttakResultatArbeidsforhold> avslagArbeidsforhold = List.of(ura_A, ura_B);

        // Act
        Set<SvpAvslagPeriode> avslagPerioder = SvpUtledAvslagPerioder.utled(avslagArbeidsforhold);

        // Assert
        assertThat(avslagPerioder).hasSize(1);
        assertThat(avslagPerioder).anySatisfy(p -> {
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
            assertThat(p.getFom().toLocalDate()).isEqualTo(DAGENS_DATO);
            assertThat(p.getTom().toLocalDate()).isEqualTo(DAGENS_DATO.plusWeeks(1));
        });

    }

}
