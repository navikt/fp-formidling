package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DatoVerktøyTest {

    @Test
    void skal_finne_at_dato_2_er_rett_etter_dato_1() {
        // Act
        var resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.now().plusDays(5), LocalDate.now().plusDays(6));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_finne_at_dato_2_er_rett_etter_dato_1_når_det_er_en_hel_helg_imellom() {
        // Act
        var resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.of(2021, 12, 17), LocalDate.of(2021, 12, 20));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_finne_at_dato_2_er_rett_etter_dato_1_når_det_er_en_søndag_imellom() {
        // Act
        var resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.of(2021, 12, 18), LocalDate.of(2021, 12, 20));

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    void skal_finne_at_dato_2_ikke_er_rett_etter_dato_1() {
        // Act
        var resultat = DatoVerktøy.erFomRettEtterTomDato(LocalDate.now().plusDays(3), LocalDate.now().plusDays(8));

        // Assert
        assertThat(resultat).isFalse();
    }
}
