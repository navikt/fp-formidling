package no.nav.foreldrepenger.melding.typer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;

public class DatoTest {

    @Test
    public void skal_formatere_dato_med_norsk_format_for_februar() {
        // Arrange
        LocalDate dato = LocalDate.of(2021, 2, 10);

        // Act
        String resultat = Dato.formaterDatoNorsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("10. februar 2021");
    }

    @Test
    public void skal_formatere_dato_med_norsk_format_for_juli() {
        // Arrange
        LocalDate dato = LocalDate.of(2021, 7, 15);

        // Act
        String resultat = Dato.formaterDatoNorsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("15. juli 2021");
    }

    @Test
    public void skal_formatere_dato_med_norsk_format_for_desember() {
        // Arrange
        LocalDate dato = LocalDate.of(2021, 12, 2);

        // Act
        String resultat = Dato.formaterDatoNorsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("2. desember 2021");
    }

    @Test
    public void skal_formatere_dato_med_engelsk_format() {
        // Arrange
        LocalDate dato = LocalDate.of(2021, 2, 10);

        // Act
        String resultat = Dato.formaterDatoEngelsk(dato);

        // Assert
        assertThat(resultat).isEqualTo("10th of February 2021");
    }

    @Test
    public void skal_formatere_dato_med_formatet_som_er_angitt_eksplisitt() {
        // Arrange
        LocalDate dato = LocalDate.of(2021, 9, 14);

        // Act
        String resultatNo = Dato.formaterDato(dato, Språkkode.NB);
        String resultatEn = Dato.formaterDato(dato, Språkkode.EN);

        // Assert
        assertThat(resultatNo).isEqualTo("14. september 2021");
        assertThat(resultatEn).isEqualTo("14th of September 2021");
    }
}