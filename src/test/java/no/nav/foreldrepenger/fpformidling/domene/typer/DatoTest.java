package no.nav.foreldrepenger.fpformidling.domene.typer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;

class DatoTest {

    @Test
    void skal_formatere_dato_med_norsk_format_for_februar() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 10);

        // Act
        var resultat = Dato.formaterDatoNorsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("10. februar 2021");
    }

    @Test
    void skal_formatere_dato_med_norsk_format_for_juli() {
        // Arrange
        var dato = LocalDate.of(2021, 7, 15);

        // Act
        var resultat = Dato.formaterDatoNorsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("15. juli 2021");
    }

    @Test
    void skal_formatere_dato_med_norsk_format_for_desember() {
        // Arrange
        var dato = LocalDate.of(2021, 12, 2);

        // Act
        var resultat = Dato.formaterDatoNorsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("2. desember 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 10);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("10th of February 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format_1st() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 1);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("1st of February 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format_2nd() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 2);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("2nd of February 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format_3rd() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 3);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("3rd of February 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format_11th() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 11);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("11th of February 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format_13th() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 13);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("13th of February 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format_31st() {
        // Arrange
        var dato = LocalDate.of(2021, 3, 31);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("31st of March 2021");
    }

    @Test
    void skal_formatere_dato_med_engelsk_format_22nd() {
        // Arrange
        var dato = LocalDate.of(2021, 2, 22);

        // Act
        var resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("22nd of February 2021");
    }

    @Test
    void skal_formatere_dato_med_formatet_som_er_angitt_eksplisitt() {
        // Arrange
        var dato = LocalDate.of(2021, 9, 14);

        // Act
        var resultatNo = Dato.formaterDato(dato, Språkkode.NB);
        var resultatEn = Dato.formaterDato(dato, Språkkode.EN);

        // Assert
        assertThat(resultatNo).isEqualTo("14. september 2021");
        assertThat(resultatEn).isEqualTo("14th of September 2021");
    }
}
