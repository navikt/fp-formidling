package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;


import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class OpphørPeriodeMergerTest {
    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(20);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(30);

    @Test
    public void skal_slå_sammen_perioder_som_er_sammenhengende() {
        // Arrange
        OpphørPeriode periode1 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).build();
        OpphørPeriode periode2 = OpphørPeriode.ny().medPeriodeFom(PERIODE2_FOM, Språkkode.NB).medPeriodeTom(PERIODE2_TOM, Språkkode.NB).build();
        OpphørPeriode periode3 = OpphørPeriode.ny().medPeriodeFom(PERIODE3_FOM, Språkkode.NB).medPeriodeTom(PERIODE3_TOM, Språkkode.NB).build();

        // Act
        List<OpphørPeriode> resultat = OpphørPeriodeMerger.mergePerioder(asList(periode1, periode2, periode3));

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getStønadsperiodeFomDate()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getStønadsperiodeTomDate()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(1).getStønadsperiodeFomDate()).isEqualTo(PERIODE3_FOM);
        assertThat(resultat.get(1).getStønadsperiodeTomDate()).isEqualTo(PERIODE3_TOM);
    }

    @Test
    public void skal_slå_sammen_perioder_som_er_Like_og_har_samme_årsak() {
        // Arrange
        OpphørPeriode periode1 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medÅrsak(Årsak.of(PeriodeIkkeOppfyltÅrsak._8309.getKode())).build();
        OpphørPeriode periode2 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medÅrsak(Årsak.of(PeriodeIkkeOppfyltÅrsak._8309.getKode())).build();
        OpphørPeriode periode3 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medÅrsak(Årsak.of(PeriodeIkkeOppfyltÅrsak._8309.getKode())).build();

        // Act
        List<OpphørPeriode> resultat = OpphørPeriodeMerger.mergePerioder(asList(periode1, periode2, periode3));

        // Assert
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStønadsperiodeFomDate()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getStønadsperiodeTomDate()).isEqualTo(PERIODE1_TOM);
        assertThat(resultat.get(0).getÅrsak()).isEqualTo(Årsak.of("8309"));
    }

    @Test
    public void skal_ikke_slå_sammen_perioder_som_er_Like_med_ulik_årsak() {
        // Arrange
        OpphørPeriode periode1 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medÅrsak(Årsak.of(PeriodeIkkeOppfyltÅrsak._8309.getKode())).build();
        OpphørPeriode periode2 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medÅrsak(Årsak.of(PeriodeIkkeOppfyltÅrsak._8310.getKode())).build();

        // Act
        List<OpphørPeriode> resultat = OpphørPeriodeMerger.mergePerioder(asList(periode1, periode2));

        // Assert
        assertThat(resultat).hasSize(2);
    }

    @Test
    public void skal_ikke_legge_til_arbeidsgivere_som_allerede_finnes() {
        // Arrange
        String arbNavn1 = "Arbeidsgiver 1 AS";
        String arbNavn2 = "Arbeidsgiver 2 AS";
        String arbNavn3 = "Arbeidsgiver 3 AS";
        OpphørPeriode periode1 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medArbeidsgivere(List.of(arbNavn1, arbNavn2)).build();
        OpphørPeriode periode2 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medArbeidsgivere(List.of(arbNavn1, arbNavn2)).build();
        OpphørPeriode periode3 = OpphørPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medArbeidsgivere(List.of(arbNavn3)).build();

        // Act
        List<OpphørPeriode> resultat = OpphørPeriodeMerger.mergePerioder(asList(periode1, periode2, periode3));

        // Assert
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStønadsperiodeFomDate()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getStønadsperiodeTomDate()).isEqualTo(PERIODE1_TOM);
        assertThat(resultat.get(0).getArbeidsgivere()).hasSize(3);
        assertThat(resultat.get(0).getArbeidsgivere().get(0)).isEqualTo(arbNavn1);
        assertThat(resultat.get(0).getArbeidsgivere().get(1)).isEqualTo(arbNavn2);
        assertThat(resultat.get(0).getArbeidsgivere().get(2)).isEqualTo(arbNavn3);
    }


}