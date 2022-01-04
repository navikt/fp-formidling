package no.nav.foreldrepenger.melding.brevmapper.brev.felles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DatoVerktøyTest {

    @Test
    public void skal_finne_at_dato_2_er_rett_etter_dato_1() {
        // Act
        boolean resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.now().plusDays(5), LocalDate.now().plusDays(6));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_finne_at_dato_2_er_rett_etter_dato_1_når_det_er_en_hel_helg_imellom() {
        // Act
        boolean resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.of(2021, 12, 17), LocalDate.of(2021, 12, 20));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_finne_at_dato_2_er_rett_etter_dato_1_når_det_er_en_søndag_imellom() {
        // Act
        boolean resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.of(2021, 12, 18), LocalDate.of(2021, 12, 20));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_finne_at_dato_2_ikke_er_rett_etter_dato_1() {
        // Act
        boolean resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.now().plusDays(3), LocalDate.now().plusDays(8));

        // Assert
        assertThat(resultat).isFalse();
    }
}